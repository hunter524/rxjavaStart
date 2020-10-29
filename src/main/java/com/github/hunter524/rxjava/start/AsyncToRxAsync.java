package com.github.hunter524.rxjava.start;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncToRxAsync {

    public static ExecutorService CACHED_EXECUTOR_SERVICE = Executors.newCachedThreadPool();


    public static void main(String[] args) throws Throwable {
        Future<String> future = CACHED_EXECUTOR_SERVICE.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                Thread.sleep(1000);
//                throw new IllegalArgumentException("args illegal!");
                return "callable return";
            }
        });
        System.out.println("subscribe @:" + System.currentTimeMillis());
        Observable.fromFuture(future, Schedulers.io())
                  .subscribe(new Observer<String>() {
                      @Override
                      public void onSubscribe(Disposable d) {

                      }

                      @Override
                      public void onNext(String s) {
                          System.out.println("onNext @:" + System.currentTimeMillis() + "Value:" + s);
                      }

                      @Override
                      public void onError(Throwable e) {
                          e.printStackTrace();
//                          e.getCause().printStackTrace();
                      }

                      @Override
                      public void onComplete() {

                      }
                  });
    }
}
