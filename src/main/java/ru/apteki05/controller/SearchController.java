package ru.apteki05.controller;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.apteki05.output.ListResult;
import ru.apteki05.output.ListResultUtil;
import ru.apteki05.output.MedicineOutputModel;
import ru.apteki05.service.SearchService;

@RestController
@RequestMapping("/api")
@Slf4j
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

        String searchQuery = StringUtils.normalizeSpace(medicineNameFilter).toLowerCase();

        if (searchQuery.length() < 2) {
            throw new RuntimeException("Forbidden. Search: " + searchQuery);
        }

        List<MedicineOutputModel> medicines = searchService.aggregatedSearch(searchQuery);

        log.info("{} items, page: {}, by filter: {}", medicines.size(), page, medicineNameFilter);
        return ListResultUtil.getResult(medicines, page, size);
    }
}
