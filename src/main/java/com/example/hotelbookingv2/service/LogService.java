package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import java.nio.file.attribute.PosixFilePermissions;

@Service
public class LogService {

    private static final String SECURE_TEMP_DIR = System.getProperty("java.io.tmpdir") + "/secure-logs";

    private Path createTempFile(LocalDate logDate) {
        try {
            // Создаём защищённую директорию (если её ещё нет)
            Path tempDir = Paths.get(SECURE_TEMP_DIR);
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);

                // Ограничиваем права доступа (Linux/macOS)
                if (Files.getFileStore(tempDir).supportsFileAttributeView("posix")) {
                    Files.setPosixFilePermissions(tempDir, PosixFilePermissions.fromString("rwx------"));
                }
            }

            // Создаём временный файл внутри защищённого каталога
            Path tempFile = Files.createTempFile(tempDir, "log-" + logDate + "-", ".log");

            // Ограничиваем права доступа для файла
            if (Files.getFileStore(tempFile).supportsFileAttributeView("posix")) {
                Files.setPosixFilePermissions(tempFile, PosixFilePermissions.fromString("rw-------"));
            }

            return tempFile;
        } catch (IOException e) {
            throw new IllegalStateException("Ошибка создания временного файла: " + e.getMessage());
        }
    }

    private static final String LOG_FILE_PATH = "log/app.log";

    public Resource downloadLogs(String date) {
        LocalDate logDate = parseDate(date);

        Path logFilePath = Paths.get(LOG_FILE_PATH);
        validateLogFileExists(logFilePath);

        String formattedDate = logDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Path tempFile = createTempFile(logDate);
        filterAndWriteLogsToTempFile(logFilePath, formattedDate, tempFile);

        return createResourceFromTempFile(tempFile, date);
    }

    private LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Required dd-MM-yyyy");
        }
    }

    private void validateLogFileExists(Path path) {
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File doesn't exist: " + LOG_FILE_PATH);
        }
    }


    private void filterAndWriteLogsToTempFile(Path logFilePath, String formattedDate,
                                              Path tempFile) {
        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            Files.write(tempFile, reader.lines()
                    .filter(line -> line.contains(formattedDate))
                    .toList());
        } catch (IOException e) {
            throw new IllegalStateException("Error processing log file: " + e.getMessage());
        }
    }

    private Resource createResourceFromTempFile(Path tempFile, String date) {
        try {
            if (Files.size(tempFile) == 0) {
                throw new ResourceNotFoundException(
                        "There are no logs for specified date: " + date);
            }
            Resource resource = new UrlResource(tempFile.toUri());
            tempFile.toFile().deleteOnExit();
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Error creating resource from temp file: "
                    + e.getMessage());
        }
    }
}