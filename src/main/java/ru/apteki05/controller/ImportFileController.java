package ru.apteki05.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.service.BackupStorageService;
import ru.apteki05.service.ImportFileService;

import java.io.File;
import java.io.IOException;

@RestController
public class ImportFileController {

    private final BackupStorageService backupStorageService;
    private final ImportFileService importFileService;

    @Autowired
    public ImportFileController(BackupStorageService backupStorageService, ImportFileService importFileService) {
        this.backupStorageService = backupStorageService;
        this.importFileService = importFileService;
    }

    /**
     * Сохраняет файл на диск и данные из него импортирует в БД
     * todo: сделать фонову задачу для импорта данных в БД
     *
     * @param multipartFile - файл от аптеки
     * @param token - токен, по которому однозначно определяется аптека
     */
    @PostMapping("/importFile")
    public void importFile(@RequestParam("file") MultipartFile multipartFile, @RequestParam String token) throws IOException {
        File file = backupStorageService.saveFile(multipartFile, token);
        importFileService.importToDB(file, token);
    }
}
