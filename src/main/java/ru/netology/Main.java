package ru.netology;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        Runnable maxFrequence = () -> {
            while (!Thread.interrupted()) {
                synchronized (sizeToFreq) {
                    try {
                        sizeToFreq.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int keyOfMaxValue = 0;
                    for (Map.Entry<Integer, Integer> map : sizeToFreq.entrySet()) {
                        if (map.getValue() > keyOfMaxValue) keyOfMaxValue = map.getKey();
                    }
                    System.out.printf("Max frequence now: " + keyOfMaxValue + "%n");

                }
            }
        };
        Thread curentMax = new Thread(maxFrequence);
        curentMax.start();

        for (int i = 0; i < 1000; i++) {
            Runnable logic = () -> {
                String route = generateRoute("RLRFR", 100);
                long freq = route.chars().filter(ch -> ch == 'R').count();

                synchronized (sizeToFreq) {
                    if (sizeToFreq.get((int) freq) == null) {
                        sizeToFreq.put((int) freq, 1);
                    } else {
                        int value = sizeToFreq.get((int) freq);
                        sizeToFreq.put((int) freq, value + 1);
                    }
                    System.out.printf("This route have %d R%n", freq);
                    sizeToFreq.notify();
                }

            };
            Thread thread = new Thread(logic);
            thread.start();
            thread.join();
        }


        curentMax.interrupt();
        System.out.println();
        System.out.println(sizeToFreq);

    }

    public static String generateRoute(String letters, int length) {
        Random random = new Random();
        StringBuilder route = new StringBuilder();
        for (int i = 0; i < length; i++) {
            route.append(letters.charAt(random.nextInt(letters.length())));
        }
        return route.toString();
    }
}