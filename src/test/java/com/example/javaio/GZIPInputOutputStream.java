package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GZIPInputOutputStream {

    @Test
    public void zipAndUnzip() throws Exception {

        //GIVEN
        Path uncompressedFilePath = Path.of("src", "test", "resources", "loremipsum-13.txt");
        Path compressedFilePath = Path.of("loremipsum-13.gzip");
        long uncompressedFileSize = Files.size(uncompressedFilePath);

        //WHEN GZIP
        try (InputStream is = Files.newInputStream(uncompressedFilePath);
             GZIPOutputStream gzos = new GZIPOutputStream(Files.newOutputStream(compressedFilePath))) {
            is.transferTo(gzos);
        }

        //WHEN UnGZIP
        byte[] uncompressedFileContent;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPInputStream gzos = new GZIPInputStream(Files.newInputStream(compressedFilePath, StandardOpenOption.DELETE_ON_CLOSE))) {
            gzos.transferTo(bos);
            uncompressedFileContent = bos.toByteArray();
        }

        assertEquals(uncompressedFileSize, uncompressedFileContent.length);
    }
}
