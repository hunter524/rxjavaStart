package com.github.hunter524.rxjava.start;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;

public class RxAsyncToSync {
    public static void main(String[] args) throws Throwable {
//        异步的 Observable
        Observable<String> one = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                Thread.sleep(1000);
                emitter.onNext("one");
                emitter.onNext("two");
                emitter.onComplete();
            }
        }).subscribeOn(Schedulers.io());
        System.out.println("one async start subscribe @"+System.currentTimeMillis());
        one.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                System.out.println("one async onNext @"+System.currentTimeMillis()+"Value:"+s);
            }
        });
        System.out.println("one async end subscribe @"+System.currentTimeMillis());

//        异步的 Observable 使其同步的返回数据
        System.out.println("one sync block start Get @"+System.currentTimeMillis());
        List<String> blockingGet = one.toList().blockingGet();
        System.out.println("one sync block end Get @"+System.currentTimeMillis()+"Values:"+ Arrays.deepToString(blockingGet.toArray()));

        Thread.sleep(3000);
    }
}
