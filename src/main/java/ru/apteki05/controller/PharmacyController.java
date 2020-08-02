package ru.apteki05.controller;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.apteki05.model.Pharmacy;
import ru.apteki05.output.ListResult;
import ru.apteki05.output.ListResultUtil;
import ru.apteki05.output.PharmacyOutputModel;
import ru.apteki05.repository.PharmacyRepository;

import static ru.apteki05.utils.Utils.mapList;

@RestController
@RequestMapping("/api/pharmacies")
@RequiredArgsConstructor
public class PharmacyController {

    private final PharmacyRepository pharmacyRepository;

    @GetMapping
    public ListResult<PharmacyOutputModel> getPharmacies(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        List<Pharmacy> all = pharmacyRepository.findAll();
        List<PharmacyOutputModel> convertedModels = mapList(all, PharmacyOutputModel::of);
        return ListResultUtil.getResult(convertedModels, page, size);
    }

    @GetMapping("{shortName}")
    public PharmacyOutputModel getPharmacy(@PathVariable("shortName") String shortName) {

        Optional<Pharmacy> pharmacyOptional = pharmacyRepository.findByShortName(shortName);
        //todo: добавить exceptionHandler
        Pharmacy pharmacy = pharmacyOptional.orElseThrow(() -> new RuntimeException("Not found"));

        return PharmacyOutputModel.of(pharmacy);
    }


}
