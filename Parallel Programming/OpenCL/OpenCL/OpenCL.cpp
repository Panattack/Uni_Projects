#include <CL/cl.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <chrono>

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

// The kernel source as a global variable
const char* kernel_source = R"(
#define KERNEL_RADIUS 8
__kernel void gaussian_blur(__global const uchar* input, __global uchar* output, __global const float* weights, int width, int height, int axis) {
    int x = get_global_id(0);
    int y = get_global_id(1);
    int c = get_global_id(2);
    float sum_weight = 0.0f;
    float ret = 0.0f;
    for (int offset = -KERNEL_RADIUS; offset <= KERNEL_RADIUS; offset++) {
        int offset_x = axis == 0 ? offset : 0;
        int offset_y = axis == 1 ? offset : 0;
        int pixel_x = clamp(x + offset_x, 0, width - 1);
        int pixel_y = clamp(y + offset_y, 0, height - 1);
        int pixel_index = (pixel_y * width + pixel_x) * 4 + c;

        float weight = weights[offset + KERNEL_RADIUS];

        ret += weight * input[pixel_index];
        sum_weight += weight;
    }

    int output_index = (y * width + x) * 4 + c;
    output[output_index] = (uchar)clamp(ret / sum_weight, 0.0f, 255.0f);
}
)";

// Helper function to get the OpenCL device
cl_device_id get_device() {
    cl_uint platform_count;
    clGetPlatformIDs(0, nullptr, &platform_count);
    std::vector<cl_platform_id> platforms(platform_count);
    clGetPlatformIDs(platform_count, platforms.data(), nullptr);

    cl_device_id device_id;
    for (auto platform : platforms) {
        cl_uint device_count;
        clGetDeviceIDs(platform, CL_DEVICE_TYPE_GPU, 1, &device_id, &device_count);
        if (device_count > 0) {
            return device_id;
        }
    }

    // If no GPU device is found, use CPU
    for (auto platform : platforms) {
        cl_uint device_count;
        clGetDeviceIDs(platform, CL_DEVICE_TYPE_CPU, 1, &device_id, &device_count);
        if (device_count > 0) {
            return device_id;
        }
    }

    throw std::runtime_error("No suitable OpenCL device found.");
}

// Function to precompute Gaussian weights
std::vector<float> compute_gaussian_weights(int radius, float sigma) {
    std::vector<float> weights(radius * 2 + 1);
    float sum = 0.0;
    for (int i = -radius; i <= radius; i++) {
        float weight = exp(-(i * i) / (2.f * sigma * sigma));
        weights[i + radius] = weight;
    }
    return weights;
}

