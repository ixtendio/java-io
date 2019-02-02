package com.github.tavyy.javaio.nio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.WritableByteChannel;

public class PipeTest {

    @Test
    public void pipe() throws Exception {
        WritableByteChannel out = Channels.newChannel(System.out);

        Pipe pipe = Pipe.open();
        new Writer(pipe.sink()).start();

        ByteBuffer buf = ByteBuffer.allocate(48);
        Pipe.SourceChannel sourceChannel = pipe.source();
        while (sourceChannel.read(buf) > 0) {
            buf.flip();
            out.write(buf);
            buf.clear();
        }

    }

    private static class Writer extends Thread {

        private final WritableByteChannel sinkChannel;

        Writer(WritableByteChannel sinkChannel) {
            this.sinkChannel = sinkChannel;
        }

        @Override
        public void run() {
            try (this.sinkChannel) {
                ByteBuffer buf = ByteBuffer.allocate(48);

                for (int i = 0; i < 10; i++) {
                    buf.clear();
                    buf.put(("Line" + (i + 1) + "\n").getBytes());
                    buf.flip();

                    while (buf.hasRemaining()) {
                        sinkChannel.write(buf);
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }
}
