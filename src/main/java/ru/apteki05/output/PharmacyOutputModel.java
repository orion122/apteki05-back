package ru.apteki05.output;

import lombok.Data;
import ru.apteki05.model.Pharmacy;

@Data
public class PharmacyOutputModel {
    private Long id;
    private String name;
    private String shortName;
    private String city;
    private String address;
    private String phone;
    private String url;
    private String timetable;

    public static PharmacyOutputModel of(Pharmacy source) {
        PharmacyOutputModel outputModel = new PharmacyOutputModel();
        outputModel.setId(source.getId());
        outputModel.setName(source.getName());
        outputModel.setCity(source.getCity());
        outputModel.setAddress(source.getAddress());
        outputModel.setPhone(source.getPhone());
        outputModel.setUrl(source.getUrl());
        outputModel.setTimetable(source.getTimetable());
        outputModel.setShortName(source.getShortName());
        return outputModel;
    }
}
