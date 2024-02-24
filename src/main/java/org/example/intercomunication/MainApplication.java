package org.example.intercomunication;

import java.io.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApplication {
    private static final String INPUT_FILE = "./out/matrices";
    private static final String OUTPUT_FILE = "./out/matrices_results.txt";
    private static final int N = 10;
    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer multiplierConsumer = new MatricesMultiplierConsumer(new FileWriter(outputFile), threadSafeQueue);

        matricesReaderProducer.start();
        multiplierConsumer.start();
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private ThreadSafeQueue queue;
        private FileWriter fileWriter;

        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true) {
                MatricesPair matricesPair = queue.remove();
                if(matricesPair == null) {
                    System.out.println("No more matrices to read from the queue, consumer is terminating");
                    break;
                }
                
                float [][] result = multiplyMatrices(matricesPair.matrix1, matricesPair.matrix2);

                try {
                    saveMatrixToFile(fileWriter, result);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            for(int r = 0; r<N; r++) {
                StringJoiner stringJoiner = new StringJoiner(", ");
                for(int c = 0; c<N;c++) {
                    stringJoiner.add(String.format("%.2f",matrix[r][c]));
                }
                fileWriter.write(stringJoiner.toString());
                fileWriter.write('\n');
            }
            fileWriter.write('\n');
        }

        private float[][] multiplyMatrices(float [][] m1, float [][] m2) {
            float [][] result = new float[N][N];

            for(int r = 0; r<N; r++) {
                for(int c = 0; c<N; c++) {
                    for(int k = 0; k < N; k++) {
                        result[r][c] += m1[r][k] * m2[k][c];
                    }
                }
            }

            return result;
        }

    }

    private static class MatricesReaderProducer extends Thread {
        private Scanner scanner;
        private ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread() + " publisher");
            try {
                Thread.sleep(10000L);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            while(true) {
                float [][] matrix1 = readMatrix();
                float [][] matrix2 = readMatrix();

                if(matrix1 == null || matrix2 == null) {
                    try {
                        queue.terminate();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("No more matrices to read, Producer Thread is terminating");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;

                queue.add(matricesPair);
            }
        }

        private float[][] readMatrix() {
            float[][] matrix = new float[N][N];
            for (int r = 0; r < N; r++) {
                if (!scanner.hasNext()) {
                    return null;
                }
                String[] line = scanner.nextLine().split(",");
                for (int c = 0; c < N; c++) {
                    matrix[r][c] = Float.valueOf(line[c]);
                }
            }
            scanner.nextLine();
            return matrix;
        }
    }


    private static class ThreadSafeQueue {
        private Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        public synchronized void add(MatricesPair matricesPair) {
            queue.add(matricesPair);
            isEmpty = false;
            notify();
        }

        public synchronized MatricesPair remove() {
            while (isEmpty && !isTerminate) {
                try {
                    System.out.println(Thread.currentThread()+ " wait");
                    wait();
                } catch (InterruptedException e) {
                }
            }

            if(queue.size() == 1) {
                isEmpty = true;
            }
            if(queue.isEmpty() && isTerminate) {
                return null;
            }

            System.out.println("queue size in remove " + queue.size());

            return queue.remove();
        }

        public synchronized void terminate() throws InterruptedException {
            isTerminate = true;
            Thread.sleep(10000L);
            notifyAll();
        }
    }

    private static class MatricesPair {
        public float[][] matrix1;
        public float[][] matrix2;
    }
}
