package com.basti_bob.sand_wizard.registry;

import java.util.List;

public class RegistryTreePrint {

    public static void printRegistryTree() {

        List<Registry<?>> allRegistries = Registry.ALL_MAIN_REGISTRIES;

        for (Registry<?> registry : allRegistries) {
            System.out.println(print(registry));
        }

        for (Registry<?> registry : allRegistries) {
            for (RegistryObject<?> object : registry.allRegistryObjects) {
                System.out.println(object.getNameID());
            }
        }
    }

    public static String print(Registry<?> registry) {
        StringBuilder buffer = new StringBuilder(5000);
        print(registry, buffer, "", "");
        return buffer.toString();
    }

    private static void print(Registry<?> registry, StringBuilder buffer, String prefix, String childrenPrefix) {
        buffer.append(prefix);

        if (registry.parentRegistry == null) {
            buffer.append("REGISTRY: ");
        } else {
            buffer.append("SUB_REGISTRY: ");
        }

        buffer.append(registry.name);
        buffer.append('\n');

        String newPrefix = childrenPrefix + "|-- ";
        String newChildPrefix = childrenPrefix + "|   ";

        for (RegistryObject<?> object : registry.ownRegistryObjects) {
            buffer.append(newPrefix);
            buffer.append(object.getName());
            buffer.append('\n');
        }

        buffer.append(newChildPrefix);
        buffer.append('\n');

        for (Registry<?> next : registry.childRegistries) {
            print(next, buffer, newPrefix, newChildPrefix);
        }

    }

    private static void print(String name, StringBuilder buffer, String prefix, String childrenPrefix) {

    }

}
