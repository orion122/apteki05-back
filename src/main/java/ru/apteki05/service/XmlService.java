package ru.apteki05.service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.input.xml.PriceItem;
import ru.apteki05.model.input.xml.UnikoXml;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.repository.PharmacyRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Slf4j
@Service
public class XmlService {
    private static final XmlMapper MAPPER = new XmlMapper();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");

    private String importedFilesDir;

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;

    @Autowired
    public XmlService(@Value("${importedFilesDir}") String importedFilesDir, PharmacyRepository pharmacyRepository, MedicineRepository medicineRepository) {
        this.importedFilesDir = importedFilesDir;
        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
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

        List<Medicine> medicines = parseXml(file, pharmacy);

        medicineRepository.deleteAllInBatch();
        medicineRepository.saveAll(medicines);
    }

    private String getFileName(String token, String dateTime) {
        return (token + "_" + dateTime + ".xml");
    }

    private List<Medicine> parseXml(File xmlFile, Pharmacy pharmacy) throws IOException {
        UnikoXml unikoXml = MAPPER.readValue(xmlFile, UnikoXml.class);

        Collection<PriceItem> priceItems = unikoXml.getPrices().getPriceItems().stream()
                .collect(toMap(
                        PriceItem::getBarCode,
                        identity(),
                        this::getMostExpensivePriceItems))
                .values();

        return convertToMedicines(priceItems, pharmacy);
    }

    private List<Medicine> convertToMedicines(Collection<PriceItem> priceItems, Pharmacy pharmacy) {
        return priceItems.stream()
                .map(x -> {
                    Medicine medicine = new Medicine();
                    medicine.setPrice(x.getPrice());
                    medicine.setName(x.getItemName());
                    medicine.setCount(Long.valueOf(x.getQuantity()));
                    medicine.setPharmacy(pharmacy);
                    medicine.setUpdatedAt(LocalDateTime.now());

                    return medicine;
                }).collect(toList());
    }

    private PriceItem getMostExpensivePriceItems(PriceItem x, PriceItem y) {
        if (x.getPrice().compareTo(y.getPrice()) > 0) {
            return x;
        } else {
            return y;
        }
    }
}
