package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Paths;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileInputStreamTest {

    private static final String RELATIVE_FILE_PATH = "/loremipsum-1.txt";
    private static final String EXPECTED_RESULT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";

    @Test
    public void readUsingFileNameConstructor() throws Exception {

        //WHEN
        byte[] fileContent;
        String fileName = FileInputStreamTest.class.getResource(RELATIVE_FILE_PATH).getFile();
        try (FileInputStream is = new FileInputStream(fileName)) {
            fileContent = is.readAllBytes();
        }

        //THEN
        assertEquals(EXPECTED_RESULT, new String(fileContent, UTF_8));
    }

    @Test
    public void readUsingFileConstructor() throws Exception {

        //WHEN
        byte[] fileContent;
        File file = Paths.get(FileInputStreamTest.class.getResource(RELATIVE_FILE_PATH).toURI()).toFile();
        try (FileInputStream is = new FileInputStream(file)) {
            fileContent = is.readAllBytes();
        }

        //THEN
        assertEquals(EXPECTED_RESULT, new String(fileContent, UTF_8));
    }
}
