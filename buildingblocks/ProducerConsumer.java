package concurrency.buildingblocks;

import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Threads often have to coordinate their actions. The most common coordination 
 * idiom is the guarded block. Such a block begins by polling a condition that 
 * must be true before the block can proceed. Such a method could, in theory, 
 * simply loop until the condition is satisfied, but that loop is wasteful, 
 * since it executes continuously while waiting.A more efficient guard invokes 
 * Object.wait to suspend the current thread. The invocation of wait does not 
 * return until another thread has issued a notification that some special event 
 * may have occurred — though not necessarily the event this thread is waiting 
 * for
 * Note: Always invoke wait inside a loop that tests for the condition being 
 * waited for. Don't assume that the interrupt was for the particular condition 
 * you were waiting for, or that the condition is still true.Let's use guarded 
 * blocks to create a Producer-Consumer application. This kind of application 
 * shares data between two threads: the producer, that creates the data, and the 
 * consumer, that does something with it. The two threads communicate using a 
 * shared object. Coordination is essential: the consumer thread must not attempt 
 * to retrieve the data before the producer thread has delivered it, and the 
 * producer thread must not attempt to deliver new data if the consumer hasn't 
 * retrieved the old data.
 * 
 * @author vijay
 */
public class ProducerConsumer {

    Queue<Integer> queue = new PriorityQueue<Integer>();
    static final int MAX_SIZE = 3;
    static int ITEM_COUNT;
    private static ProducerConsumer pc;

    public static void main(String[] args) {
        pc = new ProducerConsumer();
        pc.execute();
    }

    void execute() {
        new Thread(new Producer()).start();
        new Thread(new Consumer()).start();
    }

    class Producer implements Runnable {

        public synchronized void run() {
            synchronized (pc) {
                while (true) {
                    while (queue.size() == MAX_SIZE) {
                        try {
                            pc.wait();
                            System.out.println("Producer Notified By Consumer. Yippee!!");
                        } catch (InterruptedException ie) {
                        }
                    }
                    try {
                        Thread.currentThread().sleep(1);//To simulate varying rates of production and consumption
                    } catch (InterruptedException ie) {
                    }
                    queue.add(new Integer(++ITEM_COUNT));
                    System.out.println("PRODUCER->Number:" + ITEM_COUNT + " added. Queue Size=" + queue.size());
                    pc.notifyAll();
                }
            }
        }
    }

    class Consumer implements Runnable {

        public void run() {
            synchronized (pc) {
                while (true) {
                    while (queue.size() == 0) {
                        try {
                            pc.wait();
                            System.out.println("Consumer Notified By Producer. Yippee!!");
                        } catch (InterruptedException ie) {
                        }
                    }
                    try {
                        Thread.currentThread().sleep(500);//To simulate varying rates of production and consumption
                    } catch (InterruptedException ie) {
                    }
                    System.out.println("CONSUMER->Number:" + queue.remove() + " returned. Queue Size=" + queue.size());
                    pc.notifyAll();
                }
            }
        }
    }
}
