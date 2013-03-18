/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

import java.util.concurrent.CountDownLatch;

/**
 *
 * @author vijay
 */

class someTask implements Runnable{
    public void run(){
        for(int i=0;i<10;i++){
            System.out.println("HolyCow");                    
        }
    }
}

public class TestHarness {
    
    public static void main(String args[]) throws InterruptedException{        
        long elapsedTime = timeTasks(new someTask(),10);
        System.out.println("ELAPSED TIME IS:"+elapsedTime);
        System.exit(0);
    }

    private static long timeTasks(final Runnable task, int nThreads) throws InterruptedException{
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
        long startTime = System.nanoTime();
        startGate.countDown();
        endGate.await();
        long endTime = System.nanoTime();
        return endTime - startTime;
    }
}
