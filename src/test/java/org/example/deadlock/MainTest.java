package org.example.deadlock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class MainTest {


    @Test
    @Timeout(value = 10)
    public void showDeadlock() throws InterruptedException {
        BaseIntersectionInterface intersection = new Main.Intersection();
        Thread ta = new Thread(new Main.TrainA(intersection));
        Thread tb = new Thread(new Main.TrainB(intersection));

        ta.start();
        tb.start();

        ta.join();
        tb.join();
    }

    @Test
    @Timeout(20)
    public void showNoDeadLock() throws InterruptedException {
        BaseIntersectionInterface intersection = new Main.NoDeadLockIntersection();
        Thread ta = new Thread(new Main.TrainA(intersection));
        Thread tb = new Thread(new Main.TrainB(intersection));

        ta.start();
        tb.start();

        ta.join();
        tb.join();
    }
}