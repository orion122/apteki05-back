package ru.apteki05.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BackupStorageService {

    private String importedFilesDir;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    @Autowired
    public BackupStorageService(@Value("${importedFilesDir}") String importedFilesDir) {

        this.importedFilesDir = importedFilesDir;
    }

    /**
     * Сохраняет файл на диск
     */
    public File saveFile(MultipartFile multipartFile, String token) throws IOException {
        String fullFileName = multipartFile.getOriginalFilename();
        String fileExtension = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        log.info("Import file with name {}. Token: {}", fullFileName, token);

        String fileName = getFileName(token, LocalDateTime.now().format(FORMATTER), fileExtension);
        File file = new File(importedFilesDir + File.separator + fileName);
        FileUtils.copyInputStreamToFile(multipartFile.getInputStream(), file);

        return file;
    }

    private String getFileName(String token, String dateTime, String fileExtension) {
        return (token + "_" + dateTime + '.' + fileExtension);
    }
}
