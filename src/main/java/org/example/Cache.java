package org.example;

public interface Cache {
    void put(Object key, Object val);

    Object get(Object key);

    void remove(Object key);

    void clear();

    void cacheOnDisk();

    void fromDiskToCache();
}
