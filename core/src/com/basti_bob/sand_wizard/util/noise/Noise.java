package com.basti_bob.sand_wizard.util.noise;

public interface Noise {

    float eval(float x);
    float eval(float x, float y);
    float eval(float x, float y, float z);
}
