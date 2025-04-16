package com.example.hotelbookingv2.service;

import com.example.hotelbookingv2.exception.InvalidInputException;
import com.example.hotelbookingv2.exception.ResourceNotFoundException;
import com.example.hotelbookingv2.model.LogObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


@Slf4j
@Service
public class LogService {

    private final AtomicLong idCounter = new AtomicLong(1);
    private final Map<Long, LogObject> tasks = new ConcurrentHashMap<>();
    private static final String LOG_FILE_PATH = "log/app.log";
    private final LogService self;

    public LogService(@Lazy LogService self) {
        this.self = self;
    }

    public static final Path SECURE_TEMP_DIR = Paths.get("/Users/margarita/"
            + "IdeaProjects/hotelbooking-v2");

    static {
        try {
            if (!Files.exists(SECURE_TEMP_DIR)) {
                Files.createDirectories(SECURE_TEMP_DIR);
                log.info("Created secure temporary directory: {}", SECURE_TEMP_DIR);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot create secure temp directory", e);
        }
    }




    public Resource downloadLogs(String date) {
        LocalDate logDate = parseDate(date);
        Path logFilePath = Paths.get(LOG_FILE_PATH);
        validateLogFileExists(logFilePath);
        String formattedDate = logDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        Path tempFile = createTempFile(logDate);
        filterAndWriteLogsToTempFile(logFilePath, formattedDate, tempFile);

        Resource resource = createResourceFromTempFile(tempFile, date);
        log.info("Log file with date {} downloaded successfully", date);
        return resource;
    }

    public LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Required dd-MM-yyyy");
        }
    }

    public void validateLogFileExists(Path path) {
        if (!Files.exists(path)) {
            throw new ResourceNotFoundException("File doesn't exist: " + LOG_FILE_PATH);
        }
    }

    public Path createTempFile(LocalDate logDate) {
        try {
            String osName = System.getProperty("os.name").toLowerCase();

            if (osName.contains("win")) {
                File tempFile = Files.createTempFile(SECURE_TEMP_DIR, "log-"
                        + logDate + "-", ".log").toFile();
                if (!tempFile.setReadable(true, true)) {
                    throw new IllegalStateException("Failed to set readable permission "
                            + "on temp file: " + tempFile);
                }
                if (!tempFile.setWritable(true, true)) {
                    throw new IllegalStateException("Failed to set writable permission "
                            + "on temp file: " + tempFile);
                }
                if (tempFile.canExecute() && !tempFile.setExecutable(false, false)) {
                    log.warn("Failed to remove executable permission on temp file: {}", tempFile);
                }
                log.info("Created secure temp file on Windows: {}", tempFile.getAbsolutePath());
                return tempFile.toPath();
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("mac")) {
                FileAttribute<Set<PosixFilePermission>> attr = PosixFilePermissions.asFileAttribute(
                        PosixFilePermissions.fromString("rw-------"));
                Path tempFile = Files.createTempFile(SECURE_TEMP_DIR, "log-" + logDate + "-",
                        ".log", attr);
                log.info("Created secure temp file on Unix/Linux: {}", tempFile.toAbsolutePath());
                return tempFile;
            } else {
                throw new IllegalStateException("Unsupported OS: " + osName);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Error creating temp file: " + e.getMessage());
        }
    }


    public void filterAndWriteLogsToTempFile(Path logFilePath,
                                              String formattedDate, Path tempFile) {
        try (BufferedReader reader = Files.newBufferedReader(logFilePath)) {
            Files.write(tempFile, reader.lines()
                    .filter(line -> line.contains(formattedDate))
                    .toList());
            log.info("Filtered logs for date {} written to temp file {}", formattedDate, tempFile);
        } catch (IOException e) {
            throw new IllegalStateException("Error processing log file: " + e.getMessage());
        }
    }

    public Resource createResourceFromTempFile(Path tempFile, String date) {
        try {
            if (Files.size(tempFile) == 0) {
                throw new ResourceNotFoundException("There are no logs for specified date: "
                        + date);
            }
            Resource resource = new UrlResource(tempFile.toUri());
            tempFile.toFile().deleteOnExit();
            log.info("Created downloadable resource from temp file: {}", tempFile);
            return resource;
        } catch (IOException e) {
            throw new IllegalStateException("Error creating resource from temp file: "
                    + e.getMessage());
        }
    }


    @Async("executor")
    public void createLogs(Long taskId, String date) {
        try {
            Thread.sleep(15000);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate logDate = LocalDate.parse(date, formatter);

            Path path = Paths.get(LOG_FILE_PATH);
            List<String> logLines = Files.readAllLines(path);
            String formattedDate = logDate.format(formatter);
            List<String> currentLogs = logLines.stream()
                    .filter(line -> line.startsWith(formattedDate))
                    .toList();

            if (currentLogs.isEmpty()) {
                LogObject logObject = tasks.get(taskId);
                if (logObject != null) {
                    logObject.setStatus("FAILED");
                    logObject.setErrorMessage("Нет логов за дату: " + date);
                }
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Нет логов за дату: " + date);
            }

            Path logFile;
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                logFile = Files.createTempFile("logs-" + formattedDate, ".log");
            } else {
                FileAttribute<Set<PosixFilePermission>> attr =
                        PosixFilePermissions.asFileAttribute(
                                PosixFilePermissions.fromString("rwx------"));
                logFile = Files.createTempFile("logs-" + formattedDate, ".log", attr);
            }

            Files.write(logFile, currentLogs);
            logFile.toFile().deleteOnExit();

            LogObject task = tasks.get(taskId);
            if (task != null) {
                task.setStatus("COMPLETED");
                task.setFilePath(logFile.toString());
            }
        } catch (IOException e) {
            LogObject task = tasks.get(taskId);
            if (task != null) {
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public Long createLogAsync(String date) {
        Long id = idCounter.getAndIncrement();
        LogObject logObject = new LogObject(id, "IN_PROGRESS");
        tasks.put(id, logObject);
        self.createLogs(id, date);
        return id;
    }

    public LogObject getStatus(Long taskId) {
        return tasks.get(taskId);
    }

    public ResponseEntity<Resource> downloadCreatedLogs(Long taskId) throws IOException {
        log.info("Download request for log ID: {}", taskId);

        LogObject logObject = getStatus(taskId);
        if (logObject == null) {
            log.error("LogObject not found for task ID: {}", taskId);
            throw new ResourceNotFoundException("Log file not found for ID: " + taskId);
        }

        log.info("LogObject found: {}", logObject);

        if (!"COMPLETED".equals(logObject.getStatus())) {
            log.error("Log file is not ready. Current status: {}", logObject.getStatus());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The logs are not ready yet");
        }

        Path path = Paths.get(logObject.getFilePath());
        if (!Files.exists(path)) {
            log.error("Log file does not exist at path: {}", path);
            throw new ResourceNotFoundException("Log file does not exist at path: " + path);
        }

        Resource resource = new UrlResource(path.toUri());
        log.info("Returning file for download: {}", path);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

}