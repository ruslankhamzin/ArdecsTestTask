package org.example;

public interface Cache {
    void put(String key, String val);

    Object get(String key);

    void remove(String key);

    void clear();

    void cacheOnDisk();

    void fromDiskToCache();
}
