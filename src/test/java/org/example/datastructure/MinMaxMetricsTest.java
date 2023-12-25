package org.example.datastructure;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class MinMaxMetricsTest {

    @Test
    public void baseTest() {
        MinMaxMetrics mmm = new MinMaxMetrics();

        for(int i=0;i<10;i++) {
           mmm.addSample(i);
        }

        assertEquals(0, mmm.getMin());
        assertEquals(9, mmm.getMax());
    }


    @Test
    public void testWithMultiThread() throws InterruptedException {
        MinMaxMetrics mmm = new MinMaxMetrics();

        Thread t1under = new Thread(() -> {
            for(int i=0; i< 10000; i++) {
                mmm.addSample(i);
            }
        });

        Thread t1Higher = new Thread(() -> {
           for(int i=10000; i<20000; i++) {
               mmm.addSample(i);
           }
        });

        t1Higher.start();
        t1under.start();

        t1Higher.join();
        t1under.join();

        assertEquals(0, mmm.getMin());
        assertEquals(19999, mmm.getMax());
    }
}