/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

import java.util.Arrays;

/**
 *
 * @author vijay
 */
class OneValueCache {

    private final Integer lastNumber;    
    private final Integer[] lastFactors;

    public OneValueCache(Integer i,
            Integer[] factors) {
        lastNumber = i;
        lastFactors = Arrays.copyOf(factors, factors.length);
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public Integer[] getLastFactors() {
        return lastFactors;
    }
    
    public void printLastFactors(){
        System.out.print("Factors are ->");
        for(int i=0;i<lastFactors.length;i++){
            System.out.print(lastFactors[i]+" ");
        }
        System.out.println("");
    }
    
    public Integer[] getFactors(Integer i) {
        if (lastNumber == null || !lastNumber.equals(i)) {
            return null;
        } else {
            return Arrays.copyOf(lastFactors, lastFactors.length);
        }
    }
}
