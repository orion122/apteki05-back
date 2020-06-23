package ru.apteki05.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.parser.ParserFactory;
import ru.apteki05.model.parser.PharmacyParser;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.repository.PharmacyRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class XmlService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private String importedFilesDir;

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final ParserFactory parserFactory;

    @Autowired
    public XmlService(
            @Value("${importedFilesDir}") String importedFilesDir,
            PharmacyRepository pharmacyRepository,
            MedicineRepository medicineRepository,
            ParserFactory parserFactory) {

        this.importedFilesDir = importedFilesDir;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
        this.parserFactory = parserFactory;
    }

    /**
     * Сохраняет xml-файл на диск
     */
    public File saveFile(MultipartFile xmlFile, String token) throws IOException {
        String fullFileName = xmlFile.getOriginalFilename();

        log.info("Import file with name {}. Token: {}", fullFileName, token);

        String fileName = getFileName(token, LocalDateTime.now().format(FORMATTER));
        File file = new File(importedFilesDir + File.separator + fileName);
        FileUtils.copyInputStreamToFile(xmlFile.getInputStream(), file);

        return file;
    }

    /**
     * Импортирует данные из файла в БД
     */
    public void importToDB(File file, String token) throws IOException {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByToken(token);
        optionalPharmacy.orElseThrow(() -> new IllegalArgumentException(String.format("Аптека с токеном %s не найдена", token)));
        Pharmacy pharmacy = optionalPharmacy.get();

        PharmacyParser pharmacyParser = parserFactory.getParser(token);
        List<Medicine> medicines = pharmacyParser.parse(file, pharmacy);

        medicineRepository.deleteByPharmacy(pharmacy);
        medicineRepository.saveAll(medicines);
    }

    private String getFileName(String token, String dateTime) {
        return (token + "_" + dateTime + ".xml");
    }
}
