package ru.apteki05.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.apteki05.model.Medicine;
import ru.apteki05.output.ListResult;
import ru.apteki05.output.MedicineOutputModel;
import ru.apteki05.service.SearchService;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ListResult<MedicineOutputModel> search(@RequestParam String medicineNameFilter, Integer page, Integer size) {
        List<Medicine> medicines = searchService.search(medicineNameFilter, page, size);

        List<MedicineOutputModel> medicineOutputModel = medicines.stream()
                .map(MedicineOutputModel::new)
                .collect(toList());

        return ListResult.of(medicineOutputModel, size, page);
    }
}
