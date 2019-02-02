package com.github.tavyy.javaio;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileTest {

    private static final FileSystem fs = FileSystems.getDefault();

    @Test
    public void relativePath() {
        Path path = fs.getPath("src", "test", "resources", "loremipsum-1.txt");

        assertTrue(Files.exists(path));
    }

    @Test
    public void watchService() throws Exception {
        //GIVEN
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Set<String> expectedResult = new TreeSet<>(Arrays.asList("test_1.txt: ENTRY_CREATE", "test_1.txt: ENTRY_DELETE", "test_2.txt: ENTRY_CREATE"));
        Set<String> events = new TreeSet<>();

        Path tmpPath = fs.getPath("tmp");
        Path test1FilePath = tmpPath.resolve(Paths.get("test_1.txt"));
        Path test2FilePath = tmpPath.resolve(Paths.get("test_2.txt"));
        deleteFolder(tmpPath);
        Files.createDirectory(tmpPath);

        //WHEN
        WatchService watcher = FileSystems.getDefault().newWatchService();
        tmpPath.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        for (int step = 0; step < 2; step++) {
            if (step == 0) {
                //Create a file asynchronously
                executor.submit(() -> Files.createFile(test1FilePath));
            } else {
                //Rename a file asynchronously
                executor.submit(() -> Files.move(test1FilePath, test2FilePath));
            }

            WatchKey key = watcher.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                if (kind != OVERFLOW) {
                    WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                    Path filename = pathEvent.context();
                    events.add(filename + ": " + pathEvent.kind().name().toUpperCase());
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                break;
            }
        }
        executor.shutdownNow();

        //THEN
        assertEquals(expectedResult, events);
    }

    @Test
    public void readAllLines() throws Exception {
        //GIVEN
        Path filePath = Path.of(FileTest.class.getResource("/loremipsum-13.txt").toURI());

        //WHEN
        List<String> lines = Files.readAllLines(filePath);

        //THEN
        assertEquals(13, lines.size());
    }

    @Test
    public void readAllLinesLongerThan70() throws Exception {
        //GIVEN
        Path filePath = Path.of(FileTest.class.getResource("/loremipsum-13.txt").toURI());

        //WHEN
        try (Stream<String> stream = Files.lines(filePath)) {
            List<String> lines = stream.filter(line -> line.length() >= 70).collect(Collectors.toList());

            //THEN
            assertEquals(3, lines.size());
        }
    }

    private void deleteFolder(Path folder) throws IOException {
        if (Files.exists(folder)) {
            Files.walkFileTree(folder, new SimpleFileVisitor<>() {

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.deleteIfExists(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.deleteIfExists(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }
    }

}
