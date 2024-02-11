package com.basti_bob.sand_wizard.util;

import java.lang.reflect.Array;

public class Array2D<T> {

    public final int rows, cols;
    private final T[] array;

    public Array2D(Class<T> clazz, int rows, int cols) {
        this.rows = rows;
        this.cols = cols;

        array = (T[]) Array.newInstance(clazz, rows*cols);
    }

    public T[] getArray() {
        return array;
    }

    public T get(int x, int y) {
        return array[y * rows + x];
    }

    public void set(int x, int y, T obj) {
        array[y * rows + x] = obj;
    }
}
