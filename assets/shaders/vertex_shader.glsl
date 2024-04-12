#version 330 core

struct Light {
    float x, y, radius, intensity, r, g, b;
};

layout(std430) buffer lights_data {
    Light lights[];
};

attribute vec2 a_position;
attribute vec3 a_vertexColor;
varying vec3 v_color;

uniform mat4 u_proj;
uniform float u_pointSize;
uniform int u_cellSize;

const vec3 gammaCorrection = vec3(2.2);


void main() {
    gl_Position = u_proj * vec4(a_position * u_cellSize + vec2(0, 2), 0.0, 1.0);
    gl_PointSize = u_pointSize;

    vec3 sumColor = vec3(0.8);

    //light Calculation
    for (int i = 0; i < lights.length(); i++) {
        Light light = lights[i];
        float r = light.radius;

        float dx = light.x - a_position.x;
        float dy = light.y - a_position.y;

        float distanceSqr = dx*dx + dy*dy;
        if (distanceSqr < r*r) {
            vec3 lightColor = vec3(light.r, light.g, light.b);
            float factor = exp(-distanceSqr / (0.25*r*r)) * light.intensity;
            sumColor += lightColor * factor;
        }
    }

    vec3 finalColor = a_vertexColor * sumColor;
    v_color = pow(finalColor, gammaCorrection);
}
