package ru.apteki05.controller;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.output.ListResult;
import ru.apteki05.output.MedicineOutputModel;

/**
 * Прокси для старых аптек
 */
@Deprecated
@RestController
@RequiredArgsConstructor
@Slf4j
public class LegacyController {
    private final SearchController searchController;
    private final ImportDataController importDataController;

    @Value("${spring.flyway.placeholders.syrup_and_pill}")
    private String syrupAndPillToken;

    @GetMapping("api/search2")
    @ResponseStatus(HttpStatus.OK)
    public ListResult<MedicineOutputModel> search2(
            @RequestParam(value = "medicineNameFilter", required = false) String medicineNameFilter,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        log.info("Legacy controller");
        return searchController.search(medicineNameFilter, page, size);
    }

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
