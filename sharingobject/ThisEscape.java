/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

import java.awt.Event;

/**
 *
 * @author vijay
 */
public class ThisEscape {
    public ThisEscape(Event event){
        //When you try to publish an inner class instance, You are implicitly allowing this reference to escape. Don't do this.
    }
}
