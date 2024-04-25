#version 330 core


struct Light {
    float x, y, radius, intensity, r, g, b;
};

layout(std430) buffer lights_data {
    Light lights[];
};

const int chunkSize = 32;
const int viewHeight = 350;
const int viewWidth = 650;

const vec2 celestialOffset = vec2(0, -175);
const vec2 celestialSphere = vec2(325, 325);

const float moonRadius = 20;
const float sunRadius = 20;

attribute vec2 a_position;
attribute vec3 a_vertexColor;
attribute float a_empty;
varying vec3 v_color;

uniform mat4 u_proj;
uniform float u_pointSize;
uniform int u_cellSize;

uniform vec2 u_playerPos;
uniform vec2 u_cameraPos;
uniform int u_dayTimeMinutes;


uniform float[chunkSize] terrain_heights;

const vec3 gammaCorrection = vec3(2.2);

const vec3 skyColor1 = vec3(0.91, 0.94, 0.96);
const vec3 skyColor2 = vec3(0.23, 0.58, 0.82);
const vec3 unlitBaseLight = vec3(0.2);

#define PI 3.14159265359

float rand(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453);
}

float perlin(vec2 p) {

    vec2 f = fract(p);
    f = f*f*(3.0-2.0*f);
    vec2 id = floor(p);
    float topL = rand(id+vec2(-0.5, 0.5)),
    topR = rand(id+vec2(0.5, 0.5)),
    botL = rand(id+vec2(-0.5, -0.5)),
    botR = rand(id+vec2(0.5, -0.5));
    float top = mix(topL, topR, f.x);
    float bot = mix(botL, botR, f.x);

    return mix(bot, top, f.y);;
}

float fbm(vec2 p, int oct) {
    p-=30.0;
    float val = 0.0;

    float amp = 0.5;
    float freq = 0.9;

    float lacuranity = 1.7;
    float gain = 0.55;

    for (int i=0;i<oct;i++) {
        val += amp*perlin(p*freq+float(i));
        amp *= gain;
        freq *= lacuranity;
    }

    return val;
}

float pattern(vec2 p, int oct) {
    float z = fbm(p, oct);
    float a = max(0.0, sign(z-0.7))*0.3,
    b = max(0.0, sign(z-0.60))*0.3,
    c = max(0.0, sign(z-0.50))*0.3;
    return a+b+c;
}


vec3 moon(vec2 pos, vec2 moonPos, float size) {

    vec2 normOffset = (moonPos - pos) / size;
    float normDist = length(normOffset);

    float col;
    float light = clamp(1/normDist, 0.0, 1.0);

    if (length(normOffset) < 1) {
        float pattern = pattern(normOffset * 5, 10);
        pattern = mix(0.0, 0.4, pattern);

        col = light - pattern * (1.0-normDist);
    } else {
        col = light;
    }

    return vec3(col-0.19, col-0.16, col-0.1);

}


vec3 sun(vec2 pos, vec2 moonPos, float size) {

    vec2 normOffset = (moonPos - pos) / size;
    float normDist = length(normOffset);

    float col = clamp(1/normDist, 0.0, 1.0);
    col = smoothstep(0.0, 1.0, col);

    return vec3(col, col-0.3, 0);

}


float lightFactor(float distanceSqr, float r) {
    float invRsqr = 1.0 / (r*r);
    float v = -distanceSqr * invRsqr;

    return max(0, exp(4 * v) * (v + 1));
}

vec3 gammaCorrect(vec3 color) {
    return pow(color, gammaCorrection);
}

float map(float value, float inputMin, float inputMax, float outputMin, float outputMax) {
    return outputMin + (value - inputMin) * (outputMax - outputMin) / (inputMax - inputMin);
}

//vec3 skyColor(vec2 cameraOff) {
//
//}

vec3 calcLight(Light light) {
    float r = light.radius;

    float dx = light.x - a_position.x;
    float dy = light.y - a_position.y;

    float distanceSqr = dx*dx + dy*dy;
    if (distanceSqr < r*r) {
        vec3 lightColor = gammaCorrect(vec3(light.r, light.g, light.b));
        float factor = lightFactor(distanceSqr, r) * light.intensity;
        return lightColor * factor;
    }

    return vec3(0);
}

vec3 calcLightAroundCircle(Light light, float dist) {
    float r = light.radius + dist;

    float dx = light.x - a_position.x;
    float dy = light.y - a_position.y;

    float distanceSqr = dx*dx + dy*dy - dist*dist;
    if (distanceSqr < 0) {
        return vec3(light.r, light.g, light.b);
    }

    if (distanceSqr < r*r) {
        vec3 lightColor = gammaCorrect(vec3(light.r, light.g, light.b));
        float factor = lightFactor(distanceSqr, r) * light.intensity;
        return lightColor * factor;
    }

    return vec3(0);
}


void main() {
    gl_Position = u_proj * vec4(a_position * u_cellSize + vec2(0, 2), 0.0, 1.0);
    gl_PointSize = u_pointSize;

    float hour = u_dayTimeMinutes / 60.0;
    float sunLight = sin(hour / 24.0 * PI * 2) * 0.5 + 0.5;

    float moonAngle = map(hour, 0, 24, 0, PI * 2);
    vec2 moonWorldPos = celestialOffset + u_cameraPos + vec2(cos(moonAngle) * celestialSphere.x, -sin(moonAngle) * celestialSphere.y);

    float sunAngle = moonAngle + PI;
    vec2 sunWorldPos = celestialOffset + u_cameraPos + vec2(cos(sunAngle) * celestialSphere.x, -sin(sunAngle) * celestialSphere.y);


    int inChunkX = int(mod(int(a_position.x), chunkSize));
    float terrainHeight = terrain_heights[inChunkX];
    float surfaceDist = a_position.y - terrainHeight;

    vec3 vertexColor = a_vertexColor;
    if (a_empty == 1 && surfaceDist > 0) {

        vec2 cameraOff = a_position - u_cameraPos;
        float verticalT = map(cameraOff.y, -viewHeight/2.0, viewHeight/2.0, 0.0, 1.0);

        vec3 skyColor = mix(skyColor1 * sunLight, skyColor2 * sunLight, verticalT);
        vec3 moonColor = moon(a_position, moonWorldPos, moonRadius);
        vec3 sunColor = sun(a_position, sunWorldPos, sunRadius);

        vec3 maxSunMoon = max(moonColor, sunColor);

        vertexColor = max(unlitBaseLight, mix(maxSunMoon, skyColor, sunLight));
    }

    vec3 summedLightColor = unlitBaseLight + mix(vec3(sunLight / 2), vec3(0), clamp(-surfaceDist/32, 0, 1));

    if (a_empty == 1 && surfaceDist > 0) {
        Light moonLight = Light(moonWorldPos.x, moonWorldPos.y, moonRadius*5, 1, 1, 1, 1);
        summedLightColor += max(vec3(0), calcLightAroundCircle(moonLight, moonRadius));

        Light sunLight = Light(sunWorldPos.x, sunWorldPos.y, sunRadius*5, 1, 1, 1, 0);
        summedLightColor += max(vec3(0), calcLightAroundCircle(sunLight, sunRadius));

    }

    for (int i = 0; i < lights.length(); i++) {
        summedLightColor += calcLight(lights[i]);
    }

    v_color = gammaCorrect(vertexColor) * summedLightColor;

    //v_color = gammaCorrect(a_vertexColor);

}
