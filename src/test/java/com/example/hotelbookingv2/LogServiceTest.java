package com.example.hotelbookingv2;

import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.nio.file.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.Resource;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    private LogService logService;

    private static final String TEST_LOG_FILE = "log/app.log";
    private static final String VALID_DATE = "08-04-2025";
    private static final String INVALID_DATE = "2025/04/08";
    private static final Path SECURE_TEMP_DIR = Paths.get("/Users/margarita/IdeaProjects/hotelbooking-v2");

    @BeforeEach
    void setUp() throws IOException {
        // Создаем временную директорию и файл
        Files.createDirectories(Paths.get("log"));
        Files.writeString(Paths.get(TEST_LOG_FILE),
                """
                08-04-2025 INFO test log line 1
                08-04-2025 ERROR test log line 2
                07-04-2025 INFO some other log
                """);
        logService = new LogService();
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(TEST_LOG_FILE));
        // Удаляем временные лог-файлы
        Files.list(SECURE_TEMP_DIR)
                .filter(p -> p.getFileName().toString().startsWith("log-"))
                .forEach(p -> p.toFile().delete());
    }

    @Test
    void downloadLogs_successful() {
        Resource resource = logService.downloadLogs(VALID_DATE);
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void downloadLogs_invalidDateFormat_shouldThrowException() {
        InvalidInputException exception = assertThrows(InvalidInputException.class,
                () -> logService.downloadLogs(INVALID_DATE));
        assertEquals("Invalid date format. Required dd-MM-yyyy", exception.getMessage());
    }

    @Test
    void downloadLogs_fileNotExists_shouldThrowException() {
        // Удалим файл логов перед вызовом
        try {
            Files.deleteIfExists(Paths.get(TEST_LOG_FILE));
        } catch (IOException e) {
            fail("Не удалось удалить лог-файл перед тестом");
        }

        assertThrows(ResourceNotFoundException.class,
                () -> logService.downloadLogs(VALID_DATE));
    }

    @Test
    void downloadLogs_noLogsForDate_shouldThrowException() throws IOException {
        // Перезаписываем файл логов без совпадений по дате
        Files.writeString(Paths.get(TEST_LOG_FILE), "01-01-2024 INFO no match log");

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
                () -> logService.downloadLogs(VALID_DATE));
        assertEquals("There are no logs for specified date: " + VALID_DATE, exception.getMessage());
    }

    @Test
    void downloadLogs_ioErrorOnRead_shouldThrowException() throws IOException {
        Path path = Paths.get(TEST_LOG_FILE);
        path.toFile().setReadable(false); // создаем ошибку доступа

        Exception ex = assertThrows(IllegalStateException.class,
                () -> logService.downloadLogs(VALID_DATE));
        assertTrue(ex.getMessage().startsWith("Error processing log file"));

        path.toFile().setReadable(true); // возвращаем доступ для других тестов
    }
}
