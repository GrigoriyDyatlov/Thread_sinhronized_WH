package ru.netology;

import java.util.*;
import java.util.concurrent.*;

public class Main {
    public static final Map<Integer, Integer> sizeToFreq = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        final ExecutorService threadPoll = Executors.newFixedThreadPool(5);
        List<Future> taskList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Callable<String> logic = () -> {
                String route = generateRoute("RLRFR", 100);
                long freq = route.chars().filter(ch -> ch == 'R').count();

                synchronized (sizeToFreq) {
                    if (sizeToFreq.get((int) freq) == null) {
                        sizeToFreq.put((int) freq, 1);
                    } else {
                        int value = sizeToFreq.get((int) freq);
                        sizeToFreq.put((int) freq, value + 1);
                    }
                }
                return String.format("This route have %d R", freq);

            };
            final Future<String> future = threadPoll.submit(logic);
            taskList.add(future);

        }

        for (Future<String> result : taskList) {
            System.out.println(result.get());
        }
        threadPoll.shutdown();
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