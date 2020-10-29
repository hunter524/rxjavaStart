package com.github.hunter524.rxjava.start;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

// 示例创建一个后添加的监听者也能接收到最近的一个事件的事件中心
// 并且事件的监听过滤收纳在 Observable 内部,不向外暴露.
public class RxJavaEventCenter {

    static Subject<Human> EVENT_CENTER = BehaviorSubject.create();

    static Observable<Human> HUMAN_CENTER = EVENT_CENTER.ofType(Human.class);

    static Observable<Man> MAN_CENTER = EVENT_CENTER.ofType(Man.class);

    static Observable<Woman> WOMAN_CENTER = EVENT_CENTER.ofType(Woman.class);


    public static void main(String[] args) {
        EVENT_CENTER.onNext(new Human("human 0"));

        HUMAN_CENTER.subscribe(new Consumer<Human>() {
            @Override
            public void accept(Human human) throws Exception {
                System.out.println("Observer Human:" + human.name);
            }
        });
        EVENT_CENTER.onNext(new Human("human 1"));
        EVENT_CENTER.onNext(new Man("man 0"));
        EVENT_CENTER.onNext(new Man("man 1"));
        EVENT_CENTER.onNext(new Woman("woman 0"));
        EVENT_CENTER.onNext(new Man("man 1"));

        MAN_CENTER.subscribe(new Consumer<Man>() {
            @Override
            public void accept(Man man) throws Exception {
                System.out.println("Observer Man:" + man.name);
            }
        });

        EVENT_CENTER.onNext(new Man("man 2"));
// 由于最近一个事件并非 Woman 因此该处订阅收不到任何事件
        WOMAN_CENTER.subscribe(new Consumer<Woman>() {
            @Override
            public void accept(Woman woman) throws Exception {
                System.out.println("Observer Woman:" + woman.name);
            }
        });
    }

    public static class Human {
        public String name;

        public Human(String name) {
            this.name = name;
        }
    }

    public static class Man extends Human {
        public Man(String name) {
            super("Man:" + name);
        }
    }

    public static class Woman extends Human {
        public Woman(String name) {
            super("Woman:" + name);
        }
    }
}
