package ru.apteki05.model.parser.ruspharm;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.model.parser.PharmacyParser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

//@Component
public class RusPharmParser implements PharmacyParser {

    public List<Medicine> parse(File csvFile, Pharmacy pharmacy) throws IOException {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = CsvSchema.emptySchema().withHeader().withColumnSeparator(';');
        MappingIterator<RusPharmMedicine> iterator = mapper.readerFor(RusPharmMedicine.class).with(schema).readValues(csvFile);

        return convertToMedicines(iterator, pharmacy);
    }

    private List<Medicine> convertToMedicines(MappingIterator<RusPharmMedicine> iterator, Pharmacy pharmacy) {
        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(x -> {
                    Medicine medicine = new Medicine();
                    medicine.setPrice(x.getPrice());
                    medicine.setName(x.getName());
                    medicine.setCount(x.getAmount());
                    medicine.setPharmacy(pharmacy);
                    medicine.setUpdatedAt(LocalDateTime.now());

                    return medicine;
                })
                .collect(Collectors.toList());
    }
}
