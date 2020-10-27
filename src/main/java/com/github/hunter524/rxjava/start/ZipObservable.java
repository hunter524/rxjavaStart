package com.github.hunter524.rxjava.start;

import com.google.common.collect.Lists;
import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//要实现 先请求个人信息,然后并发请求交易概括和交易详情,然后将交易详情组合进入交易概括内部
//用 java 实现则需要使用 CountDownLatch
public class ZipObservable {

    public static ExecutorService CACHED_EXECUTOR_SERVICE = Executors.newCachedThreadPool();


    public static void main(String[] args) throws InterruptedException {

//        java 实现
        final List<String>[] overView = new List[]{null};
        final List<String>[] tradeDetail = new List[]{null};

        CountDownLatch countDownLatch = new CountDownLatch(2);
        getPersonInfo("acc", "pwd", new GetPersonCallBack() {
            @Override
            public void onSucces(PersonInfo personInfo) {
                getTradeDetails(personInfo.token, new GetTradeDetailsCallBack() {
                    @Override
                    public void onSucces(TradeDetails tradedetails) {
                        tradeDetail[0] = tradedetails.details;
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError() {

                    }
                });

                getTradeOverView(personInfo.token, new GetTradeOverViewCallBack() {
                    @Override
                    public void onSucces(TradeOverView tradeoverview) {
                        overView[0] = tradeoverview.overviews;
                        countDownLatch.countDown();
                    }

                    @Override
                    public void onError() {

                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        countDownLatch.await();
//        合并 detail 进入 Overview
        System.out.println("OverView:" + Arrays.deepToString(overView[0].toArray()) + "Details:" + Arrays.deepToString(tradeDetail[0].toArray()));

//        rxjava 实现
        getPersonInfoOb("acc", "pwd")
                .flatMap(
                        new Function<PersonInfo, Observable<String>>() {
                            @Override
                            public Observable<String> apply(PersonInfo personInfo) throws Exception {
                                return Observable.zip(getTradeOverViewOb(personInfo.token), getTradeDetailOb(personInfo.token),
                                        new BiFunction<TradeOverView, TradeDetails, String>() {
                                            @Override
                                            public String apply(TradeOverView tradeOverView, TradeDetails tradeDetails) throws Exception {
                                                System.out.println("Rx OverView:" + Arrays.deepToString(tradeOverView.overviews.toArray())
                                                        + "Details:" + Arrays.deepToString(tradeDetails.details.toArray()));

                                                return "success";
                                            }
                                        });
                            }
                        })
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        System.out.println("RxResult:" + s);
                    }
                });

    }

    public static class PersonInfo {

        public String token;

        public PersonInfo(String token) {
            this.token = token;
        }
    }

    public static class TradeOverView {
        public List<String> overviews;

        public TradeOverView(List<String> overviews) {
            this.overviews = overviews;
        }
    }

    public static class TradeDetails {
        public List<String> details;

        public TradeDetails(List<String> details) {
            this.details = details;
        }
    }

    public interface GetTradeOverViewCallBack {
        public void onSucces(TradeOverView tradeoverview);

        public void onError();
    }

    public interface GetTradeDetailsCallBack {
        public void onSucces(TradeDetails tradedetails);

        public void onError();
    }

    public interface GetPersonCallBack {
        public void onSucces(PersonInfo personInfo);

        public void onError();
    }

    public static void getPersonInfo(String acc, String pwd, GetPersonCallBack getPersonCallBack) {
        CACHED_EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getPersonCallBack.onSucces(new PersonInfo("token"));
            }
        });
    }

    public static void getTradeOverView(String token, GetTradeOverViewCallBack getTradeOverViewCallBack) {
        CACHED_EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTradeOverViewCallBack.onSucces(new TradeOverView(Lists.newArrayList("over 11", "over 22", "over 33")));
            }
        });
    }

    public static void getTradeDetails(String token, GetTradeDetailsCallBack getTradeDetailsCallBack) {
        CACHED_EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTradeDetailsCallBack.onSucces(new TradeDetails(Lists.newArrayList("detail 11", "detail 22", "detail 33")));
            }
        });
    }


    //    新写的接口直接 rxjava 模式

    public static Observable<PersonInfo> getPersonInfoOb(String acc, String pwd) {
        return Observable.just("start")
                         .map(new Function<String, PersonInfo>() {
                             @Override
                             public PersonInfo apply(String s) throws Exception {
                                 Thread.sleep(1000);
                                 return new PersonInfo("token");
                             }
                         })
                         .subscribeOn(Schedulers.io());
    }

    public static Observable<TradeOverView> getTradeOverViewOb(String token) {
        return Observable.just(token)
                         .map(new Function<String, TradeOverView>() {
                             @Override
                             public TradeOverView apply(String s) throws Exception {
                                 Thread.sleep(1000);
                                 return new TradeOverView(Lists.newArrayList("over 11", "over 22", "over 33"));
                             }
                         })
                         .subscribeOn(Schedulers.io());
    }

    public static Observable<TradeDetails> getTradeDetailOb(String id) {

        return Observable.just(id)
                         .map(new Function<String, TradeDetails>() {
                             @Override
                             public TradeDetails apply(String s) throws Exception {
                                 Thread.sleep(1000);
                                 return new TradeDetails(Lists.newArrayList("detail 11", "detail 22", "detail 33"));
                             }
                         })
                         .subscribeOn(Schedulers.io());
    }
}
