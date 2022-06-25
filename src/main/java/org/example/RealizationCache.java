package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class RealizationCache implements Cache {
    String fileName;
    String filename2;
    int cacheSize;
    HashMap map;
    LinkedList list;
    HashMap map1;
    int strategy;
    int repo;

    public RealizationCache(int cacheSize, int strategy, int repo) {
        this.cacheSize = cacheSize;
        map = new HashMap(cacheSize);
        this.strategy = strategy;
        map1 = new HashMap(cacheSize);
        list = new LinkedList();
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
                boolean res3 = false;
                res3 = map.containsKey(key);
               boolean res4 = map1.containsKey(key);
                if (res3 && res4) {
                    int value = (int) map1.get(key);
                    value++;
                    map1.put(key,value);
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
        map = new HashMap(cacheSize);
        list = new LinkedList();
        map1= new HashMap(cacheSize);
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
        switch (strategy) {

            case 3:
                try {
                    FileOutputStream fos = new FileOutputStream(fileName);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    oos.writeObject(map);
                    oos.close();
                    fos = new FileOutputStream(filename2);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(map1);
                    oos.close();
                } catch (IOException e) {
                    System.out.println("Произошла ошибка ввода-вывода");
                }
                break;


            default:
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
                break;
        }

    }


    @Override
    public void fromDiskToCache() {
        try {
            FileInputStream fis = new FileInputStream(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            HashMap mapFromDisk = (HashMap) ois.readObject();
            ois.close();
            map = mapFromDisk;
            FileInputStream fis1 = new FileInputStream(filename2);
            ObjectInputStream ois2 = new ObjectInputStream(fis1);
            switch (strategy) {
                case 1:
                    LinkedList listFromDisk = (LinkedList) ois2.readObject();
                    list = listFromDisk;
                    break;
                case 3:
                    HashMap mapCount = (HashMap) ois2.readObject();
                    if(mapCount!=null) {
                        map1 = mapCount;
                    }
                    break;
            }
            ois2.close();

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден");
        } catch (IOException e) {
            System.out.println("Диск был пуст");
        } catch (ClassNotFoundException e) {
            System.out.println("Необходимый класс не был найден");
        }
    }

    public void pruning() {
        switch (strategy) {
            case 3:
                ArrayList values = new ArrayList(map1.values());
                int min = (int) values.get(0);
                for (int i = 1; i < values.size(); i++) {
                    if (min >= (int) values.get(i)) {
                        min = (int) values.get(i);
                    }
                }
                ArrayList listOfKeys = new ArrayList(map1.keySet());
                for (int i = 0; i < listOfKeys.size(); i++) {
                    if ((int) map1.get(listOfKeys.get(i)) == min) {
                        map.remove(listOfKeys.get(i));
                        map1.remove(listOfKeys.get(i));
                        break;
                    }
                }

                break;
            default:
                Object key = list.removeLast();
                map.remove(key);
                break;
        }

    }
}




