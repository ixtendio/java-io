package com.github.tavyy.javaio.assessment.executorservice;

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SearchInBigFileUsingExecutorService {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    public static void main(String[] args) throws Exception {

        String wordToSearch = "Euler";
        Path filePath = Path.of("..", "large-files", "enwik9");

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel fileChannel = raf.getChannel()) {

            long startTime = System.currentTimeMillis();
            WordCounter wordCounter = WordCounter.create(fileChannel, wordToSearch);
            int result = wordCounter.count(executorService);
            long endTime = System.currentTimeMillis();

            System.out.println("The word '" + wordToSearch + "' has been found " + result + " times and it took " + (endTime - startTime) + " ms");
        }

        executorService.shutdownNow();
    }
}
