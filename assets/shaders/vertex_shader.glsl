attribute vec2 a_position;
attribute vec3 a_color;
varying vec3 v_color;
uniform mat4 u_proj;

void main() {
    v_color = a_color;
    gl_Position = u_proj * vec4(a_position + vec2(0, 2), 0.0, 1.0);
    gl_PointSize = 4;
}