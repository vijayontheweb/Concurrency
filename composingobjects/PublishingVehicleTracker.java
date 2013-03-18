/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.composingobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//ThreadSafe
/**
 * Vehicle Tracker that safely publishes the underlying state
 * 
 *
 * 
 * @author vijay
 */
public class PublishingVehicleTracker {

    private final Map<String, SafePoint> locations;

    public PublishingVehicleTracker(Map<String, SafePoint> locations) {
        this.locations = new ConcurrentHashMap(locations);
    }

    public Map<String, SafePoint> getLiveLocations() {
        return Collections.unmodifiableMap(locations);
    }

    public Map<String, SafePoint> getStaticLocations() {
        return Collections.unmodifiableMap(new HashMap<String, SafePoint>(locations));
    }

    public SafePoint getLocation(String id) {
        return locations.get(id);
    }

    public void setLocation(String id, int x, int y) {
        SafePoint safePoint = locations.get(id);
        if (safePoint == null) {
            throw new IllegalArgumentException("No such ID: " + id);
        }
        locations.get(id).set(x, y);
    }
}

class SafePoint {

    private int x, y;

    private SafePoint(int[] a) {
        this(a[0], a[1]);
    }

    public SafePoint(SafePoint p) {
        this(p.get());
    }

    public SafePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public synchronized int[] get() {
        return new int[]{x, y};
    }

    public synchronized void set(int x, int y) {
        this.x = x;
        this.y = y;
    }
}