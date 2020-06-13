package ru.apteki05.controller;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.repository.PharmacyRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@RestController
public class XMLController {

    @Value("${XML_DIR}")
    private String XML_DIR;

    @Autowired
    PharmacyRepository pharmacyRepository;

    /**
     * Сохраняет xml-файл на диск и данные из него импортирует в БД
     * todo: сделать фонову задачу для импорта данных в БД
     *
     * @param xmlFile - файл от аптеки
     * @param token   - токен, по которому однозначно определяется аптека
     */
    @PostMapping("/importXML")
    public void importXML(@RequestParam("file") MultipartFile xmlFile, @RequestParam String token) throws IOException {
        InputStream inputStream = xmlFile.getInputStream();

        String fullFileName = xmlFile.getOriginalFilename();
//        String fileName = FilenameUtils.removeExtension(fullFileName);
//        String fileExtension = FilenameUtils.getExtension(xmlFile.getOriginalFilename());


        log.info("Import file with name {}", fullFileName);
        String pharmacyName = pharmacyRepository.findNameByToken(token);

        saveFile(inputStream, pharmacyName);

        // todo: import data to db
    }

    private void saveFile(InputStream inputStream, String fileName) throws IOException {

        File file = new File(XML_DIR + File.separator + fileName + ".xml");
        FileUtils.copyInputStreamToFile(inputStream, file);
    }
}
