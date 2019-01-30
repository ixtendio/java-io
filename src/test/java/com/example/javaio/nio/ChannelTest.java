package com.example.javaio.nio;

import org.junit.jupiter.api.Test;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChannelTest {

    @Test
    public void test() throws Exception {

        String expectedResult = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
        Path filePath = Path.of("src", "test", "resources", "loremipsum-1.txt");

        StringBuilder sb = new StringBuilder();
        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "r");
             FileChannel inChannel = raf.getChannel()) {

            ByteBuffer buf = ByteBuffer.allocate(48);
            while (inChannel.read(buf) != -1) {
                buf.flip();
                byte[] array = getByteArrayFromByteBuffer(buf);
                sb.append(new String(array, Charset.forName("UTF-8")));
                buf.clear();
            }

            assertEquals(expectedResult, sb.toString());
        }
    }

    private byte[] getByteArrayFromByteBuffer(ByteBuffer buf) {
        if (!buf.hasArray()) {
            return new byte[0];
        }
        if (buf.hasRemaining()) {
            byte[] array = new byte[buf.remaining()];
            buf.get(array);
            return array;
        } else {
            return buf.array();
        }
    }

}
