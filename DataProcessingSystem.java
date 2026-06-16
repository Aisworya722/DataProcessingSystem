import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

class Worker extends Thread {
    private Queue<String> taskQueue;
    private List<String> results;

    public Worker(String name, Queue<String> taskQueue, List<String> results) {
        super(name);
        this.taskQueue = taskQueue;
        this.results = results;
    }

    public void run() {
        while (true) {
            String task;

            synchronized (taskQueue) {
                if (taskQueue.isEmpty()) {
                    break;
                }
                task = taskQueue.poll();
            }

            System.out.println(getName() + " processing " + task);

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                System.out.println("ERROR: " + getName() + " was interrupted.");
                Thread.currentThread().interrupt();
            }

            synchronized (results) {
                results.add(getName() + " processed " + task);
            }
        }

        System.out.println(getName() + " completed.");
    }
}

public class DataProcessingSystem {
    public static void main(String[] args) {
        Queue<String> taskQueue = new LinkedList<>();
        List<String> results = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            taskQueue.add("Task " + i);
        }

        Worker worker1 = new Worker("Worker 1", taskQueue, results);
        Worker worker2 = new Worker("Worker 2", taskQueue, results);
        Worker worker3 = new Worker("Worker 3", taskQueue, results);

        worker1.start();
        worker2.start();
        worker3.start();

        try {
            worker1.join();
            worker2.join();
            worker3.join();
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted.");
            Thread.currentThread().interrupt();
        }

        System.out.println("All workers finished.");

        try (PrintWriter writer = new PrintWriter(new FileWriter("results.txt"))) {
            for (String result : results) {
                writer.println(result);
            }

            System.out.println("Results saved to results.txt");
        } catch (IOException e) {
            System.out.println("File error: " + e.getMessage());
        }
    }
}