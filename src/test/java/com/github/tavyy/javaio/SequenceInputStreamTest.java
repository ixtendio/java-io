package com.github.tavyy.javaio;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.SequenceInputStream;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SequenceInputStreamTest {

    @Test
    public void concatenateStreams() throws IOException {

        try (InputStream is1 = SequenceInputStreamTest.class.getResourceAsStream("/loremipsum-1.txt");
             ByteArrayInputStream is2 = new ByteArrayInputStream("\n".getBytes());
             InputStream is3 = SequenceInputStreamTest.class.getResourceAsStream("/loremipsum-13.txt");
             SequenceInputStream sis = new SequenceInputStream(Collections.enumeration(Arrays.asList(is1, is2, is3)));
             BufferedReader reader = new BufferedReader(new InputStreamReader(sis))) {

            long linesNo = reader.lines().count();
            assertEquals(14, linesNo);
        }

    }
}
