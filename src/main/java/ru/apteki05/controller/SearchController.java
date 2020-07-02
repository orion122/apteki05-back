package ru.apteki05.controller;

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

        String searchQuery = StringUtils.normalizeSpace(medicineNameFilter).toLowerCase();

        List<MedicineOutputModel> medicines = searchService.aggregatedSearch(searchQuery);

        return ListResultUtil.getResult(medicines, page, size);
    }
}
