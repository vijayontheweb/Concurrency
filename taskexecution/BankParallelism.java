/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.taskexecution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.Integer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;

/**
 *
 * @author vijay
 */
public class BankParallelism {

    static ExecutorService executor = null;
    static Map<Integer, Integer> stepDelay = new HashMap<Integer, Integer>();
    static int fireCount = 0;

    public static void main(String[] args) throws IOException {
        executor = Executors.newFixedThreadPool(5);
        CompletionService compService = new ExecutorCompletionService(
                executor);

        ServerSocket socket = new ServerSocket(1234);
        while (!executor.isShutdown()) {
            try {
                fireCount = 0;
                final Socket remote = socket.accept();
                System.out.println("Connection, sending data.");
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        remote.getInputStream()));
                String str = in.readLine();
                String actionStr = str.substring(str.indexOf("action=") + 7, str.indexOf("action=") + 11);
                if ("fire".equals(actionStr)) {
                    for (int count = 0; count < 25; count++) {
                        Callable callable = new Callable() {

                            public String call() {
                                return getCell(fireCount++);
                            }
                        };
                        compService.submit(callable);
                    }
                }
                outWrite(remote, compService);
                /*for (int i = 0; i < 25; i++) {
                try {
                Future<String> f = compService.take();
                outWrite(remote, f.get());
                } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                System.out.println("REJECTED" + e.getCause());
                }
                }*/
                remote.close();
            } catch (RejectedExecutionException e) {
                if (!executor.isShutdown()) {
                    System.out.println("REJECTED");
                }
            }
        }
        //outWrite(socket.accept(), "Out Of Service");
    }

    static String getCell(int fireCount) {
        int quotient = (int) Math.ceil(fireCount / 5);
        int remainder = fireCount % 5;
        if (remainder == 0) {
            remainder = 5;
        }
        String cellName = "R" + (quotient + 1) + "C" + remainder;

        try {
            Random random = new Random();
            int sleepTime = random.nextInt(10000);
            System.out.println("Sleep Time for " + cellName + " is " + sleepTime);
            Thread.currentThread().sleep(sleepTime);
        } catch (InterruptedException ie) {
        }
        return cellName;
    }

    static void outWrite(Socket remote, CompletionService compService) {
        try {
            PrintWriter out = new PrintWriter(remote.getOutputStream(), true);
            out.println("HTTP/1.1 200 OK");
            out.println("Content-Type: text/html");
            out.println("Transfer-Encoding: chunked");
            out.println("Cache-Control: no-cache");
            out.println("Server: Bot");
            // this blank line signals the end of the headers
            out.println("");
            // Send the HTML page            
            for (int i = 0; i < 25; i++) {
                try {
                    Future<String> f = compService.take();
                    String value = f.get();
                    out.print(value.length() + "\r\n");
                    out.print(value + "\r\n");                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    System.out.println("REJECTED" + e.getCause());
                }
            }
            out.print("0\r\n");
            out.flush();
        } catch (IOException io) {
        }
    }
}
