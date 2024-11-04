#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <chrono>

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

#include <omp.h>
#include <cstdio>

const int KERNEL_RADIUS = 8;
const float sigma = 3.f;

unsigned char blur(int x, int y, int channel, unsigned char* input, int width, int height)
{
	float sum_weight = 0.0f;
	float ret = 0.f;

	for (int offset_y = -KERNEL_RADIUS; offset_y <= KERNEL_RADIUS; offset_y++)
	{
		for (int offset_x = -KERNEL_RADIUS; offset_x <= KERNEL_RADIUS; offset_x++)
		{
			int pixel_y = std::max(std::min(y + offset_y, height - 1), 0);
			int pixel_x = std::max(std::min(x + offset_x, width - 1), 0);
			int pixel = pixel_y * width + pixel_x;

			float weight = std::exp(-(offset_x * offset_x + offset_y * offset_y) / (2.f * sigma * sigma));

			ret += weight * input[4 * pixel + channel];
			sum_weight += weight;
		}
	}
	ret /= sum_weight;

	return (unsigned char)std::max(std::min(ret, 255.f), 0.f);
}

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

void gaussian_blur_separate_serial(const char* filename)
{
	int width = 0;
	int height = 0;
	int img_orig_channels = 4;
	// Load an image into an array of unsigned chars that is the size of width * height * number of channels. The channels are the Red, Green, Blue and Alpha channels of the image.
	unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
	if (img_in == nullptr)
	{
		printf("Could not load %s\n", filename);
		return;
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
	printf("Gaussian Blur Separate - Serial: Time %dms\n", time);

	// Write the blurred image into a JPG file
	stbi_write_jpg("blurred_separate.jpg", width, height, 4/*channels*/, img_out, 90 /*quality*/);

	stbi_image_free(img_in);
	delete[] img_horizontal_blur;
	delete[] img_out;
}

void gaussian_blur_separate_parallel(const char* filename)
{
	int width = 0;
	int height = 0;
	int img_orig_channels = 4;
	// Load an image into an array of unsigned chars that is the size of width * height * number of channels.
	unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
	if (img_in == nullptr)
	{
		printf("Could not load %s\n", filename);
		return;
	}

	unsigned char* img_horizontal_blur = new unsigned char[width * height * 4];
	unsigned char* img_out = new unsigned char[width * height * 4];

	// Timer to measure performance
	auto start = std::chrono::high_resolution_clock::now();

	// The variables y, x, channel are already private for each thread so there is no need to define them

	// Horizontal Blur
	#pragma omp parallel for collapse(3) schedule(static)
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
	#pragma omp parallel for collapse(3) schedule(static)
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
	printf("Gaussian Blur Separate - Parallel: Time %dms\n", time);

	// Write the blurred image into a JPG file
	stbi_write_jpg("blurred_image_parallel.jpg", width, height, 4/*channels*/, img_out, 90 /*quality*/);

	stbi_image_free(img_in);
	delete[] img_horizontal_blur;
	delete[] img_out;
}

void bloom_parallel(const char* filename) {
	int width = 0;
	int height = 0;
	int img_orig_channels = 4;
	// Load an image into an array of unsigned chars that is the size of width * height * number of channels.
	unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
	if (img_in == nullptr)
	{
		printf("Could not load %s\n", filename);
		return;
	}
	float* luminance_array = new float[width * height];
	float max_luminance = 0.0f;
	// Define the bloom_mask array
	unsigned char* bloom_mask = new unsigned char[width * height * 4];
	unsigned char* img_lum = new unsigned char[width * height * 4];


	// Timer to measure performance
	auto start = std::chrono::high_resolution_clock::now();

	// Calculate luminance and find max luminance in parallel
	#pragma omp parallel
	{
		// Calculate luminance for each pixel
		#pragma omp for collapse(2) schedule(static)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = y * width + x;
				unsigned char red = img_in[4 * pixel + 0];
				unsigned char green = img_in[4 * pixel + 1];
				unsigned char blue = img_in[4 * pixel + 2];
				float luminance = (red + green + blue) / 3.0f;
				luminance_array[pixel] = luminance;
			}
		}

		// Find maximum luminance
		#pragma omp for reduction(max:max_luminance)
		for (int i = 0; i < width * height; i++) {
			if (luminance_array[i] > max_luminance) {
				max_luminance = luminance_array[i];
			}
		}

		std::cout << "Max luminance: " << max_luminance << std::endl;

		// Create bloom_mask
		#pragma omp for collapse(3) schedule(static)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int channel = 0; channel < 4; channel++) {
					int pixel = (y * width + x);
					if (luminance_array[pixel] > 0.9 * max_luminance) {
						// Keep the original color because it is higher than 90%
						bloom_mask[pixel * 4 + channel] = img_in[pixel * 4 + channel];
					}
					else {
						// Set to black -> 0
						bloom_mask[pixel * 4 + channel] = 0;
					}
				}
			}
		}

		// Horizontal Blur
		#pragma omp for collapse(3) schedule(static)
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				for (int channel = 0; channel < 4; channel++)
				{
					int pixel = (y * width + x) * 4 + channel;
					bloom_mask[pixel] = blurAxis(x, y, channel, 0, bloom_mask, width, height);
				}
			}
		}

		// Vertical Blur
		#pragma omp for collapse(3) schedule(static)
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				for (int channel = 0; channel < 4; channel++)
				{
					int pixel = (y * width + x) * 4 + channel;
					bloom_mask[pixel] = blurAxis(x, y, channel, 1, bloom_mask, width, height);
				}
			}
		}

		// Write the blurred image to a file (only from one thread)
		#pragma omp single nowait
		{
			stbi_write_jpg("bloom_blurred.jpg", width, height, 4, bloom_mask, 90);
		}

		// Final Image Creation
		#pragma omp for collapse(3) schedule(static)
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				for (int channel = 0; channel < 4; channel++) {
					int pixel = (y * width + x) * 4 + channel;
					// Combine colors of original and blurred images
					img_lum[pixel] = img_in[pixel] + bloom_mask[pixel];
					// Ensure maximum value does not exceed 255
					if (img_in[pixel] + bloom_mask[pixel] > 255) {
						img_lum[pixel] = 255;
					}
					else {
						img_lum[pixel] = img_in[pixel] + bloom_mask[pixel];
					}
				}
			}
		}

		// Write the final image to a file (only from one thread)
		#pragma omp single nowait
		{
			stbi_write_jpg("bloom_final.jpg", width, height, 4, img_lum, 90);
		}
	}

	// Timer to measure performance
	auto end = std::chrono::high_resolution_clock::now();
	// Computation time in milliseconds
	int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
	printf("Bloom Parallel: Time %dms\n", time);

	delete[] bloom_mask;
	delete[] luminance_array;
	delete[] img_in;
	delete[] img_lum;
}

int main()
{
	// Do not forget to mention that in order to activate the OpenMP in Visual Studio we had to:
	// 1. Open Project -> Properties
	// 2. Navigate to "Configuration Properties" > "C/C++" > "Language"
	// 3. Set "OpenMP Support" to "Yes"
	// 4. Click "Apply" or "Ok" to save the changes

	const char* filename = "street_night.jpg";
	gaussian_blur_separate_serial(filename);
	gaussian_blur_separate_parallel(filename);
	bloom_parallel(filename);

	return 0;
}