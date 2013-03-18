/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.buildingblocks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author vijay
 */
class FileCrawler implements Runnable {

    private final BlockingQueue<File> queue;
    private final File root;
    private final FileFilter filter;

    public FileCrawler(BlockingQueue<File> queue, File root, FileFilter filter) {
        this.queue = queue;
        this.root = root;
        this.filter = filter;
    }

    public void run() {
        try {
            crawl(root);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void crawl(File root) throws InterruptedException {
        File[] files = root.listFiles(filter);
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    crawl(f);
                } else {
                    queue.put(f);
                }
            }
        }
    }
}

class Indexer implements Runnable {

    BlockingQueue<File> queue;

    public Indexer(BlockingQueue<File> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            while (true) {
                indexFile(queue.take());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void indexFile(File f) throws InterruptedException {
        System.out.println("File " + f.getName() + " is indexed");
    }
}

public class DesktopSearch {

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    static BlockingQueue<File> bQueue = new LinkedBlockingQueue<File>();

    public static void main(String[] args) throws IOException {
        System.out.println("Please enter Directory Name");
        //String directory = in.readLine();
        String directoryStr = "C:\\MYSTUFF\\TODELETE\\FILES";
        System.out.println("Please enter Filter Criteria");
        //String filter = in.readLine();
        String filter = ".java";
        startIndexing(directoryStr, filter);
    }

    private static void startIndexing(String directoryStr, final String filter) {
        File directory = new File(directoryStr);
        FileFilter fileFilter = new FileFilter() {

            public boolean accept(File file) {
                //return file.getName().toLowerCase().endsWith(filter);
                return true;
            }
        };
        for (File file : directory.listFiles()) {
            new Thread(new FileCrawler(bQueue, file, fileFilter)).start();
        }
        for (int i = 0; i < 10; i++) {
            new Thread(new Indexer(bQueue)).start();
        }

    }
}
