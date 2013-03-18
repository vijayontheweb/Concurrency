/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Delegating Thread safety to concurrent hash map
 * 
 * Point is thread safe because it is immutable.Immutable values can be freely 
 * shared and published,so we no longer need to copy the locations when 
 * returning them. DelegatingVehicleTracker does not use any explicit 
 * synchronization; all access to state is managed by ConcurrentHashMap, and all 
 * the keys and values of the Map are immutable.
 * 
 * If a class is composed of multiple independent thread safe state variables 
 * and has no operations that have any invalid state transitions, then it can 
 * delegate thread safety to the underlying state variables. 
 * 
 * If a state variable is thread safe, does not participate in any invariants 
 * that constrain its value, and has no prohibited state transitions for any of 
 * its operations, then it can safely be published. 
 * 
 * @author vijay
 */
public class DelegatingVehicleTracker {

    private final ConcurrentHashMap<String, Point> locations;

    public DelegatingVehicleTracker(Map<String, Point> locations) {
        this.locations = new ConcurrentHashMap(locations);
    }

    public Map<String, Point> getLiveLocations() {
        return Collections.unmodifiableMap(locations);
    }
    
    public Map<String, Point> getStaticLocations() {
        return Collections.unmodifiableMap(new HashMap<String, Point>(locations));
    }

    public Point getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        Point point = locations.get(id);
        if (point == null) {
            throw new IllegalArgumentException("No such ID: " + id);
        }
        locations.replace(id, new Point(x, y));
    }
}
//Immutable
class Point {

    public final int x;
    public final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }
}