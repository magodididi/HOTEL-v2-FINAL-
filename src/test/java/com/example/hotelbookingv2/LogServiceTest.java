package com.example.hotelbookingv2;

import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.service.LogService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.util.stream.Stream;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    private final LogService logService = new LogService();
    private final Path logDir = Paths.get("log");
    private final Path logFilePath = logDir.resolve("app.log");

    @BeforeEach
    void setUp() throws IOException {
        if (!Files.exists(logDir)) {
            Files.createDirectory(logDir);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(logFilePath)) {
            Files.delete(logFilePath);
        }
        if (Files.exists(logDir)) {
            try (Stream<Path> stream = Files.list(logDir)) {
                if (stream.findAny().isEmpty()) {
                    Files.delete(logDir);
                }
            }
        }
    }

    @Test
    void downloadLogs_invalidDateFormat() {
        String invalidDate = "2025-03-15";
        Assertions.assertThrows(InvalidInputException.class, () -> logService.downloadLogs(invalidDate));
    }

    @Test
    void downloadLogs_logFileNotFound() {
        if (Files.exists(logFilePath)) {
            try {
                Files.delete(logFilePath);
            } catch (IOException e) {
                // ignore
            }
        }
        Assertions.assertThrows(ResourceNotFoundException.class, () -> logService.downloadLogs("15-03-2025"));
    }


    @Test
    void downloadLogs_invalidDateForParsing() {
        String invalidDate = "99-99-9999";
        Assertions.assertThrows(InvalidInputException.class, () -> logService.downloadLogs(invalidDate));
    }

}
