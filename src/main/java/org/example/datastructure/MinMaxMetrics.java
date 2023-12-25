package org.example.datastructure;

public class MinMaxMetrics {

    // Add all necessary member variables
    private long min = Long.MAX_VALUE;
    private long max = Long.MIN_VALUE;


    /**
     * Initializes all member variables
     */
    public MinMaxMetrics() {
        // Add code here
    }

    /**
     * Adds a new sample to our metrics.
     */
    public void addSample(long newSample) {
        // Add code here
        synchronized (this) {
            if (newSample > max) {
                this.max = newSample;
            }
            if (newSample < min) {
                this.min = newSample;
            }
        }
    }

    /**
     * Returns the smallest sample we've seen so far.
     */
    public long getMin() {
        // Add code here
        return this.min;
    }

    /**
     * Returns the biggest sample we've seen so far.
     */
    public long getMax() {
        // Add code here
        return this.max;
    }
}
