/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.cancellationshutdown;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author vijay
 */
class PrimeGenerator implements Runnable {

    List<Integer> primes = new ArrayList<Integer>();
    boolean cancelled = false;

    public void run() {
        int initial = 1;
        while (!cancelled) {
            initial = nextPrime(initial);
            synchronized (this) {
                primes.add(initial);
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
        System.out.println("Number of prime nos: "+primes.size());
    }
}

public class PrimeControllerFlag {

    public static void main(String[] args) {
        PrimeGenerator pg = new PrimeGenerator();
        new Thread(pg).start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        } finally {
            pg.cancel();
        }
        pg.printPrimeNos();
    }
}
