#define _CRT_SECURE_NO_WARNINGS
#include <iostream>
#include <chrono>

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION
#include "stb_image_write.h"

#include <array>
#include <thread>
#include <vector>
#include <barrier>

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

void gaussian_blur_serial(const char* filename)
{
	int width = 0;
	int height = 0;
	int img_orig_channels = 4;
	// Load an image into an array of unsigned chars that is the size of [width * height * number of channels]. The channels are the Red, Green, Blue and Alpha channels of the image.
	unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
	if (img_in == nullptr)
	{
		printf("Could not load %s\n", filename);
		return;
	}

	unsigned char* img_out = new unsigned char[width * height * 4];

	// Timer to measure performance
	auto start = std::chrono::high_resolution_clock::now();

	// Perform Gaussian Blur to each pixel
	for (int y = 0; y < height; y++)
	{
		for (int x = 0; x < width; x++)
		{
			int pixel = y * width + x;
			for (int channel = 0; channel < 4; channel++)
			{
				img_out[4 * pixel + channel] = blur(x, y, channel, img_in, width, height);
			}
		}
	}

	// Timer to measure performance
	auto end = std::chrono::high_resolution_clock::now();
	// Computation time in milliseconds
	int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
	printf("Gaussian Blur - Serial: Time %dms\n", time);

	// Write the blurred image into a JPG file
	stbi_write_jpg("blurred_image_serial.jpg", width, height, 4, img_out, 90 /*quality*/);

	stbi_image_free(img_in);
	delete[] img_out;
}
/// <summary>
/// Given a starting and an ending point, each thread blurs a portion of the image
/// </summary>
/// <param name="img_in">: An input image as an array of characters</param>
/// <param name="img_out">: A blurred image as an array of characters</param>
/// <param name="height">: The height of the image</param>
/// <param name="width">: The width of the image</param>
/// <param name="start_y">: The starting point of the image in this thread</param>
/// <param name="end_y">: The ending point of the image in this thread</param>
void applyGaussianBlurParallel(unsigned char* img_in, unsigned char* img_out, int width, int height, int start_y, int end_y) {
	for (int y = start_y; y < end_y; y++) {
		for (int x = 0; x < width; x++) {
			int pixel = y * width + x;
			for (int channel = 0; channel < 4; channel++) {
				// No need for mutexes, every loop in every thread access a different element of the image once
				img_out[4 * pixel + channel] = blur(x, y, channel, img_in, width, height);
			}
		}
	}
}

