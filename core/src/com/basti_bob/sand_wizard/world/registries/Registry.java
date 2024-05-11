package com.basti_bob.sand_wizard.world.registries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Registry<T> {

    public final String nameID;
    public HashMap<String, T> nameEntryMap = new HashMap<>();
    public List<T> allEntries = new ArrayList<>();
    public Registry<? super T> parentRegistry;

    public Registry(String name, Registry<? super T> parentRegistry) {
        this(parentRegistry.nameID + "." + name);

        this.parentRegistry = parentRegistry;
    }

    public Registry(String nameID) {
        this.nameID = nameID;
    }
    public T register(String name, T type) {
        String id = nameID + ": " + name;

        registerFinalID(id, type);

        return type;
    }

    private void registerFinalID(String id, T type) {

        nameEntryMap.put(id, type);
        allEntries.add(type);

        if(parentRegistry != null)
            parentRegistry.registerFinalID(id, type);
    }

}
