package com.github.hunter524.rxjava.start;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class ThreadChange {

    public static void main(String[] args) throws Throwable {
        Observable.just("start")
                  .observeOn(Schedulers.io())
                  .map(new Function<String, String>() {
                      @Override
                      public String apply(String s) throws Exception {
                          System.out.println("Map1 Thread:" + Thread.currentThread());
                          return s + " map1 io ";
                      }
                  })
                  .observeOn(Schedulers.computation())
                  .map(new Function<String, String>() {
                      @Override
                      public String apply(String s) throws Exception {
                          System.out.println("Map2 Thread:" + Thread.currentThread());
                          return s + "map2 computation";
                      }
                  })
                  .observeOn(Schedulers.newThread())
                  .map(new Function<String, String>() {
                      @Override
                      public String apply(String s) throws Exception {
                          System.out.println("Map3 Thread:" + Thread.currentThread());
                          return s + " map3 newThread";
                      }
                  })
                  .observeOn(Schedulers.single())
                  .map(new Function<String, String>() {
                      @Override
                      public String apply(String s) throws Exception {
                          System.out.println("Map4 Thread:" + Thread.currentThread());
                          return s + " map4 single";
                      }
                  })
                  .subscribe(new Observer<String>() {
                      @Override
                      public void onSubscribe(Disposable d) {

                      }

                      @Override
                      public void onNext(String s) {
                          System.out.println("onNext Thread:" + Thread.currentThread());
                          System.out.println("onNext:" + s);
                      }

                      @Override
                      public void onError(Throwable e) {

                      }

                      @Override
                      public void onComplete() {

                      }
                  });
        Thread.sleep(1000);
    }
}
//    OUT_PUT:
//    Map1 Thread:Thread[RxCachedThreadScheduler-1,5,main]
//    Map2 Thread:Thread[RxComputationThreadPool-1,5,main]
//    Map3 Thread:Thread[RxNewThreadScheduler-1,5,main]
//    Map4 Thread:Thread[RxSingleScheduler-1,5,main]
//    onNext Thread:Thread[RxSingleScheduler-1,5,main]
//    onNext:start map1 io map2 computation map3 newThread map4 single