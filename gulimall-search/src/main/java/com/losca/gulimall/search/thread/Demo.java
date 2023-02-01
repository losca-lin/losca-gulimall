package com.losca.gulimall.search.thread;

import java.util.concurrent.*;

/**
 * 线程的四种方式
 */
public class Demo {
    public static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("main start。。。");
        //CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
        //    int i = 10 / 2;
        //    System.out.println(i);
        //}, executorService);
        //CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
        //    int i = 10 / 0;
        //    System.out.println(i);
        //    return i;
        //}, executorService).whenComplete((res, exc) -> {
        //    //感知异常，不能处理异常
        //    System.out.println("处理结果" + res);
        //    System.out.println("异常为" + exc);
        //}).exceptionally((exc) -> {
        //    //可以感知异常，同时返回默认值
        //    return 10;
        //});
        //CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
        //    int i = 10 / 4;
        //    System.out.println(i + "当前线程为" + Thread.currentThread().getId());
        //    return i;
        //}, executorService).thenApplyAsync((res) -> {
        //    System.out.println(res + "当前线程为" + Thread.currentThread().getId());
        //    return res*2;
        //}
        //CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
        //    int i = 10 / 4;
        //    System.out.println(i + "当前线程为" + Thread.currentThread().getId());
        //    return i;
        //}, executorService).thenApplyAsync((res) -> {
        //    System.out.println(res + "当前线程为" + Thread.currentThread().getId());
        //    return "hello" + res;
        //}, executorService);
        CompletableFuture<Object> future1 = CompletableFuture.supplyAsync(() -> {
            int i = 10 / 2;
            System.out.println(i + "当前的线程为" + Thread.currentThread().getId());
            System.out.println("任务1");
            return i;
        }, executorService);
        CompletableFuture<Object> future2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3_000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("任务2" + "当前的线程为" + Thread.currentThread().getId());
            return "任务二";
        }, executorService);
        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("任务3" + "当前的线程为" + Thread.currentThread().getId());
            return "任务三";
        }, executorService);
        //CompletableFuture<Void> future = CompletableFuture.allOf(future1, future2, future3);
        CompletableFuture<Object> future = CompletableFuture.anyOf(future1, future2, future3);
        //CompletableFuture<Void> future = future1.runAfterBothAsync(future2, () -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId());
        //}, executorService);
        //CompletableFuture<Void> future = future1.thenAcceptBothAsync(future2, (f1, f2) -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId() + f1 + "=>" + f2);
        //}, executorService);
        //CompletableFuture<String> future = future1.thenCombineAsync(future2, (f1, f2) -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId() + f1 + "=>" + f2);
        //    return f1 + f2 + "haha";
        //}, executorService);
        //CompletableFuture<Void> future = future1.runAfterEitherAsync(future2, () -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId());
        //}, executorService);
        //CompletableFuture<Void> future = future1.acceptEitherAsync(future2, (res) -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId() + "=>" + res);
        //}, executorService);
        //CompletableFuture<Integer> future = future1.applyToEitherAsync(future2, (res) -> {
        //    System.out.println("任务三" + "当前的线程为" + Thread.currentThread().getId() + "=>" + res);
        //    return (int) res * 3;
        //}, executorService);
        System.out.println("main end。。。" + future.get());
    }

    public static void threadTest(String[] args) throws ExecutionException, InterruptedException {
        System.out.println("主线程开始了");
        //MyThread myThread = new MyThread();
        //myThread.start();
        //Thread thread = new Thread(new MyThread2());
        //thread.start();
        //FutureTask<Integer> futureTask = new FutureTask<Integer>(new MyThread3());
        //Thread thread = new Thread(futureTask);
        //thread.start();
        ////阻塞等待 拿到返回结果才可以进行
        //Integer result = futureTask.get();
        //service.execute(new MyThread2());
        //Future<Integer> submit = service.submit(new MyThread3());
        //Integer res = submit.get();
        //service.execute(new MyThread2());
        //new ThreadPoolExecutor(5,
        //        200,
        //        30,
        //        TimeUnit.SECONDS,
        //        new LinkedBlockingQueue<>(),
        //        Executors.defaultThreadFactory(),
        //        new ThreadPoolExecutor.AbortPolicy());
        System.out.println("主线程结束了");
    }

    //继承Thread类，new出这个对象 myThread.start();启动多线程
    public static class MyThread extends Thread {
        @Override
        public void run() {
            int i = 10 / 2;
            System.out.println(i);
        }
    }

    //实现Runnable接口 Thread thread = new Thread(new MyThread2());  thread.start();启动多线程
    public static class MyThread2 implements Runnable {
        @Override
        public void run() {
            int i = 10 / 2;
            System.out.println(i);
        }
    }

    //实现Runnable接口，有返回值
    public static class MyThread3 implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            int i = 10 / 2;
            System.out.println(i);
            return i;

        }
    }
}

