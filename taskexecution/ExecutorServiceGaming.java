/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.taskexecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;

/**
 *
 * @author vijay
 */
public class ExecutorServiceGaming {

    static ExecutorService executor = null;

    public static void main(String[] args) throws IOException {
        executor = Executors.newFixedThreadPool(5);
        ServerSocket socket = new ServerSocket(1234);
        while (!executor.isShutdown()) {
            try {
                final Socket remote = socket.accept();
                Runnable runnable = new Runnable() {

                    public void run() {
                        handleRequest(remote);
                    }
                };
                executor.execute(runnable);
            } catch (RejectedExecutionException e) {
                if (!executor.isShutdown()) {
                    System.out.println("REJECTED");
                }
            }
        }
        outWrite(socket.accept(), "Out Of Service");
    }

    static void handleRequest(Socket remote) {
        try {
            System.out.println("Connection, sending data.");
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    remote.getInputStream()));
            String str = in.readLine();
            String actionStr = str.substring(str.lastIndexOf("action=")+7, str.lastIndexOf("action=")+11);
            String resultStr = null;
            if ("fire".equals(actionStr)) {
                String counterStr = str.substring(str.indexOf("value=") + 6, str.length() - 9);
                int counterInt = 0;
                try {
                    counterInt = Integer.parseInt(counterStr);
                } catch (NumberFormatException e) {
                    counterStr = "BAD INPUT";
                }
                counterInt++;
                if (!"BAD INPUT".equals(counterStr)) {
                    counterStr = Integer.toString(counterInt);
                }
                resultStr = counterStr;
            } else if ("kill".equals(actionStr)) {
                executor.shutdown();
                resultStr = "SHUTDOWN";
            }
            outWrite(remote, resultStr);

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

    }

    static void outWrite(Socket remote, String resultStr) {
        try {
            PrintWriter out = new PrintWriter(remote.getOutputStream());
            out.println("HTTP/1.0 200 OK");
            out.println("Content-Type: text/html");
            out.println("Server: Bot");
            // this blank line signals the end of the headers
            out.println("");
            // Send the HTML page
            out.println(resultStr);
            out.flush();
            remote.close();
        } catch (IOException io) {
        }
    }
}
