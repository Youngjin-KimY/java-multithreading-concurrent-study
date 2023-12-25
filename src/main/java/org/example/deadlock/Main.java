package org.example.deadlock;

import java.util.Random;

public class Main {
    public static void main(String[] args) {
        Intersection intersection = new Intersection();
        Thread ta = new Thread(new TrainA(intersection));
        Thread tb = new Thread(new TrainB(intersection));

        ta.start();
        tb.start();
    }

    public static class TrainA implements Runnable {
        private BaseIntersectionInterface intersection;
        private Random random = new Random();

        public TrainA(BaseIntersectionInterface intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while(true) {
                long sleepingTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intersection.takeRoadA();
            }
        }
    }

    public static class TrainB implements Runnable {
        private BaseIntersectionInterface intersection;

        private Random random = new Random();

        public TrainB(BaseIntersectionInterface intersection) {
            this.intersection = intersection;
        }



        @Override
        public void run() {
            while(true) {
                long sleepingTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepingTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intersection.takeRoadB();
            }
        }
    }

    public static class Intersection implements BaseIntersectionInterface {
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        @Override
        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through roadA");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }

        @Override
        public void takeRoadB() {
            synchronized (roadB) {
                System.out.println("Road B is locked by thread " + Thread.currentThread().getName());

                synchronized (roadA) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
    }

    //making same every locking(critical section) order
    public static class NoDeadLockIntersection implements BaseIntersectionInterface {
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        @Override
        public void takeRoadA() {
            synchronized (roadA) {
                System.out.println("Road A is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through roadA");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }

        @Override
        public void takeRoadB() {
            synchronized (roadA) {
                System.out.println("Road B is locked by thread " + Thread.currentThread().getName());

                synchronized (roadB) {
                    System.out.println("Train is passing through road B");
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {

                    }
                }
            }
        }
    }

}
