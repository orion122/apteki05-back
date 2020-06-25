package ru.apteki05.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Medicine;
import ru.apteki05.repository.MedicineRepository;

import java.util.List;

@Component
public class SearchService {

    private final MedicineRepository medicineRepository;

    @Autowired
    public SearchService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> search(String word, Integer page, Integer size) {
        return medicineRepository.findAllByNameContainingIgnoreCase(word);
    }
}
