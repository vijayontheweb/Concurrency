package concurrency.buildingblocks;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * FutureTask acts like a latch. i.e) If FutureTask enters the completed state
 * it stays in that state forever. The behavior of Future.get depends on the 
 * state of the task. If it is completed, get returns the result immediately, 
 * and otherwise blocks until the task transitions to the completed state and 
 * then returns the result or throws an exception. Preloader uses FutureTask to 
 * perform an expensive computation whose results are needed later; by starting 
 * the computation early i.e) use startThread(), you reduce the time you would 
 * have to wait later when you actually need the results i.e) use 
 * retrieveLengthyComputation(). 
 * 
 *
 * @author vijay
 */
public class Preloader {
    LengthyComputation lc = new LengthyComputation();//implements Callable
    FutureTask<String> ft = new FutureTask<String>(lc);//implements Runnable
    
    public static void main(String[] args){
        Preloader p = new Preloader();        
        p.startThread();
        p.retrieveLengthyComputation();
    }
    
    public void startThread(){
        Thread t = new Thread(ft);
        t.start();
    }
    
    public void retrieveLengthyComputation(){                
        try{
            String result = ft.get();
            System.out.println(result);            
        }catch(InterruptedException ie){
        }catch(ExecutionException ie){
        }
    }   
}


class LengthyComputation implements Callable{
    public String call() throws InterruptedException{
        Thread.sleep(1000);
        return "COMPUTED AFTER 1 SEC";
    }
}