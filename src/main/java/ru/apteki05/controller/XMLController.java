package ru.apteki05.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.service.XmlService;

import java.io.File;
import java.io.IOException;

@RestController
public class XMLController {
    private final XmlService xmlService;

    @Autowired
    public XMLController(XmlService xmlService) {
        this.xmlService = xmlService;
    }

    /**
     * Сохраняет xml-файл на диск и данные из него импортирует в БД
     * todo: сделать фонову задачу для импорта данных в БД
     *
     * @param xmlFile - файл от аптеки
     * @param token   - токен, по которому однозначно определяется аптека
     */
    @PostMapping("/importXML")
    public void importXML(@RequestParam("file") MultipartFile xmlFile, @RequestParam String token) throws IOException {
        File file = xmlService.saveFile(xmlFile, token);
        xmlService.importToDB(file, token);
    }
}
