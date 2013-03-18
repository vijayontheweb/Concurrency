/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.threadsafety;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * @author vijay
 */
public class UnsafeCachingFactorizer {

    private final AtomicReference<Integer> lastNumber = new AtomicReference<Integer>();
    private final AtomicReference<ArrayList<Integer>> lastFactors = new AtomicReference<ArrayList<Integer>>();

    public static void main(String[] args){
        UnsafeCachingFactorizer ucf = new UnsafeCachingFactorizer();
        
        System.out.println(ucf.cacheFactor(4));
        System.out.println(ucf.cacheFactor(4));
        System.out.println(ucf.cacheFactor(40));
        System.out.println(ucf.cacheFactor(30));
        System.out.println(ucf.cacheFactor(3600));
    }
    
    public ArrayList<Integer> cacheFactor(Integer i) {        
        if (i.equals(lastNumber.get())) {
            System.out.println("Fetching from Cache..for value "+i);
            return lastFactors.get();
        } else {
            System.out.println("Storing onto Cache..for value "+i);
            ArrayList<Integer> factors = getFactors(i);
            lastNumber.set(i);
            lastFactors.set(factors);
            return lastFactors.get();
        }
    }
    
    private ArrayList<Integer> getFactors(Integer value){
        ArrayList<Integer> factors = new ArrayList<Integer>();
        for(Integer factor=2;value>1;factor++){
           while(value%factor==0){
               factors.add(factor);
               value=value/factor;
           }             
        }
        return factors;
    }
}
