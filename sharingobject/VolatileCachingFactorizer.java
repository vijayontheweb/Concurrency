/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

import java.util.ArrayList;

/**
 *
 * @author vijay
 */
public class VolatileCachingFactorizer {

    private volatile OneValueCache ovc = new OneValueCache(1, new Integer[]{1});

    public static void main(String[] args) {
        VolatileCachingFactorizer vcf = new VolatileCachingFactorizer();

        vcf.cacheFactor(4);
        vcf.cacheFactor(40);
        vcf.cacheFactor(30);
        vcf.cacheFactor(30);
        vcf.cacheFactor(3600);
    }

    public void cacheFactor(Integer i) {
        if (i.equals(ovc.getLastNumber())) {
            System.out.println("Fetching from Cache..for value " + i);
        } else {
            System.out.println("Storing onto Cache..for value " + i);
            Integer[] factors = getFactors(i);
            ovc = new OneValueCache(i, factors);
        }
        ovc.printLastFactors();
    }

    private Integer[] getFactors(Integer value) {        
        ArrayList<Integer> factors = new ArrayList<Integer>();
        for (Integer factor = 2; value > 1; factor++) {
            while (value % factor == 0) {
                factors.add(factor);
                value = value / factor;
            }
        }        
        return factors.toArray(new Integer[factors.size()]);
    }
}