// The main function to perform Gaussian blur using OpenCL
int gaussian_blur_separate_parallel(const char* filename, std::tuple<int, int, int> combination) {
    // Load the image
    int width, height;
    int channels = 4;
    unsigned char* img_in = stbi_load(filename, &width, &height, &channels, 4);
    if (!img_in) {
        std::cerr << "Failed to load image\n";
        return 0;
    }

    // Precompute Gaussian weights
    int radius = 8;
    float sigma = 3.0f;
    std::vector<float> weights = compute_gaussian_weights(radius, sigma);

    // Create OpenCL context and device
    cl_device_id device_id = get_device();
    cl_context context = clCreateContext(nullptr, 1, &device_id, nullptr, nullptr, nullptr);
    cl_command_queue queue = clCreateCommandQueue(context, device_id, 0, nullptr);

    // Save the kernel to a file
    std::ofstream kernel_file("kernel.cl");
    kernel_file << kernel_source;
    kernel_file.close();

    // Create and build the program
    const char* kernel_source_cstr = kernel_source;
    cl_program program = clCreateProgramWithSource(context, 1, &kernel_source_cstr, nullptr, nullptr);
    clBuildProgram(program, 1, &device_id, nullptr, nullptr, nullptr);

    // Create kernel
    cl_kernel kernel = clCreateKernel(program, "gaussian_blur", nullptr);

    // Tune local worksize
    size_t local_work_size[3] = { std::get<2>(combination), std::get<1>(combination), std::get<0>(combination) };
    //size_t max_work_group_size;
    //clGetKernelWorkGroupInfo(kernel, device_id, CL_KERNEL_WORK_GROUP_SIZE, sizeof(size_t), &max_work_group_size, nullptr);

    //size_t max_work_item_sizes[3];
    //clGetDeviceInfo(device_id, CL_DEVICE_MAX_WORK_ITEM_SIZES, sizeof(max_work_item_sizes), &max_work_item_sizes, nullptr);

    //size_t max_work_items_per_group;
    //clGetDeviceInfo(device_id, CL_DEVICE_MAX_WORK_GROUP_SIZE, sizeof(size_t), &max_work_items_per_group, nullptr);

    //std::cout << "Max work group size: " << max_work_group_size << std::endl;
    //std::cout << "Max work item sizes: [" << max_work_item_sizes[0] << ", " << max_work_item_sizes[1] << ", " << max_work_item_sizes[2] << "]" << std::endl;
    //std::cout << "Max work items per group: " << max_work_items_per_group << std::endl;

    // Allocate buffers
    cl_mem input_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, width * height * 4 * sizeof(unsigned char), img_in, nullptr);
    cl_mem output_buffer = clCreateBuffer(context, CL_MEM_WRITE_ONLY, width * height * 4 * sizeof(unsigned char), nullptr, nullptr);
    cl_mem weight_buffer = clCreateBuffer(context, CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR, weights.size() * sizeof(float), weights.data(), nullptr);

    unsigned char* img_horizontal_blur = new unsigned char[width * height * 4];

    // Set kernel arguments for horizontal blur
    clSetKernelArg(kernel, 0, sizeof(cl_mem), &input_buffer);
    clSetKernelArg(kernel, 1, sizeof(cl_mem), &output_buffer);
    clSetKernelArg(kernel, 2, sizeof(cl_mem), &weight_buffer);
    clSetKernelArg(kernel, 3, sizeof(int), &width);
    clSetKernelArg(kernel, 4, sizeof(int), &height);
    int axis = 0;
    clSetKernelArg(kernel, 5, sizeof(int), &axis);

    // Define the global work size (width * height * channels)
    channels = 4;
    size_t global_work_size[3] = { static_cast<size_t>(width), static_cast<size_t>(height), static_cast<size_t>(channels) };

    // Timer to measure performance
    auto start = std::chrono::high_resolution_clock::now();

    // Execute kernel for horizontal blur
    clEnqueueNDRangeKernel(queue, kernel, 3, nullptr, global_work_size, local_work_size, 0, nullptr, nullptr);
    clFinish(queue);  // Ensure the kernel execution is finished

    // Read the horizontal blur result back to host memory
    clEnqueueReadBuffer(queue, output_buffer, CL_TRUE, 0, width * height * 4 * sizeof(unsigned char), img_horizontal_blur, 0, nullptr, nullptr);

    // Prepare for vertical blur
    clSetKernelArg(kernel, 0, sizeof(cl_mem), &output_buffer);
    clSetKernelArg(kernel, 1, sizeof(cl_mem), &input_buffer);
    axis = 1;
    clSetKernelArg(kernel, 5, sizeof(int), &axis);

    // Execute kernel for vertical blur
    clEnqueueNDRangeKernel(queue, kernel, 3, nullptr, global_work_size, local_work_size, 0, nullptr, nullptr);
    clFinish(queue);  // Ensure the kernel execution is finished

    // Read the final result back to host memory
    clEnqueueReadBuffer(queue, input_buffer, CL_TRUE, 0, width * height * 4 * sizeof(unsigned char), img_in, 0, nullptr, nullptr);
    
    // Timer to measure performance
    auto end = std::chrono::high_resolution_clock::now();
    // Computation time in milliseconds
    int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
    //printf("Combination: %d %d %d\n", std::get<2>(combination), std::get<1>(combination), std::get<0>(combination));
    //printf("Gaussian Blur Separate - Parallel: Time %dms\n", time);

    // Write the blurred image into a JPG file
    stbi_write_jpg("image_blurred_final.jpg", width, height, 4, img_in, 90);

    // Cleanup
    clReleaseMemObject(input_buffer);
    clReleaseMemObject(output_buffer);
    clReleaseMemObject(weight_buffer);
    clReleaseKernel(kernel);
    clReleaseProgram(program);
    clReleaseCommandQueue(queue);
    clReleaseContext(context);
    stbi_image_free(img_in);
    delete[] img_horizontal_blur;
    return time;
}

const int KERNEL_RADIUS = 8;
const float sigma = 3.f;
unsigned char blurAxis(int x, int y, int channel, int axis/*0: horizontal axis, 1: vertical axis*/, unsigned char* input, int width, int height)
{
    float sum_weight = 0.0f;
    float ret = 0.f;

    for (int offset = -KERNEL_RADIUS; offset <= KERNEL_RADIUS; offset++)
    {
        int offset_x = axis == 0 ? offset : 0;
        int offset_y = axis == 1 ? offset : 0;
        int pixel_y = std::max(std::min(y + offset_y, height - 1), 0);
        int pixel_x = std::max(std::min(x + offset_x, width - 1), 0);
        int pixel = pixel_y * width + pixel_x;

        float weight = std::exp(-(offset * offset) / (2.f * sigma * sigma));

        ret += weight * input[4 * pixel + channel];
        sum_weight += weight;
    }
    ret /= sum_weight;

    return (unsigned char)std::max(std::min(ret, 255.f), 0.f);
}

