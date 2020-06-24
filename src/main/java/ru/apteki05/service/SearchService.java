package ru.apteki05.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Medicine;
import ru.apteki05.repository.MedicineRepository;

import java.util.List;

import static java.util.stream.Collectors.toList;


@Component
public class SearchService {

    private final MedicineRepository medicineRepository;

    @Autowired
    public SearchService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> search(String word, Integer page, Integer size) {
        List<Medicine> medicines = medicineRepository.findAllByNameContainingIgnoreCase(word);

        return medicines.stream()
                .skip(page * size)
                .limit(size)
                .collect(toList());
    }
}
