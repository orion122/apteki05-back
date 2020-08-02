package ru.apteki05.controller;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Прокси для старых аптек
 *
 * @deprecated см. ImportDataController
 */
@Deprecated
@RestController
@RequiredArgsConstructor
@Slf4j
public class LegacyController {
    private final ImportDataController importDataController;

    @Value("${spring.flyway.placeholders.syrup_and_pill}")
    private String syrupAndPillToken;

    @PostMapping("api/sendXml")
    public void sendXml(
            @RequestParam(name = "file") MultipartFile multipartFile) throws IOException {

        log.info("Legacy controller");
        importDataController.importFile(multipartFile, syrupAndPillToken.replace("'", ""));
    }

    @PostMapping("api/importXML")
    public void importXml(
            @RequestParam(name = "file") MultipartFile multipartFile, @RequestParam String token) throws IOException {

        log.info("Legacy controller");
        importDataController.importFile(multipartFile, token);
    }

}