int gaussian_blur_separate_serial(const char* filename)
{
    int width = 0;
    int height = 0;
    int img_orig_channels = 4;
    // Load an image into an array of unsigned chars that is the size of width * height * number of channels. The channels are the Red, Green, Blue and Alpha channels of the image.
    unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
    if (img_in == nullptr)
    {
        printf("Could not load %s\n", filename);
        return 0;
    }

    unsigned char* img_horizontal_blur = new unsigned char[width * height * 4];
    unsigned char* img_out = new unsigned char[width * height * 4];

    // Timer to measure performance
    auto start = std::chrono::high_resolution_clock::now();

    // Horizontal Blur
    for (int y = 0; y < height; y++)
    {
        for (int x = 0; x < width; x++)
        {
            int pixel = y * width + x;
            for (int channel = 0; channel < 4; channel++)
            {
                img_horizontal_blur[4 * pixel + channel] = blurAxis(x, y, channel, 0, img_in, width, height);
            }
        }
    }
    // Vertical Blur
    for (int y = 0; y < height; y++)
    {
        for (int x = 0; x < width; x++)
        {
            int pixel = y * width + x;
            for (int channel = 0; channel < 4; channel++)
            {
                img_out[4 * pixel + channel] = blurAxis(x, y, channel, 1, img_horizontal_blur, width, height);
            }
        }
    }
    // Timer to measure performance
    auto end = std::chrono::high_resolution_clock::now();
    // Computation time in milliseconds
    int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
    //printf("Gaussian Blur Separate - Serial: Time %dms\n", time);

    // Write the blurred image into a JPG file
    stbi_write_jpg("blurred_separate.jpg", width, height, 4/*channels*/, img_out, 90 /*quality*/);

    stbi_image_free(img_in);
    delete[] img_horizontal_blur;
    delete[] img_out;
    return time;
}

#include <limits> // for std::numeric_limits

int main() {
    const char* filename = "street_night.jpg";
    const int NUM_TESTS = 4; // Number of times to execute each test

    // Execute serial Gaussian blur four times and calculate average time
    std::cout << "Serial Gaussian blur times (4 executions) and average time:\n";
    std::vector<double> serialTimes;
    double totalSerialTime = 0.0;
    for (int i = 0; i < NUM_TESTS; ++i) {
        double executionTime = gaussian_blur_separate_serial(filename);
        totalSerialTime += executionTime;
        serialTimes.push_back(executionTime);
    }
    double avgSerialTime = totalSerialTime / NUM_TESTS;
    for (const auto& time : serialTimes) {
        std::cout << time << "ms ";
    }
    std::cout << " Average time: " << avgSerialTime << "ms\n";

    // Define a vector of tuples to store the combinations
    std::vector<std::tuple<int, int, int>> combinations = {
        {2, 4, 32},
        {2, 8, 16},
        {4, 4, 16},
        {2, 2, 64},
        {4, 8, 8},
        {1, 16, 16},
        {1, 8, 32},
        {1, 4, 64},
        {1, 2, 128}
    };

    // Execute parallel Gaussian blur for each combination, calculate average time, and print results
    std::cout << "\nParallel Gaussian blur times (4 executions) and average time for each combination:\n";
    std::tuple<int, int, int> minAvgCombination;
    double minAvgTime = std::numeric_limits<double>::max();
    for (const auto& combination : combinations) {
        std::cout << "Combination: " << std::get<0>(combination) << " "
            << std::get<1>(combination) << " "
            << std::get<2>(combination) << " : ";
        std::vector<double> times;
        double totalParallelTime = 0.0;
        for (int i = 0; i < NUM_TESTS; ++i) {
            double executionTime = gaussian_blur_separate_parallel(filename, combination);
            totalParallelTime += executionTime;
            times.push_back(executionTime);
        }
        double avgParallelTime = totalParallelTime / NUM_TESTS;
        if (avgParallelTime < minAvgTime) {
            minAvgTime = avgParallelTime;
            minAvgCombination = combination;
        }
        for (const auto& time : times) {
            std::cout << time << "ms ";
        }
        std::cout << " Average time: " << avgParallelTime << "ms\n";
    }

    // Print the combination with the minimum average time
    std::cout << "\nCombination with the minimum average time: "
        << std::get<0>(minAvgCombination) << " "
        << std::get<1>(minAvgCombination) << " "
        << std::get<2>(minAvgCombination) << " : "
        << minAvgTime << "ms\n";

    return 0;
}
