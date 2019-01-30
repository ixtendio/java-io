package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomAccessFileTest {

    @Test
    public void test() throws Exception {

        String initialLine = "There is nothing either good or bad but thinking makes it so.";
        String lineToAppend = " William Shakespeare";
        Path filePath = Path.of("random-access-file-test.txt");
        Files.writeString(filePath, initialLine);

        try (RandomAccessFile raf = new RandomAccessFile(filePath.toFile(), "rw")) {
            byte[] readBlock = new byte[7];
            raf.seek(9);
            raf.read(readBlock);
            assertEquals("nothing", new String(readBlock, Charset.forName("UTF-8")));

            raf.seek(raf.length());
            raf.write(lineToAppend.getBytes());
            raf.seek(0);
            readBlock = new byte[initialLine.length() + lineToAppend.length()];
            raf.read(readBlock);
            assertEquals(initialLine + lineToAppend, new String(readBlock, Charset.forName("UTF-8")));
        }
    }

}
