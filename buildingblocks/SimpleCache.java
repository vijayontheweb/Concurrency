/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author vijay
 */
public class SimpleCache {

    public static void main(String[] args) throws InterruptedException {
        CacheWrapper cache = new CacheWrapper(new Factorial());
        Random random = new Random();
        Integer randomNumber;
        SimpleCache sc = new SimpleCache();
        for (int i = 0; i < 100; i++) {
            randomNumber = random.nextInt(5);
            System.out.println("MAIN PROGRAM :" + randomNumber + "->" + cache.computeExpensiveFunction(randomNumber));
            System.out.println("---------------------------------------");
        }
        cache.stop();
    }
}

interface ComputingLogic<A, V> {

    V computeExpensiveFunction(A arg) throws InterruptedException;
}

class Factorial implements ComputingLogic<Integer, Integer> {

    public Integer computeExpensiveFunction(Integer n) throws InterruptedException {
        if (n > 1) {
            return n * computeExpensiveFunction(n - 1);
        } else {
            return 1;
        }
    }
}

class CacheWrapper<CacheKey, CacheValue> implements ComputingLogic<CacheKey, CacheValue> {

    Map<CacheKey, CacheObj> cache = new ConcurrentHashMap<CacheKey, CacheObj>();
    ComputingLogic logic;
    Thread cleanerThread;

    CacheWrapper(ComputingLogic<CacheKey, CacheValue> logic) {
        this.logic = logic;
        cleanerThread = new Thread(new CacheCleaner(cache));
        cleanerThread.start();
    }

    public CacheValue computeExpensiveFunction(CacheKey cacheKey) throws InterruptedException {
        CacheValue cacheValue = fetch(cacheKey);
        if (cacheValue == null) {
            cacheValue = (CacheValue) logic.computeExpensiveFunction(cacheKey);
            System.out.println("NOT FOUND IN CACHE.PUTTING..." + cacheKey + "->" + cacheValue);
            store(cacheKey, cacheValue);
        } else {
            System.out.println("FOUND IN CACHE.GETTING...");
            System.out.println("CACHE FETCH:" + cacheKey + "->" + cacheValue);
        }
        //Thread.currentThread().sleep(2);
        return cacheValue;

    }

    CacheValue fetch(CacheKey cacheKey) {
        CacheObj<CacheKey, CacheValue> cacheObj = cache.get(cacheKey);
        if (cacheObj != null) {
            return cacheObj.fetch();
        }
        return null;
    }

    void store(CacheKey cacheKey, CacheValue cacheValue) {
        cache.put(cacheKey, new CacheObj(cacheKey, cacheValue));
    }

    void stop() {
        cleanerThread.stop();
    }
}

class CacheObj<CacheKey, CacheValue> implements Comparable<CacheObj> {

    int accessCount;
    long expireTime;
    CacheKey cacheKey;
    CacheValue cacheValue;
    private static final long CACHE_LIFETIME = 10;

    CacheObj(CacheKey cacheKey, CacheValue cacheValue) {
        this.cacheKey = cacheKey;
        this.cacheValue = cacheValue;
        this.expireTime = System.currentTimeMillis() + CACHE_LIFETIME;
    }

    public long getExpireTime() {
        return expireTime;
    }

    CacheValue fetch() {
        if (cacheValue != null) {
            this.accessCount++;
        }
        System.out.println(cacheKey + "->" + cacheValue + " fetched..." + accessCount + " times.");
        return cacheValue;
    }

    public int compareTo(CacheObj cacheObj) {
        return this.accessCount - cacheObj.accessCount;
    }

    public CacheKey getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(CacheKey cacheKey) {
        this.cacheKey = cacheKey;
    }
}

interface CacheAlgorithm {

    void clean() throws InterruptedException;
}

class CacheCleaner<CacheKey> implements Runnable, CacheAlgorithm {

    private static final long CACHE_CLEANUP_INTERVAL = 1;//in milliseconds
    Map<CacheKey, CacheObj> cache;

    public CacheCleaner(Map<CacheKey, CacheObj> cache) {
        this.cache = cache;
    }

    public void clean() {
        if (cache.size() > 0) {
            for (CacheObj cacheObj : cache.values()) {
                if (System.currentTimeMillis() > cacheObj.getExpireTime()) {
                    //remove from cache
                    System.out.println("CacheKey: " + cacheObj.getCacheKey() + " EXPIRED and removed from cache");
                    cache.remove(cacheObj.getCacheKey());
                }
            }
            try {
                Thread.currentThread().sleep(CACHE_CLEANUP_INTERVAL);
            } catch (InterruptedException ie) {
            }
        }

    }

    public void run() {
        while (true) {
            clean();
        }
    }
}
