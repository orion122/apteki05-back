package ru.apteki05.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.repository.PharmacyRepository;
import ru.apteki05.service.fileparser.ParserFactory;
import ru.apteki05.service.fileparser.PharmacyParser;

@Slf4j
@Service
public class ImportFileService {

    private final PharmacyRepository pharmacyRepository;
    private final MedicineRepository medicineRepository;
    private final ParserFactory parserFactory;

    @Autowired
    public ImportFileService(
            PharmacyRepository pharmacyRepository,
            MedicineRepository medicineRepository,
            ParserFactory parserFactory) {

        this.pharmacyRepository = pharmacyRepository;
        this.medicineRepository = medicineRepository;
        this.parserFactory = parserFactory;
    }

    /**
     * Импортирует данные из файла в БД
     */
    public void importToDB(File file, String token) throws IOException {
        Pharmacy pharmacy = getPharmacy(token);

        PharmacyParser pharmacyParser = parserFactory.getParser(token);
        List<Medicine> medicines = pharmacyParser.parse(file, pharmacy);

        medicineRepository.deleteByPharmacy(pharmacy);
        medicineRepository.saveAll(medicines);
    }

    private Pharmacy getPharmacy(String token) {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByToken(token);
        return optionalPharmacy.orElseThrow(
                () -> new IllegalArgumentException(String.format("Аптека с токеном %s не найдена", token)));
    }
}
