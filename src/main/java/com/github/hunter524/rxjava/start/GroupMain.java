package com.github.hunter524.rxjava.start;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observables.GroupedObservable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupMain {

    private static Integer[] SOURCE_DATA = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9};

    public static void main(String[] args) {
        Observable.fromArray(SOURCE_DATA)
                  .groupBy(new Function<Integer, String>() {
                      @Override
                      public String apply(Integer integer) throws Exception {
                          String isOdd = "odd";
                          if (integer % 2 == 0) {
                              isOdd = "even";
                          }
                          return isOdd;
                      }
                  })
                  .flatMap(new Function<GroupedObservable<String, Integer>, Observable<List<Integer>>>() {

                      @Override
                      public Observable<List<Integer>> apply(GroupedObservable<String, Integer> stringIntegerGroupedObservable) throws Exception {
                          return stringIntegerGroupedObservable.toList()
                                                               .toObservable();
                      }
                  })
                  .subscribe(new Consumer<List<Integer>>() {
                      @Override
                      public void accept(List<Integer> integers) throws Exception {
                          System.out.println(Arrays.deepToString(integers.toArray()));
                      }
                  });
    }
}
