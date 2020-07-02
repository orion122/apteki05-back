package ru.apteki05.service.fileparser.zolotayaseredina;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.service.fileparser.PharmacyParser;

import static java.time.LocalDateTime.now;
import static java.util.stream.Collectors.toList;

public class ZolotayaSeredinaParser implements PharmacyParser {

    @Override
    public List<Medicine> parse(File file, Pharmacy pharmacy) throws IOException {

        String content = Files.readString(file.toPath());

        return Arrays.stream(content.split("\n"))
                .map(x -> {
                            Medicine item = new Medicine();
                            item.setPharmacy(pharmacy);
                            item.setUpdatedAt(now());
                            item.setName(x);
                            item.setCount(1L);
                            item.setPrice(null);
                            return item;
                        }
                ).collect(toList());
    }
}
