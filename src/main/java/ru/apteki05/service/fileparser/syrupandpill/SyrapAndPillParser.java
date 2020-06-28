package ru.apteki05.service.fileparser.syrupandpill;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.parser.syrupandpill.PriceItem;
import ru.apteki05.model.parser.syrupandpill.UnikoXml;
import ru.apteki05.service.fileparser.PharmacyParser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

public class SyrapAndPillParser implements PharmacyParser {

    private static final XmlMapper MAPPER = new XmlMapper();

    public List<Medicine> parse(File xmlFile, Pharmacy pharmacy) throws IOException {
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
