package com.example.javaio.nio;

import com.example.javaio.FileTest;
import org.junit.jupiter.api.Test;

import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MemoryMappedFileTest {

    @Test
    public void readTest() throws Exception {
        //GIVEN
        Path filePath = Path.of(FileTest.class.getResource("/loremipsum-13.txt").toURI());

        //WHEN
        String readValue = readSegment(filePath, 132, 7);

        //THEN
        assertEquals("vivamus", readValue);
    }

//    @Test
//    public void readBigFileTest() throws Exception {
//        //GIVEN Euler
//        Path filePath = Path.of("..", "large-files", "enwik9");
//
//        //WHEN
//        String readValue = readSegment(filePath, 891963511, 63);
//
//        //THEN
//        assertEquals("However, FBI investigators soon determined that the design data", readValue);
//    }

    private String readSegment(Path filePath, long position, long size) throws Exception {

        try (FileChannel fileChannel = (FileChannel) Files.newByteChannel(filePath, EnumSet.of(StandardOpenOption.READ))) {
            MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, position, size);
            if (mappedByteBuffer != null) {
                return Charset.forName("UTF-8").decode(mappedByteBuffer).toString();
            }
        }

        return null;
    }
}
