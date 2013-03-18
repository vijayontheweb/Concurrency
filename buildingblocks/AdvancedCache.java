/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 *
 * @author vijay
 */
public class AdvancedCache {

    public static void main(String args[]) throws InterruptedException {
        long elapsedTime = timeTasks(new Task(), 10);
        System.out.println("ELAPSED TIME IS:" + elapsedTime);
        System.exit(0);
    }

    private static long timeTasks(final Runnable task, int nThreads) throws InterruptedException {
        final CountDownLatch startGate = new CountDownLatch(1);
        final CountDownLatch endGate = new CountDownLatch(nThreads);

        for (int i = 0; i < nThreads; i++) {
            Thread t = new Thread() {

                public void run() {
                    try {
                        startGate.await();
                        try {
                            task.run();
                        } finally {
                            endGate.countDown();
                        }
                    } catch (InterruptedException ie) {
                    }
                }
            };
            t.start();
        }
        long startTime = System.currentTimeMillis();
        startGate.countDown();
        endGate.await();
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }
}

class Task implements Runnable {

    public void run() {
        AdvancedCacheWrapper cache = new AdvancedCacheWrapper(new Factoral());
        Random random = new Random();
        Integer randomNumber;
        for (int i = 0; i < 100; i++) {
            randomNumber = random.nextInt(5);
            try {
                System.out.println("MAIN PROGRAM :" + randomNumber + "->" + cache.computeExpensiveFunction(randomNumber));
            } catch (InterruptedException ie) {
            }
            System.out.println("---------------------------------------");
        }
    }
}

interface ComputeLogic<A, V> {

    V computeExpensiveFunction(A arg) throws InterruptedException;
}

class Factoral implements ComputeLogic<Integer, Integer> {

    public Integer computeExpensiveFunction(Integer n) throws InterruptedException {
        Thread.currentThread().sleep(50);
        if (n > 1) {
            return n * computeExpensiveFunction(n - 1);
        } else {
            return 1;
        }
    }
}

class AdvancedCacheWrapper<CacheKey, CacheValue> implements ComputeLogic<CacheKey, CacheValue> {

    ConcurrentHashMap<CacheKey, Future<CacheObj>> cache = new ConcurrentHashMap<CacheKey, Future<CacheObj>>();
    ComputeLogic logic;
    Thread cleanerThread;

    AdvancedCacheWrapper(ComputeLogic<CacheKey, CacheValue> logic) {
        this.logic = logic;
        cleanerThread = new Thread(new AdvCacheCleaner(cache));
        cleanerThread.start();
    }

    public CacheValue computeExpensiveFunction(final CacheKey cacheKey) throws InterruptedException {
        Future<CacheObj> fCacheObj = cache.get(cacheKey);
        if (fCacheObj == null) {
            System.out.println("NOT FOUND IN CACHE.PUTTING...");
            FutureTask<CacheObj> ftCacheObj = futureComputeExpensiveFunction(cacheKey);
            fCacheObj = cache.putIfAbsent(cacheKey, ftCacheObj);
            if (fCacheObj == null) {
                ftCacheObj.run();// call to logic.computeExpensiveFunction(cacheKey) happens here            
                fCacheObj = ftCacheObj;
            }
        } else {
            System.out.println("FOUND IN CACHE.GETTING...");
        }
        return fetchCacheValueFromFutureObj(fCacheObj);

    }

    CacheValue fetch(CacheKey cacheKey) {
        Future<CacheObj> fCacheObj = cache.get(cacheKey);
        if (fCacheObj != null) {
            return fetchCacheValueFromFutureObj(fCacheObj);
        }
        return null;
    }

    CacheValue fetchCacheValueFromFutureObj(Future<CacheObj> fCacheObj) {
        CacheValue cacheValue = null;
        try {
            CacheObj<CacheKey, CacheValue> cacheObj = fCacheObj.get();
            if (cacheObj != null) {
                cacheValue = cacheObj.fetch();
            }
        } catch (ExecutionException ee) {
        } catch (InterruptedException ee) {
        }
        return cacheValue;
    }

    FutureTask<CacheObj> futureComputeExpensiveFunction(final CacheKey cacheKey) {
        Callable<CacheObj> cCacheObj = new Callable<CacheObj>() {

            public CacheObj call() throws InterruptedException {
                CacheValue cacheValue = (CacheValue) logic.computeExpensiveFunction(cacheKey);
                return new CacheObj(cacheKey, cacheValue);
            }
        };
        FutureTask<CacheObj> futureTask = new FutureTask<CacheObj>(cCacheObj);
        return futureTask;
    }

    void stop() {
        cleanerThread.stop();
    }
}

class AdvCacheCleaner<CacheKey> implements Runnable, CacheAlgorithm {

    private static final long CACHE_CLEANUP_INTERVAL = 200;//in milliseconds
    ConcurrentHashMap<CacheKey, Future<CacheObj>> cache;

    public AdvCacheCleaner(ConcurrentHashMap<CacheKey, Future<CacheObj>> cache) {
        this.cache = cache;
    }

    public void clean() {
        if (cache.size() > 0) {
            for (Future<CacheObj> fCacheObj : cache.values()) {
                CacheObj cacheObj = null;
                try {
                    cacheObj = fCacheObj.get();
                } catch (ExecutionException ee) {
                } catch (InterruptedException ee) {
                }
                if (cacheObj != null && System.currentTimeMillis() > cacheObj.getExpireTime()) {
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
