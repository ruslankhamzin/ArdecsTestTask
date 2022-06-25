package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RealizationCache implements Cache {
    String fileName;
    String filename2;
    int cacheSize;
    HashMap<String, String> map;
    LinkedList<String> list;
    HashMap<String, Integer> map1;
    int strategy;
    int repo;

    public RealizationCache(int cacheSize, int strategy, int repo) {
        this.cacheSize = cacheSize;
        map = new HashMap<>(cacheSize);
        this.strategy = strategy;
        map1 = new HashMap<>(cacheSize);
        list = new LinkedList<>();
        this.repo = repo;
        switch (strategy) {
            case 1:
                fileName = "LRUCacheRepo.txt";
                filename2 = "LRUHelpCacheRepo.txt";
                break;
            case 2:
                fileName = "MRUCacheRepo.txt";
                filename2 = "MRUHelpCacheRepo.txt";
                break;
            case 3:
                fileName = "LFUCacheRepo.txt";
                filename2 = "LFUHelpCacheRepo.txt";
                break;
        }
    }

    @Override
    public void put(String key, String value) {
        switch (strategy) {
            case 1:
                list.remove(key);
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
            case 3:
                while (map.size() >= this.cacheSize) {
                    this.pruning();
                }
                map.put(key, value);
                map1.put(key, 1);
                break;
        }
        if (repo == 1) {
            cacheOnDisk();
        }

    }

    @Override
    public Object get(String key) {
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
            case 3:
                boolean res3 = map1.containsKey(key);
                if (res3) {
                    int value = map1.get(key);
                    value++;
                    map1.put(key, value);
                    if (repo == 1) {
                        cacheOnDisk();
                    }
                    return map.get(key);
                }
                break;

        }
        return null;
    }

    @Override
    public void remove(String key) {
        list.remove(key);
        map.remove(key);
    }

    @Override
    public void clear() {
        map = new HashMap<>(cacheSize);
        list = new LinkedList<>();
        map1 = new HashMap<>(cacheSize);
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
            if (strategy == 3) {
                oos.writeObject(map1);
            } else {
                oos.writeObject(list);
            }
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
            HashMap<String, String> mapFromDisk = (HashMap<String, String>) ois.readObject();
            ois.close();
            map = mapFromDisk;
            fis = new FileInputStream(filename2);
            ois = new ObjectInputStream(fis);
            if (strategy == 3) {
                HashMap<String, Integer> mapCount = (HashMap<String, Integer>) ois.readObject();
                if (mapCount != null) {
                    map1 = mapCount;
                }
            } else {
                list = (LinkedList<String>) ois.readObject();
            }
            ois.close();

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Диск был пуст");
        } catch (ClassNotFoundException e) {
            System.out.println("Необходимый класс не был найден");
        }
    }

    public void pruning() {
        if (strategy == 3) {
            List<Integer> values = new ArrayList<>(map1.values());
            int min = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                if (min >= values.get(i)) {
                    min = values.get(i);
                }
            }
            List<String> listOfKeys = new ArrayList<>(map1.keySet());
            for (String listOfKey : listOfKeys) {
                if (map1.get(listOfKey) == min) {
                    map.remove(listOfKey);
                    map1.remove(listOfKey);
                    break;
                }
            }
        } else {
            Object key = list.removeLast();
            map.remove(key);
        }

    }
}




