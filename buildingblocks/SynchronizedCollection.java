/*
 * The synchronized collections are  thread safe, but you may sometimes need to 
 * use additional client side locking to guard compound  actions. Common  
 * compound  actions  on  collections  include  iteration  (repeatedly  fetch  
 * elements  until  the collection  is  exhausted),  navigation  (find  the  
 * next  element  after  this  one  according  to  some  order),  and  
 * conditional operations  such  as put if absent  (check if a Map  has a 
 * mapping  for  key  K, and  if  not, add  the mapping  (K,V)). With a
 * synchronized  collection, these compound actions are  still technically  
 * thread safe even without  client
 * 
 *  The synchronized collection classes guard each method with the lock on the 
 * synchronized collection object itself.  By  acquiring  the  collection  lock  
 * we  can  make  getLast  and  deleteLast  atomic,  ensuring  that  the  size  
 * of  the Vector does not change between calling size and get. The risk that 
 * the size of the list might change between a call to size and the 
 * corresponding call to get is also present when we iterate through the elements 
 * of a Vector. The problem of unreliable iteration can again be addressed by 
 * client side locking, at some additional cost to scalability. By  holding  the  
 * Vector  lock  for  the  duration  of  iteration,  we  prevent  other  threads  
 * from modifying  the Vector while we  are  iterating  it. Unfortunately, we 
 * also prevent  other  threads from  accessing  it  at  all during this time, 
 * impairing concurrency. 
 * 
 * An alternative to locking the collection during iteration is to clone the 
 * collection and iterate the copy instead. Since the clone  is  thread confined
 * ,  no  other  thread  can  modify  it  during  iteration,  eliminating  the  
 * possibility  of ConcurrentModificationException. (The collection still must 
 * be locked during the clone operation itself.) Cloning the collection has an 
 * obvious performance cost; whether this is a favorable tradeoff depends on 
 * many factors including the size of the collection, how much work is done for 
 * each element, the relative frequency of iteration compared to other 
 * collection operations, and responsiveness and throughput requirements.
 * 
 */
package concurrency.buildingblocks;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

/**
 *
 * @author vijay
 */
public class SynchronizedCollection {
    
    private static final Vector<String> list = new Vector<String>();
    
    public static String getLast(){
        synchronized(list){
            int lastIndex = list.size()-1;
            return list.get(lastIndex);    
        }
    }
    
    public static void deleteLast(){
        synchronized(list){
            int lastIndex = list.size()-1;
            list.remove(lastIndex);
        }
    }
    
    /*
     * Hidden Iterator
     * 
     * While locking can prevent iterators from throwing ConcurrentModification
     * Exception, you have to remember to use locking  everywhere  a  shared  
     * collection  might  be  iterated.  This  is  trickier  than  it  sounds,  
     * as  iterators  are  sometimes hidden,  as  in  HiddenIterator  in  
     * Listing  5.6.  There  is  no  explicit  iteration  in  HiddenIterator,  
     * but  the  code  in  bold entails  iteration  just  the  same.  The  
     * string  concatenation  gets  turned  by  the  compiler  into  a  call  
     * to StringBuilder.append(Object), which in turn invokes the collection's 
     * toString method and the implementation of toString  in  the  standard  
     * collections  iterates  the  collection and  calls toString on  each  
     * element  to  produce  a  nicely formatted representation of the 
     * collection's contents. The addTenThings method could throw 
     * ConcurrentModificationException, because the collection is being iterated 
     * by toString in the process of preparing the debugging message. Of course, 
     * the real problem is that HiddenIterator is not thread safe; the 
     * HiddenIterator lock should be acquired before using set in the println 
     * call, but debugging and logging code commonly neglect to do this.The real 
     * lesson here is that the greater the distance between the state and the 
     * synchronization that guards it, the more likely that someone will forget 
     * to use proper synchronization when accessing that  state.If HiddenIterator 
     * wrapped the HashSet with a synchronizedSet, encapsulating the synchronization, 
     * this sort of error would not occur. Just as encapsulating an object's 
     * state makes it easier to preserve its invariants, encapsulating its 
     * synchronization makes it easier to enforce its synchronization policy. 
     * 
     */
    private final Set<Integer> set = new HashSet<Integer>();
    
    private void addTenThings(){
        for(int i=0;i<10;i++){
            set.add(i);
        }
        System.out.println(set);
    }
    
}
