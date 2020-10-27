package com.github.hunter524.rxjava.start;

import io.reactivex.*;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class CallBackHell {
    public static ExecutorService CACHED_EXECUTOR_SERVICE = Executors.newCachedThreadPool();


    public static void main(String[] args) {
//        旧的 callbackHell
        getPersonInfo("acc", "pwd", new GetPersonCallBack() {
            @Override
            public void onSucces(PersonInfo personInfo) {
                getTradeData(personInfo.token, new GetTradeDataCallBack() {
                    @Override
                    public void onSucces(TradeData tradedata) {
                        getTradeData(personInfo.token, new GetTradeDataCallBack() {
                            @Override
                            public void onSucces(TradeData tradedata) {
                                getTradeDetail(tradedata.id, new GetTradeDetailCallBack() {
                                    @Override
                                    public void onSucces(TradeDetail tradeDetail) {
                                        System.out.println("Call Back hell Detail:" + tradeDetail.content);
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


//        rxjava wrapped
        getPersonInfoObWrapp("acc", "pwd")
                .flatMap(new Function<PersonInfo, ObservableSource<TradeData>>() {
                    @Override
                    public ObservableSource<TradeData> apply(PersonInfo personInfo) throws Exception {
                        return getTradeDataObWrapp(personInfo.token);
                    }
                })
                .flatMap(new Function<TradeData, ObservableSource<TradeDetail>>() {
                    @Override
                    public ObservableSource<TradeDetail> apply(TradeData tradeData) throws Exception {
                        return getTradeDetailObWrapp(tradeData.id);
                    }
                })
                .observeOn(Schedulers.trampoline())
                .subscribe(new Consumer<TradeDetail>() {
                    @Override
                    public void accept(TradeDetail tradeDetail) throws Exception {
                        System.out.println("RxJava Wrap Detail:" + tradeDetail.content);
                    }
                });
// no wrap rxjava

        getPersonInfoOb("acc", "pwd")
                .flatMap(new Function<PersonInfo, ObservableSource<TradeData>>() {
                    @Override
                    public ObservableSource<TradeData> apply(PersonInfo personInfo) throws Exception {
                        return getTradeDataOb(personInfo.token);
                    }
                })
                .flatMap(new Function<TradeData, ObservableSource<TradeDetail>>() {
                    @Override
                    public ObservableSource<TradeDetail> apply(TradeData tradeData) throws Exception {
                        return getTradeDetailOb(tradeData.id);
                    }
                })
                .observeOn(Schedulers.trampoline())
                .subscribe(new Consumer<TradeDetail>() {
                    @Override
                    public void accept(TradeDetail tradeDetail) throws Exception {
                        System.out.println("RxJava NoWrap Detail:" + tradeDetail.content);
                    }
                });

    }

    public static class PersonInfo {

        public String token;

        public PersonInfo(String token) {
            this.token = token;
        }
    }

    public static class TradeData {
        public String id;

        public TradeData(String id) {
            this.id = id;
        }
    }

    public static class TradeDetail {
        public String content;

        public TradeDetail(String content) {
            this.content = content;
        }
    }

    public interface GetPersonCallBack {
        public void onSucces(PersonInfo personInfo);

        public void onError();
    }

    public interface GetTradeDetailCallBack {
        public void onSucces(TradeDetail tradeDetail);

        public void onError();
    }

    public interface GetTradeDataCallBack {
        public void onSucces(TradeData tradedata);

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

    public static void getTradeData(String token, GetTradeDataCallBack getTradeDataCallBack) {
        CACHED_EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTradeDataCallBack.onSucces(new TradeData("id"));
            }
        });
    }

    public static void getTradeDetail(String id, GetTradeDetailCallBack getTradeDetailCallBack) {
        CACHED_EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getTradeDetailCallBack.onSucces(new TradeDetail("content"));
            }
        });
    }

    // 包装老的接口进入 rxjava 的模式
    public static Observable<PersonInfo> getPersonInfoObWrapp(String acc, String pwd) {
        return Observable.<PersonInfo>create(new ObservableOnSubscribe<PersonInfo>() {
            @Override
            public void subscribe(ObservableEmitter<PersonInfo> emitter) throws Exception {
                getPersonInfo(acc, pwd, new GetPersonCallBack() {
                    @Override
                    public void onSucces(PersonInfo personInfo) {
                        emitter.onNext(personInfo);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError() {
                        emitter.onError(new Throwable());
                    }
                });
            }
        });
    }

    public static Observable<TradeData> getTradeDataObWrapp(String token) {
        return Observable.<TradeData>create(new ObservableOnSubscribe<TradeData>() {
            @Override
            public void subscribe(ObservableEmitter<TradeData> emitter) throws Exception {
                getTradeData(token, new GetTradeDataCallBack() {
                    @Override
                    public void onSucces(TradeData tradeData) {
                        emitter.onNext(tradeData);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError() {
                        emitter.onError(new Throwable());
                    }
                });
            }
        });
    }

    public static Observable<TradeDetail> getTradeDetailObWrapp(String id) {
        return Observable.<TradeDetail>create(new ObservableOnSubscribe<TradeDetail>() {
            @Override
            public void subscribe(ObservableEmitter<TradeDetail> emitter) throws Exception {
                getTradeDetail(id, new GetTradeDetailCallBack() {
                    @Override
                    public void onSucces(TradeDetail tradeDetail) {
                        emitter.onNext(tradeDetail);
                        emitter.onComplete();
                    }

                    @Override
                    public void onError() {
                        emitter.onError(new Throwable());
                    }
                });
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

    public static Observable<TradeData> getTradeDataOb(String token) {
        return Observable.just(token)
                         .map(new Function<String, TradeData>() {
                             @Override
                             public TradeData apply(String s) throws Exception {
                                 Thread.sleep(1000);
                                 return new TradeData("id");
                             }
                         })
                         .subscribeOn(Schedulers.io());
    }

    public static Observable<TradeDetail> getTradeDetailOb(String id) {

        return Observable.just(id)
                         .map(new Function<String, TradeDetail>() {
                             @Override
                             public TradeDetail apply(String s) throws Exception {
                                 Thread.sleep(1000);
                                 return new TradeDetail("content");
                             }
                         })
                         .subscribeOn(Schedulers.io());
    }

}
