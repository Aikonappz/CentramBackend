package com.centram.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Batch {
    /*public static void main(String[] args) {
        System.out.println("=============EXAMPLE 1===============");
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task " + i);
            System.out.println("Created : " + task.getName());
            threadPoolExecutor.execute(task);
        }
        threadPoolExecutor.shutdown();
    }*/

    /*public static void main(String[] args) {
        System.out.println("=============EXAMPLE 2===============");
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
        Task task = new Task("Repeat Task");
        System.out.println("Created : " + task.getName());
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(task, 2, 2, TimeUnit.SECONDS);
    }*/
   /* public static void main(String[] args) throws Exception {
        // FutureTask is a concrete class that
        // implements both Runnable and Future
        FutureTask[] randomNumberTasks = new FutureTask[5];
        for (int i = 0; i < 5; i++) {
            Task1 task1 = new Task1();
            // Create the FutureTask with Callable
            randomNumberTasks[i] = new FutureTask(task1);
            // As it implements Runnable, create Thread
            // with FutureTask
            Thread t = new Thread(randomNumberTasks[i]);
            t.start();
        }
        for (int i = 0; i < 5; i++) {
            // As it implements Future, we can call get()
            System.out.println(randomNumberTasks[i].get());
            // This method blocks till the result is obtained
            // The get method can throw checked exceptions
            // like when it is interrupted. This is the reason
            // for adding the throws clause to main
        }
    }*/

    public static void main(String[] args) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        List<Future<Integer>> resultList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 4; i++) {
            Integer number = random.nextInt(10);
            Task2 calculator = new Task2(number);
            Future<Integer> result = executor.submit(calculator);
            resultList.add(result);
        }
        for (Future<Integer> future : resultList) {
            try {
                System.out.println("Future result is - " + " - " + future.get() + "; And Task done is " + future.isDone());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        //shut down the executor service now
        executor.shutdown();
    }
}
