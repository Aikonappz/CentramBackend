package com.centram.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

public class Batch {
     /*System.out.println("=============Start ThreadPoolExecutor===============");
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        for (int i = 1; i <= 5; i++) {
            Task task = new Task("Task ".concat(String.valueOf(i)));
            System.out.println("Created : " + task.getName());
            threadPoolExecutor.execute(task);
        }
        threadPoolExecutor.shutdown();*/

        /*System.out.println("=============Start ScheduledThreadPoolExecutor===============");
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
        Task task = new Task("Repeat Task");
        System.out.println("Created : ".concat(task.getName()));
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(task, 2, 2, TimeUnit.SECONDS);*/

        /*System.out.println("=============Start FutureTask===============");
        // FutureTask is a concrete class that implements both Runnable and Future
        List<FutureTask> futureTaskList = new LinkedList<FutureTask>();
        for (int i = 0; i < 5; i++) {
            // Create the FutureTask with Callable
            futureTaskList.add(new FutureTask(new Task1()));
            // As it implements Runnable, create Thread
            // with FutureTask
            new Thread(futureTaskList.get(i)).start();
        }
        for (FutureTask futureTask : futureTaskList) {
            // As it implements Future, we can call get()
            System.out.println(futureTaskList.indexOf(futureTask) + " => " + futureTask.get());
            // This method blocks till the result is obtained
            // The get method can throw checked exceptions
            // like when it is interrupted. This is the reason
            // for adding the throws clause to main
        }*/

        /*System.out.println("=============Start ThreadPoolExecutor With Future return value===============");
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        Map<Integer, Future<Integer>> futureMap = new LinkedHashMap<Integer, Future<Integer>>();
        Task2 task2 = null;
        for (int i = 0; i < 4; i++) {
            task2 = new Task2(new Random().nextInt(10));
            futureMap.put(task2.getNumber(), executor.submit(task2));
        }
        for (Map.Entry<Integer, Future<Integer>> entry : futureMap.entrySet()) {
            System.out.println("Future value of ".concat(String.valueOf(entry.getKey())).concat(" factorial is - ").concat(" - ").concat(String.valueOf(entry.getValue().get())).concat("; and Task done is ").concat(String.valueOf(entry.getValue().isDone())));
        }
        //shut down the executor service now
        executor.shutdown();*/

        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            public String get() {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new IllegalStateException(e);
                }
                return "Result of the asynchronous computation";
            }
        });

        // Block and get the result of the Future
        String result = future.get();
        System.out.println(result);
    }
}
