package ru.apteki05.service.fileparser.kasppharm;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.parser.kasppharm.Warebase;
import ru.apteki05.service.fileparser.PharmacyParser;

import static java.util.stream.Collectors.toList;

public class KaspPharmParser implements PharmacyParser {

    private static final XmlMapper MAPPER = new XmlMapper();

    @Override
    public List<Medicine> parse(File xmlFile, Pharmacy pharmacy) throws IOException {
        String content = Files.readString(xmlFile.toPath(), Charset.forName("windows-1251"));
        Warebase warebase = MAPPER.readValue(content, Warebase.class);

        return convertToMedicines(warebase, pharmacy);
    }

    private List<Medicine> convertToMedicines(Warebase warebase, Pharmacy pharmacy) {
        return warebase.getWares().stream()
                .map(x -> {
                    Medicine medicine = new Medicine();
                    medicine.setPrice(new BigDecimal(x.getPrice().replace(",", ".")));
                    medicine.setName(x.getName());
                    medicine.setCount(Long.valueOf(x.getQuant()));
                    medicine.setPharmacy(pharmacy);
                    medicine.setUpdatedAt(LocalDateTime.now());

                    return medicine;
                }).collect(toList());
    }
}
