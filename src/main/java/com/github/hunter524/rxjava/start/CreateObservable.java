package com.github.hunter524.rxjava.start;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;

public class CreateObservable {
    public static void main(String[] args) {
        Observable.create(
                new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                        ObservableEmitter<String> serialize = emitter.serialize();
                        serialize.onNext("one");
                        serialize.onNext("two");
                        serialize.onNext("three");
                        serialize.onComplete();
                    }
                })
                  .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String s) throws Exception {
                          System.out.println("Accetp Data From ObservableEmitter:" + s);
                      }
                  });

        Observable.just("one")
                  .subscribe(new Consumer<String>() {
                      @Override
                      public void accept(String s) throws Exception {
                          System.out.println("Accetp Data From ObservableEmitter:" + s);
                      }
                  });
    }
}
