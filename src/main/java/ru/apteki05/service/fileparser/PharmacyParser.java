package ru.apteki05.service.fileparser;

import ru.apteki05.model.Medicine;
import ru.apteki05.model.Pharmacy;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface PharmacyParser {
    List<Medicine> parse(File file, Pharmacy pharmacy) throws IOException;
}
