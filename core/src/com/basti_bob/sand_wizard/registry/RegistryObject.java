package com.basti_bob.sand_wizard.registry;

import java.util.Objects;

public class RegistryObject<T> {

    private final String name;
    private final String nameID;
    private final T value;
    public RegistryObject(String name, String nameID, T value) {
        this.name = name;
        this.nameID = nameID;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getNameID() {
        return nameID;
    }

    public T getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nameID);
    }
}
