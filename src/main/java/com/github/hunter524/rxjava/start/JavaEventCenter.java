package com.github.hunter524.rxjava.start;

import java.util.Observable;
import java.util.Observer;

public class JavaEventCenter {
    public static void main(String[] args) {

//        下发时要设置 setChanged 否则该事件会被忽略

        Observable eventCenter = new Observable() {
            @Override
            public void notifyObservers(Object arg) {
                setChanged();
                super.notifyObservers(arg);
            }
        };

        Observer manObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println("Man Observer Type:" + arg.getClass()
                                                             .getSimpleName());
                if (arg instanceof Man) {
                    System.out.println("Man Receive:" + ((Man) arg).name);
                }
            }
        };

        Observer woManObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                System.out.println("Woman Observer Type:" + arg.getClass()
                                                             .getSimpleName());
                if (arg instanceof Woman) {
                    System.out.println("Woman Receive:" + ((Woman) arg).name);
                }
            }
        };
        eventCenter.notifyObservers(new Human("human 1"));

        eventCenter.addObserver(manObserver);

        eventCenter.notifyObservers(new Human("Human 1"));
        eventCenter.notifyObservers(new Man("Man 1"));
        eventCenter.notifyObservers(new Woman("Woman 1"));

        eventCenter.addObserver(woManObserver);

        eventCenter.notifyObservers(new Human("Human 2"));
        eventCenter.notifyObservers(new Man("Man 2"));
        eventCenter.notifyObservers(new Woman("Woman 2"));

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
