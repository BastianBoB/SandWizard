package com.basti_bob.sand_wizard.util;

public class FunctionRunTime {

    public static void timeFunction(String text, Runnable runnable){
        long start = System.nanoTime();

        runnable.run();

        System.out.println(text + " took: " + (System.nanoTime() - start) / 1e6 + " ms");
    }
}
