package com.github.tavyy.javaio.assessment.executorservice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

class WordCounter {

    private static final int MAX_READ_SIZE = 100000;
    private final AtomicInteger result = new AtomicInteger();
    private CountDownLatch cdl;
    private final long fileLength;
    private final FileChannel fileChannel;
    private final String word;

    private WordCounter(FileChannel fileChannel, String word, long fileLength) {
        this.fileChannel = fileChannel;
        this.word = word;
        this.fileLength = fileLength;
    }

    Integer count(ExecutorService executorService) throws Exception {
        int taskNo = (int) (this.fileLength / MAX_READ_SIZE);
        int rest = (int) (this.fileLength % MAX_READ_SIZE);
        cdl = new CountDownLatch(taskNo * 2 - 1);

        for (int i = 0; i < taskNo; i++) {
            int start = i * MAX_READ_SIZE;
            int length = (i == taskNo - 1) ? MAX_READ_SIZE + rest : MAX_READ_SIZE;
            executorService.submit(new Counter(this, start, length));
            if (i > 0) {
                executorService.submit(new Counter(this, start - word.length() + 1, Math.min(word.length() * 2 - 2, length)));
            }
        }

        cdl.await();
        return result.get();
    }

    static WordCounter create(FileChannel fileChannel, String searchWord) throws IOException {
        return new WordCounter(fileChannel, searchWord, fileChannel.size());
    }

    static class Counter implements Runnable {

        private final WordCounter wordCounter;
        private final int position;
        private final int length;

        Counter(WordCounter wordCounter, int position, int length) {
            this.wordCounter = wordCounter;
            this.position = position;
            this.length = length;
        }

        @Override
        public void run() {
            try {
                ByteBuffer buffer = ByteBuffer.allocate(this.length);
                wordCounter.fileChannel.read(buffer, this.position);
                buffer.flip();
                String value = Charset.forName("UTF-8").decode(buffer).toString();

                while (value.contains(wordCounter.word)) {
                    value = value.substring(value.indexOf(wordCounter.word) + wordCounter.word.length());
                    wordCounter.result.incrementAndGet();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                wordCounter.cdl.countDown();
            }
        }
    }


}
