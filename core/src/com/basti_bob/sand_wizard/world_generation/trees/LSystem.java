package com.basti_bob.sand_wizard.world_generation.trees;

public class LSystem {

    public static String generateLSystem(String rule, int iterations) {
        String lSystem = rule;

        for (int i = 0; i < iterations; i++) {
            lSystem = getNextLSystem(lSystem, rule);
        }

        return lSystem;
    }

    public static String getNextLSystem(String lSystem, String rule) {
        StringBuilder next = new StringBuilder();

        for (char c : lSystem.toCharArray()) {
            if (c == 'F')
                next.append(rule);
            else
                next.append(c);
        }

        return next.toString();
    }
}
