/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *Monitor based vehicle tracker implementation
 * @author vijay
 */
public class MonitorVehicleTracker {
    private final Map<String,MutablePoint> locations;
    
    public MonitorVehicleTracker(Map<String,MutablePoint> locations){
        this.locations = deepCopy(locations);
    }
    
    public synchronized Map<String,MutablePoint> getLocations(){
        return deepCopy(locations);
    }
    
    public synchronized MutablePoint getLocation(String id){
        MutablePoint point = locations.get(id);
        return point==null?null:new MutablePoint(point);
    }
    
    public synchronized void setLocation(String id, int x, int y){
        MutablePoint point = locations.get(id);
        if(point == null){
            throw new IllegalArgumentException("No such ID: "+id);
        }
        point.x=x;
        point.y=y;
    }
    
    private static Map<String,MutablePoint> deepCopy(Map<String,MutablePoint> m){        
        Map<String,MutablePoint> result = new HashMap<String,MutablePoint>();  
        for(String id:m.keySet())
            result.put(id, new MutablePoint(m.get(id)));
        return Collections.unmodifiableMap(result);
    } 
}
//Not Thread Safe
class MutablePoint{
    int x;
    int y;   
    
    public MutablePoint(MutablePoint p) {
        this.x = p.x;
        this.y = p.y;
    }

}