package ru.apteki05.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.apteki05.model.Medicine;
import ru.apteki05.output.ListResult;
import ru.apteki05.output.ListResultUtil;
import ru.apteki05.output.MedicineOutputModel;
import ru.apteki05.service.SearchService;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public ListResult<MedicineOutputModel> search(
            @RequestParam String medicineNameFilter,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        List<MedicineOutputModel> fromDB = searchService.fuzzySearch(medicineNameFilter);
        List<MedicineOutputModel> fromOutside = searchService.outsideSearch(medicineNameFilter);

        List<MedicineOutputModel> all = new ArrayList<>(fromDB);
        all.addAll(fromOutside);

        return ListResultUtil.getResult(all, page, size);
    }
}
