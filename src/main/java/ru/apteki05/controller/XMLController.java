package ru.apteki05.controller;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.input.xml.PriceItems;
import ru.apteki05.model.input.xml.UnikoXml;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.repository.PharmacyRepository;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@RestController
public class XMLController {

    @Value("${importedFiles}")
    private String IMPORTED_FILES;

    private static final XmlMapper MAPPER = new XmlMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;

    @Autowired
    public XMLController(PharmacyRepository pharmacyRepository, MedicineRepository medicineRepository) {
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
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
        String fullFileName = xmlFile.getOriginalFilename();
//        String fileName = FilenameUtils.removeExtension(fullFileName);
//        String fileExtension = FilenameUtils.getExtension(xmlFile.getOriginalFilename());

        log.info("Import file with name {}. Token: {}", fullFileName, token);

        String fileName = getFileName(token, LocalDateTime.now().format(FORMATTER));
        File savedFile = saveFile(xmlFile.getInputStream(), fileName);

        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByToken(token);
        optionalPharmacy.orElseThrow(() -> new IllegalArgumentException(String.format("Аптека с токеном %s не найдена", token)));
        Pharmacy pharmacy = optionalPharmacy.get();

        Collection<PriceItems> priceItems = parseXml(savedFile);
        importToDB(priceItems, pharmacy);
    }

    private File saveFile(InputStream inputStream, String fileName) throws IOException {

        File file = new File(IMPORTED_FILES + File.separator + fileName);
        FileUtils.copyInputStreamToFile(inputStream, file);
        return file;
    }

    private String getFileName(String token, String dateTime) {
        return (token + "_" + dateTime + ".xml");
    }

    private Collection<PriceItems> parseXml(File xmlFile) throws IOException {
        UnikoXml unikoXml = MAPPER.readValue(xmlFile, UnikoXml.class);

        return unikoXml.getPrices().getPriceItems().stream()
                .collect(toMap(
                        PriceItems::getBarCode,
                        identity(),
                        this::getMostExpensivePriceItems))
                .values();
    }

    private void importToDB(Collection<PriceItems> priceItems, Pharmacy pharmacy) {
        List<Medicine> medicines = priceItems.stream()
                .map(x -> {
                    Medicine medicine = new Medicine();
                    medicine.setPrice(x.getPrice());
                    medicine.setName(x.getItemName());
                    medicine.setCount(Long.valueOf(x.getQuantity()));
                    medicine.setPharmacy(pharmacy);
                    medicine.setUpdatedAt(LocalDateTime.now());

                    return medicine;
                }).collect(toList());

        medicineRepository.saveAll(medicines);
    }

    private PriceItems getMostExpensivePriceItems(PriceItems x, PriceItems y) {
        if (x.getPrice().compareTo(y.getPrice()) > 0) {
            return x;
        } else {
            return y;
        }
    }
}
