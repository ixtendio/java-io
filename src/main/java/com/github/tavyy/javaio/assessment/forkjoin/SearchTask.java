package com.github.tavyy.javaio.assessment.forkjoin;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

public class SearchTask extends RecursiveTask<Integer> {

    private static final int MAX_READ_SIZE = 100000;
    private final int position;
    private final int length;
    private final transient FileChannel fileChannel;
    private final String word;

    private SearchTask(FileChannel fileChannel, String word, int position, int length) {
        this.fileChannel = fileChannel;
        this.word = word;
        this.position = position;
        this.length = length;
    }

    @Override
    protected Integer compute() {
        if (this.length > MAX_READ_SIZE) {
            return ForkJoinTask.invokeAll(split())
                    .stream()
                    .mapToInt(ForkJoinTask::join)
                    .sum();
        } else {
            return wordCount();
        }
    }

    static SearchTask create(FileChannel fileChannel, String searchWord) throws IOException {
        return new SearchTask(fileChannel, searchWord, 0, (int) fileChannel.size());
    }

    private Collection<SearchTask> split() {
        //61035
        int newLength = this.length / 2;
        int restLength = this.length % 2;

        SearchTask leftTask = new SearchTask(fileChannel, word, this.position, newLength);
        SearchTask rightTask = new SearchTask(fileChannel, word, this.position + newLength, newLength + restLength);
        SearchTask midTask = new SearchTask(fileChannel, word, newLength - word.length() + 1, word.length() * 2 - 2);

        return Arrays.asList(leftTask, rightTask, midTask);
    }

    private Integer wordCount() {
        try {
            ByteBuffer buffer = ByteBuffer.allocateDirect(this.length);
            fileChannel.read(buffer, this.position);
            buffer.flip();
            String value = Charset.forName("UTF-8").decode(buffer).toString();

            int counter = 0;
            while (value.contains(word)) {
                value = value.substring(value.indexOf(word) + word.length());
                counter++;
            }
            return counter;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
