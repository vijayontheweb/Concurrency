/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

/**
 *
 * @author vijay
 */
import java.util.*;
import java.lang.*;
import java.net.*;

public class GetDesktopPath {

    public static void main(String args[]) {
        try {
            String desktopPath = System.getProperty("user.home") + "/Desktop";
            System.out.print(desktopPath.replace("\\", "/"));
        } catch (Exception e) {
            System.out.println("Exception caught =" + e.getMessage());
        }
    }
}
