package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PushbackInputStreamTest {

    @Test
    public void parse() throws IOException {
        String dsl = "if (a == 4) a = 0;";
        String expected = "if (a eq 4) a <- 0;";

        StringBuilder sb = new StringBuilder();
        try (ByteArrayInputStream in = new ByteArrayInputStream(dsl.getBytes());
             PushbackInputStream is = new PushbackInputStream(in)) {
            int c;
            while ((c = is.read()) != -1) {
                if (c == '=') {
                    if ((c = is.read()) == '=')
                        sb.append("eq");
                    else {
                        sb.append("<-");
                        is.unread(c);
                    }
                } else {
                    sb.append((char)c);
                }
            }
        }
        assertEquals(expected, sb.toString());
    }
}
