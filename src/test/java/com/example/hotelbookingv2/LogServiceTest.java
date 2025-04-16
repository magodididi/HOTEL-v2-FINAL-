package com.example.hotelbookingv2;

import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.service.LogService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.core.io.Resource;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Mock
    private Path logFilePath;

    @Mock
    private Resource resource;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private Path tempFile;

    @Mock
    private LocalDate logDate;

    @Mock
    private DateTimeFormatter dateTimeFormatter;

    @Test
    void parseDate_valid_returnsLocalDate() {
        LocalDate result = invokeParseDate("10-04-2025");
        assertEquals(LocalDate.of(2025, 4, 10), result);
    }

    @Test
    void parseDate_invalid_throwsInvalidInputException() {
        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> invokeParseDate("2025-04-10"));
        assertEquals("Invalid date format. Required dd-MM-yyyy", ex.getMessage());
    }

    @Test
    void validateLogFileExists_whenFileNotExist_throwsException() {
        Path path = Paths.get("non_existing.log");
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> invokeValidateLogFileExists(path));
        assertEquals("File doesn't exist: log/app.log", ex.getMessage());
    }

    @Test
    void createTempFile_throwsIOException() throws IOException {
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createTempFile(any(Path.class), anyString(), anyString()))
                    .thenThrow(new IOException("Failed to create temp file"));

            IOException ex = assertThrows(IOException.class,
                    () -> invokeCreateTempFile(LocalDate.now()));  // Должен быть выброшен IOException
            assertFalse(ex.getMessage().startsWith("Failed to create temp file"));
        }
    }


    @Test
    void filterAndWriteLogsToTempFile_success(@TempDir Path tempDir) throws IOException {
        Path log = tempDir.resolve("app.log");
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Files.write(log, List.of("Log1 " + today, "Log2 wrong-date"));

        Path out = tempDir.resolve("out.log");
        invokeFilterAndWrite(log, today, out);

        List<String> lines = Files.readAllLines(out);
        assertEquals(1, lines.size());
        assertTrue(lines.get(0).contains(today));
    }

    @Test
    void filterAndWriteLogsToTempFile_throwsIOException() {
        Path fake = mock(Path.class);
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> invokeFilterAndWrite(fake, "10-04-2025", fake));
        assertFalse(ex.getMessage().startsWith("Error processing log file"));
    }

    @Test
    void createResourceFromTempFile_success(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("logfile.log");
        Files.write(tempFile, List.of("Some log"));
        Resource resource = invokeCreateResourceFromTempFile(tempFile, "10-04-2025");
        assertNotNull(resource);
        assertTrue(resource.exists());
    }

    @Test
    void createTempFile_and_permissions_OK(@TempDir Path tempDir) throws IOException {
        Path file = logService.createTempFile(LocalDate.now());  // Теперь вызываем напрямую
        assertTrue(Files.exists(file), "Temporary file should exist");
        assertTrue(Files.isReadable(file), "Temporary file should be readable");
        assertTrue(Files.isWritable(file), "Temporary file should be writable");
    }

    @Test
    void createResourceFromTempFile_emptyFile_throwsResourceNotFound(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("empty.log");
        Files.write(tempFile, List.of());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> invokeCreateResourceFromTempFile(tempFile, "10-04-2025"));
        assertTrue(ex.getMessage().contains("no logs"));
    }

    @Test
    void createResourceFromTempFile_error_throwsIllegalState() {
        Path mockPath = mock(Path.class);
        try (MockedStatic<Files> mockFiles = Mockito.mockStatic(Files.class)) {
            mockFiles.when(() -> Files.size(mockPath)).thenThrow(new IOException("fail"));
            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> invokeCreateResourceFromTempFile(mockPath, "10-04-2025"));
            assertTrue(ex.getMessage().contains("Error creating resource"));
        }
    }

    private LocalDate invokeParseDate(String date) {
        try {
            Method m = LogService.class.getDeclaredMethod("parseDate", String.class);
            m.setAccessible(true);
            return (LocalDate) m.invoke(logService, date);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void invokeValidateLogFileExists(Path path) {
        try {
            Method m = LogService.class.getDeclaredMethod("validateLogFileExists", Path.class);
            m.setAccessible(true);
            m.invoke(logService, path);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Path invokeCreateTempFile(LocalDate date) throws IOException {
        try {
            Method createTempFileMethod = LogService.class.getDeclaredMethod("createTempFile", LocalDate.class);
            createTempFileMethod.setAccessible(true);
            return (Path) createTempFileMethod.invoke(logService, date);
        } catch (Exception e) {
            throw new IOException("Failed to invoke createTempFile method", e);
        }
    }

    private void invokeFilterAndWrite(Path in, String date, Path out) {
        try {
            Method m = LogService.class.getDeclaredMethod("filterAndWriteLogsToTempFile",
                    Path.class, String.class, Path.class);
            m.setAccessible(true);
            m.invoke(logService, in, date, out);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Resource invokeCreateResourceFromTempFile(Path file, String date) {
        try {
            Method m = LogService.class.getDeclaredMethod("createResourceFromTempFile", Path.class, String.class);
            m.setAccessible(true);
            return (Resource) m.invoke(logService, file, date);
        } catch (InvocationTargetException e) {
            throw (RuntimeException) e.getCause();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createTempFile_windows_success() throws IOException {
        // Mocking the Windows OS condition
        System.setProperty("os.name", "Windows 10");

        Path tempFile = logService.createTempFile(LocalDate.now());

        assertTrue(Files.exists(tempFile), "Temporary file should exist");
        assertTrue(Files.isReadable(tempFile), "Temporary file should be readable");
        assertTrue(Files.isWritable(tempFile), "Temporary file should be writable");
    }

    @Test
    void createTempFile_unix_success() throws IOException {
        // Mocking the Unix/Linux OS condition
        System.setProperty("os.name", "Linux");

        Path tempFile = logService.createTempFile(LocalDate.now());

        assertTrue(Files.exists(tempFile), "Temporary file should exist");
        assertTrue(Files.isReadable(tempFile), "Temporary file should be readable");
        assertTrue(Files.isWritable(tempFile), "Temporary file should be writable");
    }

    @Test
    void createTempFile_windows_noPermission() throws IOException {
        // Mocking the Windows OS condition
        System.setProperty("os.name", "Windows 10");

        // Creating a file with no permissions
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createTempFile(any(Path.class), anyString(), anyString()))
                    .thenReturn(Paths.get("nonexistent_file"));

            // The test should fail due to no permissions
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> logService.createTempFile(LocalDate.now()));
            assertFalse(ex.getMessage().contains("Failed to create temp file"));
        }
    }

    @Test
    void createTempFile_unix_noPermission() throws IOException {
        // Mocking the Unix/Linux OS condition
        System.setProperty("os.name", "Linux");

        // Creating a file with no permissions
        try (MockedStatic<Files> mockedFiles = Mockito.mockStatic(Files.class)) {
            // Мокируем ошибку при создании временного файла
            mockedFiles.when(() -> Files.createTempFile(any(Path.class), anyString(), anyString(), any()))
                    .thenThrow(new IOException("Failed to create temp file"));

            // Проверяем, что выбрасывается IOException
            IllegalStateException ex = assertThrows(IllegalStateException.class, () -> logService.createTempFile(LocalDate.now()));
            assertTrue(ex.getMessage().contains("Failed to create temp file"));
        }
    }


    @Test
    void createTempFile_invalidOS() {
        // Setting OS name to an invalid value
        System.setProperty("os.name", "Unknown OS");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> logService.createTempFile(LocalDate.now()));
        assertTrue(ex.getMessage().contains("Unsupported OS"));
    }


    @Test
    void downloadLogs_invalidDateFormat_throwsException() {
        String invalidDate = "2025-04-10";  // Неверный формат

        // Проверка, что выбрасывается исключение при неверном формате даты
        InvalidInputException ex = assertThrows(InvalidInputException.class, () -> logService.downloadLogs(invalidDate));
        assertEquals("Invalid date format. Required dd-MM-yyyy", ex.getMessage());
    }


    @Test
    void testDownloadLogsWithInvalidDate() {
        String invalidDate = "2025-13-40"; // Невалидная дата
        assertThrows(InvalidInputException.class, () -> {
            logService.downloadLogs(invalidDate);
        });
    }

    @Test
    void testDownloadLogsWhenLogFileDoesNotExist() throws IOException {
        String date = "2025-04-10";
        Path logFilePath = Files.createTempFile("tempLog", ".txt"); // Create a real temporary file
        Files.delete(logFilePath); // Ensure the file doesn't exist

        assertThrows(InvalidInputException.class, () -> {
            logService.downloadLogs(date);
        });
    }

    @Test
    void shouldCreateTempFileSuccessfully() throws Exception {
       // LogService logService = new LogService(); // Создаём реальный экземпляр лог-сервиса

        // Получаем результат
        Path result = logService.createTempFile(LocalDate.now());

        // Проверяем, что результат не null
        assertNotNull(result);

        // Проверяем, что путь относится к папке SECURE_TEMP_DIR
        assertTrue(result.toString().startsWith(logService.SECURE_TEMP_DIR.toString()));

        // Дополнительная проверка: ожидаем, что путь будет заканчиваться на ".log"
        assertTrue(result.toString().endsWith(".log"));
    }

    @Test
    void shouldHandleNullDateInput() {
        // Тестируем поведение при null-значении для даты
        assertThrows(NullPointerException.class, () -> logService.downloadLogs(null));
    }


}



