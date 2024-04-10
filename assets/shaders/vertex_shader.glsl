#version 330 core

struct Light {
    float x, y, radius, intensity, r, g, b;
};

layout(std430) buffer lights_data {
    Light lights[];
};

attribute vec2 a_position;
attribute float a_vertexData;
varying vec3 v_color;

uniform mat4 u_proj;
uniform float u_pointSize;
uniform int u_cellSize;


void main() {
    int packedData = floatBitsToInt(a_vertexData);
    float r = float(packedData & 0x7F) / 127.0;
    float g = float(packedData >> 8 & 0x7F) / 127.0;
    float b = float(packedData >> 16 & 0x7F) / 127.0;

    gl_Position = u_proj * vec4(a_position * u_cellSize + vec2(0, 2), 0.0, 1.0);
    gl_PointSize = u_pointSize;

    vec3 sumColor = vec3(0, 0, 0);

    for (int i = 0; i < lights.length(); i++) {
        Light light = lights[i];
        float r = light.radius;
        vec3 lightColor = vec3(light.r, light.g, light.b);

        float dx = light.x - a_position.x;
        float dy = light.y - a_position.y;

        float distanceSqr = dx*dx + dy*dy;
        if (distanceSqr < r*r) {
            float factor = exp(-distanceSqr / (0.25*r*r)) * light.intensity;
            sumColor += lightColor * factor;
        }
    }

    float m = 0.2; //a_position.x < 0 ? 0 : 1;
    sumColor = vec3(max(m, sumColor.r), max(m, sumColor.g), max(m, sumColor.b));

    v_color = vec3(r, g, b) * sumColor;
}
