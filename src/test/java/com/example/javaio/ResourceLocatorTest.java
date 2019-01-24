package com.example.javaio;

import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ResourceLocatorTest {

    @Test
    public void locateFileRelativeToExecutionDir() {
        URL url = ResourceLocatorTest.class.getResource("/loremipsum-13.txt");

        assertNotNull(url);
    }

    @Test
    public void locateFileRelativeToExecutionDirUsingFolders() {
        URL url = ResourceLocatorTest.class.getResource("/com/example/javaio/FileTest.class");

        assertNotNull(url);
    }

    @Test
    public void convertUrlToPath() throws Exception {
        URL url = ResourceLocatorTest.class.getResource("/com/example/javaio/FileTest.class");

        Path path = Path.of(url.toURI());
        assertNotNull(path);
    }

    @Test
    public void getFullExecutionPath() throws Exception {
        URL url = ResourceLocatorTest.class.getResource("/");

        Path path = Path.of(url.toURI());
        assertTrue(path.toString().endsWith("/java-io/target/test-classes"));
    }

    @Test
    public void getFullWorkingDirPath() {
        Path path = Path.of("");

        assertTrue(path.toAbsolutePath().toString().endsWith("/java-io"));
    }

    @Test
    public void locateFileRelativeToWorkingDir() {
        Path path = Paths.get("pom.xml");

        assertTrue(Files.exists(path));
    }
}
