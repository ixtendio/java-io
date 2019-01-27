package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipInputOutputStreamTest {

    @Test
    public void zipAndUnzip() throws Exception {
        //GIVEN
        Path compressedFilePath = Path.of("resources.zip");
        Path folderToCompress = Path.of("src", "test", "resources");
        Map<Path, Long> originalFilesSize;

        //WHEN ZIP
        try (Stream<Path> pathStream = Files.list(folderToCompress);
             ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(compressedFilePath))) {

            originalFilesSize = pathStream.collect(Collectors.toMap(path -> path, this::fileSize));
            originalFilesSize.keySet().forEach(p -> writeZipEntry(zos, p));
        }

        //WHEN UNZIP
        Map<String, Integer> uncompressedFilesSize = new HashMap<>();
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(compressedFilePath, StandardOpenOption.DELETE_ON_CLOSE))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                byte[] entryContent = readZipEntry(zis);
                uncompressedFilesSize.put(zipEntry.getName(), entryContent.length);
                zis.closeEntry();
            }
        }

        //THEN
        for (Map.Entry<Path, Long> result : originalFilesSize.entrySet()) {
            int uncompressedFileSize = uncompressedFilesSize.get(result.getKey().getFileName().toString());
            assertEquals(result.getValue().intValue(), uncompressedFileSize);
        }

    }

    private void writeZipEntry(ZipOutputStream zos, Path path) {
        try {
            zos.putNextEntry(new ZipEntry(path.getFileName().toString()));
            zos.write(Files.readAllBytes(path));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] readZipEntry(ZipInputStream zis) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            zis.transferTo(bos);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private long fileSize(Path path) {
        try {
            return Files.size(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
