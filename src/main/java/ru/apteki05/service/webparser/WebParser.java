package ru.apteki05.service.webparser;

import ru.apteki05.output.MedicineOutputModel;

import java.util.List;

public interface WebParser {

    List<MedicineOutputModel> request(String medicineName, Long maxMedicineId);
}
