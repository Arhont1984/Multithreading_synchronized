import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public static void main(String[] args) {
    //создаём HashMap для последующего заполнения
    final Map<Integer, Integer> sizeToFreq = new HashMap<>();
    //Создаём пулл потоков заданной велечины
    int numberOfRoutes = 1000;
    ExecutorService executor = Executors.newFixedThreadPool(numberOfRoutes);

    //Логикака задачи
    for (int i = 0; i < numberOfRoutes; i++) {

        executor.submit(() -> {
            synchronized (sizeToFreq) {
                String route = RobotRouteGenerator.generateRoute("RLRFR", 100);
                int countR = countRightTurns(route);
                //Создаём синхронизированный поток для добавления элементов в sizeToFreq
                new Thread(() -> {
                    synchronized (sizeToFreq) {
                        if (!sizeToFreq.containsKey(countR)) {
                            sizeToFreq.put(countR, 1);
                        } else {
                            sizeToFreq.put(countR, sizeToFreq.get(countR) + 1);
                        }
                    }
                }).start();
            }
        });
    }
    executor.shutdown();

//Поиск максимальных значений и вывод в консоль
    int maxFreq = 0;
    for (int freq : sizeToFreq.values()) {
        if (freq > maxFreq) {
            maxFreq = freq;
        }
    }
    for (Map.Entry entry : sizeToFreq.entrySet()) {
        if (entry.getValue().equals(maxFreq)) {
            System.out.println("Самое частое количество повторений " + entry.getKey() + " (встретилось " + maxFreq + " раз(а))");
        }
    }
    System.out.println("Другие размеры:");
    for (Map.Entry entry : sizeToFreq.entrySet()) {
        if (entry.getValue().equals(maxFreq)) {
            continue;
        }
        System.out.println("- " + entry.getKey() + " (" + entry.getValue() + " раз)");
    }
}


private static int countRightTurns(String route) {
    int count = 0;
    for (char command : route.toCharArray()) {
        if (command == 'R') {
            count++;
        }
    }

    return count;
}



