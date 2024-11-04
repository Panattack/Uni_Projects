
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
