/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.cancellationshutdown;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author vijay
 */
class PrimeProducer implements Runnable {

    BlockingQueue<Integer> primes = null;
    boolean cancelled = false;

    PrimeProducer(BlockingQueue<Integer> primes) {
        this.primes = primes;
    }

    public void run() {
        int initial = 1;
        while (!cancelled) {
            initial = nextPrime(initial);
            try {
                primes.put(initial);
            } catch (InterruptedException ie) {
            }
        }
    }

    public void cancel() {
        cancelled = true;
    }

    private synchronized int nextPrime(int number) {
        while (!isPrime(++number));
        return number;
    }

    private boolean isPrime(int number) {
        for (int i = number - 1; i > 1; i--) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    void printPrimeNos() {
        for (int prime : primes) {
            System.out.println(prime);
        }
        System.out.println("-----------------------");
        System.out.println("Number of prime nos: " + primes.size());
    }
}

public class PrimeControllerQueueInterrupt {
    static volatile int primeCount;
    
    public static void main(String[] args) {
        BlockingQueue<Integer> primes = new LinkedBlockingQueue<Integer>();
        PrimeProducerInterrupt pp = new PrimeProducerInterrupt(primes);
        new Thread(pp).start();
        try {            
            while(isPrimeLimit(100)) {
                consumePrimes(primes);
            }

        } catch (InterruptedException ie) {
        } finally {
            pp.cancel();
        }


        pp.printPrimeNos();
    }

    static boolean isPrimeLimit(int count){
        if(count>=++primeCount){
            return true;
        }
        return false;
    }
    
    static void consumePrimes(BlockingQueue<Integer> primes) throws InterruptedException{
         System.out.println(primes.take());        
    }
}
