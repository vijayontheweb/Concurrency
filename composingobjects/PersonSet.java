/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.HashSet;
import java.util.Set;

/**
 * Encapsulating data within an object confines access to the data to the 
 * object's methods making it easier to ensure that the data is always accessed
 * with the appropriate lock held
 * 
 * This example illustrates confinement to ensure thread safety.
 */
class Person{
}

public class PersonSet {
    private final Set<Person> mySet = new HashSet<Person>();
    
    public synchronized void addPerson(Person p){
        mySet.add(p);
    }
    
    public synchronized boolean containsPerson(Person p){
       return mySet.contains(p);
    }
}
