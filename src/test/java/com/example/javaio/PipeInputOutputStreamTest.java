package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PipeInputOutputStreamTest {

    @Test
    public void p() throws Exception {
        Path scriptPath = Path.of("./file-generator.sh");
        Path outPath = Path.of("./file-generator.log");
        String[] shArgs = {"/bin/sh", scriptPath.toAbsolutePath().toString()};

        Process process = Runtime.getRuntime().exec(shArgs);
        InputStream processIs = process.getInputStream();

        try (final PipedInputStream pipedInputStream = new PipedInputStream();
             final PipedOutputStream pipedOutputStream = new PipedOutputStream();
             final OutputStream out = Files.newOutputStream(outPath, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {

            pipedInputStream.connect(pipedOutputStream);

            CompletableFuture<Void> writer = CompletableFuture.runAsync(() -> {
                try {
                    processIs.transferTo(pipedOutputStream);
                    pipedOutputStream.flush();
                    pipedOutputStream.close();//very important to call this method here
                    System.out.println("FINISH WRITE");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

            CompletableFuture<Void> reader = CompletableFuture.runAsync(() -> {
                try {
                    pipedInputStream.transferTo(out);
                    System.out.println("FINISH READ");
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).exceptionally(ex -> {
                ex.printStackTrace();
                return null;
            });

            CompletableFuture<Void> processExit = process.onExit().thenAccept(p -> System.out.println("PROCESS FINISHED"));

            CompletableFuture.allOf(writer, reader, processExit).get();
        }

        try (Stream<String> lines = Files.lines(outPath)) {
            assertEquals(10000, lines.count());
        }
    }
}
