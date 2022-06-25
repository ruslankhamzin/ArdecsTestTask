package org.example;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;

public class RealizationCache implements Cache {
    String fileName;
    String filename2;
    int cacheSize;
    HashMap map;
    LinkedList list;
    int strategy;
    int repo;

    public RealizationCache(int cacheSize, int strategy, int repo) {
        this.cacheSize = cacheSize;
        map = new HashMap(cacheSize);
        this.strategy = strategy;
        this.repo = repo;
        switch (strategy) {
            case 1:
                list = new LinkedList();
                fileName = "LRUCacheRepo.txt";
                filename2 = "LRUHelpCacheRepo.txt";
                break;
            case 2:
                list = new LinkedList();
                fileName = "MRUCacheRepo.txt";
                filename2 = "MRUHelpCacheRepo.txt";
                break;
        }
    }

    @Override
    public void put(Object key, Object value) {
        switch (strategy) {
            case 1:
                boolean res = list.remove(key);
                while (list.size() >= this.cacheSize) {
                    this.pruning();
                }
                list.addFirst(key);
                map.put(key, value);
                break;
            case 2:
                while (list.size() >= this.cacheSize) {
                    this.pruning();
                }
                list.addLast(key);
                map.put(key, value);
                break;
        }
        if (repo == 1) {
            cacheOnDisk();
        }

    }

    @Override
    public Object get(Object key) {
        if (repo == 1) {
            fromDiskToCache();
        }
        switch (strategy) {
            case 1:
                boolean res = list.remove(key);
                if (res) {
                    list.addFirst(key);
                    if (repo == 1) {
                        cacheOnDisk();
                    }
                    return map.get(key);
                }
                break;
            case 2:
                boolean res1 = list.contains(key);
                if (res1) {
                    return map.get(key);
                }
                break;
        }
        return null;
    }

    @Override
    public void remove(Object key) {
        list.remove(key);
        map.remove(key);
    }

    @Override
    public void clear() {
        map = new HashMap(cacheSize);
        list = new LinkedList();
        if (repo == 1) {
            try {
                FileWriter fwOb = new FileWriter(fileName, false);
                PrintWriter pwOb = new PrintWriter(fwOb, false);
                pwOb.flush();
                pwOb.close();
                fwOb.close();
                fwOb = new FileWriter(filename2, false);
                pwOb = new PrintWriter(fwOb, false);
                pwOb.flush();
                pwOb.close();
                fwOb.close();
            } catch (IOException e) {
                System.out.println("Ошибка ввода-вывода");
            }
        }

    }




    @Override
    public void cacheOnDisk() {
        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(map);
            oos.close();
            fos = new FileOutputStream(filename2);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
            oos.close();
        } catch (IOException e) {
            System.out.println("Произошла ошибка ввода-вывода");
        }
    }


    @Override
    public void fromDiskToCache() {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap mapFromDisk = (HashMap) ois.readObject();
            ois.close();
            fis = new FileInputStream(filename2);
            ois = new ObjectInputStream(fis);
            LinkedList listFromDisk = (LinkedList) ois.readObject();
            ois.close();
            list = listFromDisk;
            map = mapFromDisk;
        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Диск был пуст");
        } catch (ClassNotFoundException e) {
            System.out.println("Необходимый класс не был найден");
        }
    }

    public void pruning() {
        Object key = list.removeLast();
        map.remove(key);
    }
}




