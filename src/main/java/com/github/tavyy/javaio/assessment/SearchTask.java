package com.github.tavyy.javaio.assessment;

import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class SearchTask extends RecursiveTask<Integer> {

    static final int MAX_THREADS = Runtime.getRuntime().availableProcessors() * 2;
    private final boolean split;
    private final long position;
    private final long length;
    private final transient FileChannel fileChannel;
    private final byte[] wordArray;

    private SearchTask(FileChannel fileChannel, byte[] wordArray, long position, long length, boolean split) {
        this.fileChannel = fileChannel;
        this.wordArray = wordArray;
        this.position = position;
        this.length = length;
        this.split = split;
    }

    @Override
    protected Integer compute() {
        if (split) {
            return ForkJoinTask.invokeAll(split())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            return wordCount();
        }
    }

    static SearchTask create(FileChannel fileChannel, String searchWord) throws IOException {
        return new SearchTask(fileChannel, searchWord.getBytes(), 0, fileChannel.size(), true);
    }

    private Collection<SearchTask> split() {
        List<SearchTask> tasks = new ArrayList<>(MAX_THREADS);
        long maxReadSize = this.length / MAX_THREADS;
        long restLength = this.length % MAX_THREADS;

        for (long i = 0; i < MAX_THREADS; i++) {
            long start = i * maxReadSize;
            long newLength = (i == MAX_THREADS - 1) ? maxReadSize + restLength : maxReadSize;
            long newLenghtIncludingOverlap = Math.min(newLength + wordArray.length - 1, this.length - start);
            tasks.add(new SearchTask(fileChannel, wordArray, start, newLenghtIncludingOverlap, false));
        }

        return tasks;
    }

    private Integer wordCount() {
        try {
            int counter = 0;
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, position, length);

            int wordIndex = 0;
            for (int i = 0; i < mappedByteBuffer.limit(); i++) {
                wordIndex = (mappedByteBuffer.get(i) == wordArray[wordIndex]) ? wordIndex + 1 : 0;

                if (wordIndex == wordArray.length) {
                    counter++;
                    wordIndex = 0;
                }
            }
            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
