package com.xy.search.thread.test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadTest {
    public static ExecutorService  executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws  Exception{
        System.out.println("方法开始");
//        CompletableFuture.runAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/5;
//            System.out.println("返回结果："+i);
//
//        },executorService);

        /**
         * 方法完成后的感知
         */
//        CompletableFuture<Integer> futre = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/0;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService).whenComplete((result,exception)->{
//            System.out.println("结果是："+result);
//            //虽然能得到异常，但是 没有默认返回值
//            System.out.println("异常是："+exception);
//        }).exceptionally((th)->{
//            System.out.println(th.getMessage());
//            //能得到异常 且能有一个默认返回值(对结果进行修改)，程序不会崩
//            return 10;
//        });

        //方法完成后的处理[supplyAsync 入果加个Async 下一个执行方法会调用一个新的线程 不加就是同一个线程调用]
//        CompletableFuture<Integer> futre = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/0;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService).handle((Integer integer,Throwable throwable)->{
//            return integer==null?0:integer*2;
//        });


        //then有三种：A.thenRun B B不会获取A的返回值， A.thenAccept B B会接收A的返回值   A。thenApply B B可以使用A的返回值 同时 B经过处理还可以返回新的结果
        /**
         * thenRun 不能获取上一部的执行结果
         *
         * thenAccept 能获取上一部的执行结果 但是不会返回任何结果
         *
         *thenApply 能获取上一部的执行结果 同时会返回结果 (接收参数和返回参数的类型可以不同)
         */

//        CompletableFuture<String> futre = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService).thenApplyAsync((Integer t)->{
//            return t*2+"";
//        },executorService);

        //任务组合都成功才执行action有三种：
        /**
         * A.runAfterBothAsync B A,B都执行完 会调用收尾的方法， 收尾的方法 不能获得A，B的返回值，且 任务完成不会返回值
         *
         * thenAcceptBothAsync A,B都执行完 会调用收尾的方法， 收尾的方法 能获得A，B的返回值，并根据返回值进行处理且 任务完成不会返回值
         *
         *thenCombineAsync 能获取上一部的执行结果 同时会返回结果 (接收参数和返回参数的类型可以不同)
         */

//        CompletableFuture<String> futre = CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService).thenCombineAsync(CompletableFuture.supplyAsync(()->{
//           System.out.println("当前线程："+Thread.currentThread().getId());
//           int i = 10/2;
//           System.out.println("返回结果："+i);
//           return i+"";
//       },executorService),(Integer integer,String two)->{
//            System.out.println("全部成功"+integer+two);
//            return "aaa";
//       },executorService);


        //任务组合有一个成功就会执行action也有三种：
        /**
         * A.runAfterBothAsync B A,B任意一个执行完 会调用收尾的方法， 收尾的方法 不能获得A，B的返回值，且 任务完成不会返回值
         *
         * acceptEitherAsync A,B任意一个执行完 会调用收尾的方法， 收尾的方法 能获得A，B其中一个成功的返回值（A,B的返回值类型必须要相同），并根据返回值进行处理且 任务完成不会返回值
         *
         *applyToEitherAsync A,B任意一个执行完 会调用收尾的方法， 收尾的方法 能获得A，B其中一个成功的返回值（A,B的返回值类型必须要相同），并根据返回值进行处理且 任务完成会返回值
         */
//        CompletableFuture<Integer> futre =  CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService).applyToEitherAsync(CompletableFuture.supplyAsync(()->{
//            System.out.println("当前线程："+Thread.currentThread().getId());
//            int i = 10/2;
//            System.out.println("返回结果："+i);
//            return i;
//        },executorService),(res)->{
//            System.out.println("全部成功"+res);
//            return res;
//        },executorService);


        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(()->{
            System.out.println("获取产品图片1");
            return "succ";
        },executorService);

        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(()->{
            System.out.println("获取产品图片2");
            return "succ";
        },executorService);

        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(()->{
            System.out.println("获取产品图片3");
            return "succ";
        },executorService);


        //CompletableFuture.allOf  f1,f2,f3 都执行完 才能有返回值 f1 f2 f3 都能有各自的返回值 所以最后总的返回值可以为空
        //CompletableFuture<Void> allRes =  CompletableFuture.allOf(f1,f2,f3);

        //CompletableFuture.allOf  f1,f2,f3 有一个执行完 就能有返回值 返回值 就是这三个线程 完成后饭后的对象 只能是object 因为每个线程的返回值可能不同 所以只会 返回成功的值
        CompletableFuture<Object> allRes =  CompletableFuture.anyOf(f1,f2,f3);

        System.out.println("方法结束"+allRes.get());
    }

}