void gaussian_blur_parallel(const char* filename, int num_threads) {
	int width = 0;
	int height = 0;
	int img_orig_channels = 4;
	// Load an image into an array of unsigned chars that is the size of [width * height * number of channels]. The channels are the Red, Green, Blue and Alpha channels of the image.
	unsigned char* img_in = stbi_load(filename, &width, &height, &img_orig_channels /*image file channels*/, 4 /*requested channels*/);
	if (img_in == nullptr)
	{
		printf("Could not load %s\n", filename);
		return;
	}

	unsigned char* img_out = new unsigned char[width * height * 4];

	std::vector<std::thread> threads;

	// We follow the block scheduling technique
	// Width: 2048 -> 2, 4, 8 threads divide precisely the width
	// Advantage: Increased Locality
	int band_height = height / num_threads;

	// Timer to measure performance
	auto start = std::chrono::high_resolution_clock::now();

	for (int i = 0; i < num_threads; i++) {
		int start_y = i * band_height;
		int end_y = (i == num_threads - 1) ? height : start_y + band_height;
		threads.emplace_back(applyGaussianBlurParallel, img_in, img_out, width, height, start_y, end_y);
	}

	for (auto& th : threads) {
		th.join();
	}

	// Timer to measure performance
	auto end = std::chrono::high_resolution_clock::now();
	// Computation time in milliseconds
	int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
	printf("Threads -> %d / Gaussian Blur - Parallel: Time %dms\n", num_threads, time);

	// Write the blurred image into a JPG file
	stbi_write_jpg("blurred_image_parallel.jpg", width, height, 4, img_out, 90 /*quality*/);

	stbi_image_free(img_in);
	delete[] img_out;
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
/// <summary>
/// Calculates the max value of the given channel and inserts it to the max_values array
/// </summary>
/// <param name="img_in">: An array of characters which make up the image</param>
/// <param name="channel">: An integer which symbolizes the given channel</param>
/// <param name="height">: The height of the image</param>
/// <param name="width">: The width of the image</param>
/// <param name="max_values">: The array consists of 4 values, one for every channel</param>
void calculate_max_value_channel(unsigned char* img_in, int channel, int height, int width, std::array<double, 4>& max_values) {
	double max_value = 0.0;
	for (int y = 0; y < height; y++)
	{
		for (int x = 0; x < width; x++)
		{
			int pixel = y * width + x;
			max_value = max_value < img_in[4 * pixel + channel] ? img_in[4 * pixel + channel] : max_value;
		}
	}
	max_values[channel] = max_value;
}
/// <summary>
/// Normalizes the pixels of the image on the given channel using the type 
/// pixel[channel] = 255 * pixel[channel] / maxValue[channel];
/// </summary>
/// <param name="img_in">: An array of characters which make up the image</param>
/// <param name="channel">: An integer which symbolizes the given channel</param>
/// <param name="height">: The height of the image</param>
/// <param name="width">: The width of the image</param>
/// <param name="max_values">: The array consists of 4 values, one for every channel</param>
void normalize_channels(unsigned char* img_in, int channel, int height, int width, std::array<double, 4>& max_values) {
	for (int y = 0; y < height; y++)
	{
		for (int x = 0; x < width; x++)
		{
			int pixel = y * width + x;
			img_in[4 * pixel + channel] = 255 * img_in[4 * pixel + channel] / max_values[channel];
		}
	}
}
/// <summary>
/// Blurs the input image on the given axis
/// </summary>
/// <param name="start_y">: The starting point of the image in this thread</param>
/// <param name="end_y">: The ending point of the image in this thread</param>
/// <param name="width">: The width of the image</param>
/// <param name="axis">: In this axis, the image will be blurred. 0 for horizontal, 1 for vertical</param>
/// <param name="img_in">: An input image as an array of characters</param>
/// <param name="img_blur">: A blurred image as an array of characters</param>
void blur_parallel(int start_y, int end_y, int width, int axis, unsigned char* img_in, unsigned char* img_blur) {

	for (int y = start_y; y < end_y; y++)
	{
		for (int x = 0; x < width; x++)
		{
			int pixel = y * width + x;
			for (int channel = 0; channel < 4; channel++)
			{
				img_blur[4 * pixel + channel] = blurAxis(x, y, channel, axis, img_in, width, end_y);
			}
		}
	}
}

// Atomic Variable for writing the normalized image as a file, executed by one thread
std::atomic<bool> task_write_normalized_image_done(false);
// Atomic Variable for writing the blurred horizontal image as a file, executed by one thread
std::atomic<bool> task_write_horizontal_blurred_image_done(false);

/// <summary>
/// Given a channel, each thread blur horizontally and vertically a portion of the image
/// </summary>
/// <param name="img_out">: A blurred image consists of characters</param>
/// <param name="img_in">: An image consists of characters</param>
/// <param name="channel">: An integer symbolizing the given channel</param>
/// <param name="height">: The height of the image</param>
/// <param name="width">: The width of the image</param>
/// <param name="max_values">: The max values stored for the given channel</param>
/// <param name="sync_point">: A thread barrier</param>
/// <param name="start_y">: The starting point of the image in this thread</param>
/// <param name="end_y">: The ending point of the image in this thread</param>
void applyGaussianBlurParallelSeparate(unsigned char* img_out, unsigned char* img_in, unsigned char* img_blur, int channel, int height, int width, std::array<double, 4>& max_values, std::barrier<>& sync_point, int start_y, int end_y) {
	// Calculation of the maximum value of the 4 channels separately, from all the pixels of the image
	calculate_max_value_channel(img_in, channel, height, width, max_values);

	// Normalization of the values of each channel in each pixel of the image based on the maximum channel value
	normalize_channels(img_in, channel, height, width, max_values);

	// Wait at the first barrier
	sync_point.arrive_and_wait();

	// Only one thread is allowed to execute this task
	if (!task_write_normalized_image_done.exchange(true)) {
		stbi_write_jpg("image_normalized.jpg", width, height, 4/*channels*/, img_in, 90 /*quality*/);
	}

	// Blur horizontally
	blur_parallel(start_y, end_y, width, 0, img_in, img_out);

	// Wait at the second barrier
	sync_point.arrive_and_wait();

	// Only one thread is allowed to execute this task
	if (!task_write_horizontal_blurred_image_done.exchange(true)) {
		stbi_write_jpg("image_blurred_horizontal.jpg", width, height, 4/*channels*/, img_out, 90 /*quality*/);
	}

	// Blur vertically
	blur_parallel(start_y, end_y, width, 1, img_out, img_blur);
}

void gaussian_blur_separate_parallel(const char* filename) {
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

	unsigned char* img_out = new unsigned char[width * height * 4];
	unsigned char* img_blur = new unsigned char[width * height * 4];
	std::array<double, 4> max_values = { 0.0 };

	int num_threads = 4;
	std::barrier sync_point(num_threads);
	std::vector<std::thread> threads;
	int band_height = height / num_threads;

	// Timer to measure performance
	auto start = std::chrono::high_resolution_clock::now();

	for (int i = 0; i < num_threads; i++) {
		int start_y = i * band_height;
		int end_y = (i == num_threads - 1) ? height : start_y + band_height;
		threads.emplace_back(applyGaussianBlurParallelSeparate, img_out, img_in, img_blur, i, height, width, std::ref(max_values), std::ref(sync_point), start_y, end_y);
	}

	for (auto& th : threads) {
		th.join();
	}

	// Timer to measure performance
	auto end = std::chrono::high_resolution_clock::now();
	// Computation time in milliseconds
	int time = (int)std::chrono::duration_cast<std::chrono::milliseconds>(end - start).count();
	printf("Threads -> %d / Gaussian Blur Separate - Parallel: Time %dms\n", num_threads, time);

	// Write the blurred image into a JPG file
	stbi_write_jpg("image_blurred_final.jpg", width, height, 4, img_blur, 90 /*quality*/);

	stbi_image_free(img_in);
	delete[] img_out;
}

int main()
{
	const char* filename = "garden.jpg";
	gaussian_blur_serial(filename);

	 //gaussian_blur_parallel(filename, 8);

	const char* filename2 = "street_night.jpg";
	//gaussian_blur_separate_serial(filename2);

	 //gaussian_blur_separate_parallel(filename2);

	return 0;
}