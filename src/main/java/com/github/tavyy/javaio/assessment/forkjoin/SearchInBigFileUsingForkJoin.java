package com.github.tavyy.javaio.assessment.forkjoin;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.concurrent.ForkJoinPool;

public class SearchInBigFileUsingForkJoin {

    public static void main(String[] args) throws IOException {

        Path filePath = Path.of("..", "large-files", "enwik9");

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel fileChannel = raf.getChannel()) {

            String wordToSearch = "Euler";
            ForkJoinPool forkJoinPool = new ForkJoinPool(8);

            long startTime = System.currentTimeMillis();
            int result = forkJoinPool.invoke(SearchTask.create(fileChannel, wordToSearch));
            long endTime = System.currentTimeMillis();

            System.out.println("The word '" + wordToSearch + "' has been found " + result + " times and it took " + (endTime - startTime) + " ms");
        }
    }
}
