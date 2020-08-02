package ru.apteki05.service.fileparser;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.repository.PharmacyRepository;
import ru.apteki05.service.fileparser.kasppharm.KaspPharmParser;
import ru.apteki05.service.fileparser.ruspharm.RusPharmParser;
import ru.apteki05.service.fileparser.syrupandpill.SyrapAndPillParser;
import ru.apteki05.service.fileparser.zolotayaseredina.ZolotayaSeredinaParser;

@Component
public class ParserFactory {

    private final PharmacyRepository pharmacyRepository;

    private Map<String, Supplier<PharmacyParser>> pharmacyParsers = Map.of(
            "syrup_and_pill", SyrapAndPillParser::new,
            "zolotaya_seredina", ZolotayaSeredinaParser::new,
            "rus_pharm", RusPharmParser::new,
            "kasp_pharm", KaspPharmParser::new
    );

    @Autowired
    public ParserFactory(PharmacyRepository pharmacyRepository) {
        this.pharmacyRepository = pharmacyRepository;
    }

    public PharmacyParser getParser(String token) {
        Optional<Pharmacy> optionalPharmacy = pharmacyRepository.findByToken(token);
        optionalPharmacy.orElseThrow(() -> new IllegalArgumentException(String.format("Аптека с токеном %s не " +
                "найдена", token)));
        Pharmacy pharmacy = optionalPharmacy.get();

        return pharmacyParsers.get(pharmacy.getShortName()).get();
    }
}
