/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class built out of mutable underlying objects.
 * 
 * An immutable object is one whose state cannot be changed after construction.
 * Immutable objects are inherently thread-safe
 * @author vijay
 */
public class Immutable {
    private final List<String> students = new ArrayList<String>();

    public Immutable() {
        students.add("Vijay");
        students.add("Priya");
    }
    
    public static void main(String[] args){
        Immutable immutable = new Immutable();
        for(String student:immutable.students){
            System.out.println(student);
        }
        //You can still modify the state even after construction
        immutable.students.add("Meenu");
                
    }
    
}
