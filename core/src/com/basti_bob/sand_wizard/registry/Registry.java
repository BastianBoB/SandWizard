package com.basti_bob.sand_wizard.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.ArrayList;
import java.util.List;

public class Registry<T> {

    public static final List<Registry<?>> ALL_REGISTRIES = new ArrayList<>();
    public static final List<Registry<?>> ALL_MAIN_REGISTRIES = new ArrayList<>();

    public final String nameID;
    public final String name;

    public Registry<? super T> parentRegistry;
    public final List<Registry<?>> childRegistries = new ArrayList<>();

    public final BiMap<String, T> idEntryMap = HashBiMap.create();

    public final BiMap<String, RegistryObject<? extends T>> idRegistryObjectMap = HashBiMap.create();

    public final List<RegistryObject<? extends T>> allRegistryObjects = new ArrayList<>();
    public final List<RegistryObject<T>> ownRegistryObjects = new ArrayList<>();

    private final List<T> allEntries = new ArrayList<>();

    public Registry(String name, Registry<? super T> parentRegistry) {
        this(name, parentRegistry.nameID + "." + name);

        this.parentRegistry = parentRegistry;
        parentRegistry.childRegistries.add(this);
    }

    public Registry(String name) {
        this(name, name);

        ALL_MAIN_REGISTRIES.add(this);
    }

    public Registry(String name, String nameID) {
        this.name = name;
        this.nameID = nameID;

        ALL_REGISTRIES.add(this);
    }

    public List<T> getAllEntries() {
        if(allEntries.size() == 0) {
            System.out.println("WARNING: getAllEntries called, but Registry:" + this.nameID + " has no entries yet");
        }

        return allEntries;
    }

    public String getEntryNameID(T entry) {
        return idEntryMap.inverse().get(entry);
    }

    public String getEntryName(T entry) {
        return idRegistryObjectMap.get(idEntryMap.inverse().get(entry)).getName();
    }

    public T getEntryWithId(String nameID) {
        return idEntryMap.get(nameID);
    }

    public T getEntryWithName(String name) {
        return idEntryMap.get(this.nameID);
    }

    public String getEntryId(String entryName) {
        return this.nameID + ":" + entryName;
    }

    public <U extends T> U register(String name, U type) {

        RegistryObject<T> registryObject = new RegistryObject<>(name, getEntryId(name), type);
        registerRegistryObject(registryObject);

        return type;
    }

    private void registerRegistryObject(RegistryObject<T> registryObject) {
        ownRegistryObjects.add(registryObject);

        registerRegistryObjectInParentRegistries(registryObject);
    }

    private <U extends T> void registerRegistryObjectInParentRegistries(RegistryObject<U> registryObject) {
        idEntryMap.put(registryObject.getNameID(), registryObject.getValue());
        idRegistryObjectMap.put(registryObject.getNameID(), registryObject);
        allEntries.add(registryObject.getValue());
        allRegistryObjects.add(registryObject);

        if (parentRegistry != null)
            parentRegistry.registerRegistryObjectInParentRegistries(registryObject);
    }

}
