
#define KERNEL_RADIUS 5
#define sigma 1.0f

__kernel void blurAxis(__global const uchar4* input, __global uchar4* output, const int width, const int height, const int axis) {
    int x = get_global_id(0);
    int y = get_global_id(1);

    float sum_weight = 0.0f;
    float ret[4] = {0.0f};

    for (int offset = -KERNEL_RADIUS; offset <= KERNEL_RADIUS; offset++) {
        int offset_x = (axis == 0) ? offset : 0;
        int offset_y = (axis == 1) ? offset : 0;

        int pixel_x = clamp(x + offset_x, 0, width - 1);
        int pixel_y = clamp(y + offset_y, 0, height - 1);
        int pixel = pixel_y * width + pixel_x;

        float weight = exp(-(offset * offset) / (2.0f * sigma * sigma));
        ret[0] += weight * input[pixel].x;
        ret[1] += weight * input[pixel].y;
        ret[2] += weight * input[pixel].z;
        ret[3] += weight * input[pixel].w;
        sum_weight += weight;
    }

    output[y * width + x].x = (uchar)clamp(ret[0] / sum_weight, 0.0f, 255.0f);
    output[y * width + x].y = (uchar)clamp(ret[1] / sum_weight, 0.0f, 255.0f);
    output[y * width + x].z = (uchar)clamp(ret[2] / sum_weight, 0.0f, 255.0f);
    output[y * width + x].w = (uchar)clamp(ret[3] / sum_weight, 0.0f, 255.0f);
}
