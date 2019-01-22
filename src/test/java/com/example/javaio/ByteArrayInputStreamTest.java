package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteArrayInputStreamTest {

    @Test
    public void appendStrings() throws IOException {
        //GIVEN
        String input1 = "John";
        String input2 = " Doe";

        //WHEN
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        asInputStream(input1).transferTo(bos);
        asInputStream(input2).transferTo(bos);

        //THEN
        String result = bos.toString();
        assertEquals(result, "John Doe");
    }

    private InputStream asInputStream(String value) {
        return new ByteArrayInputStream(value.getBytes());
    }
}
