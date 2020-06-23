package ru.apteki05.model.parser.ruspharm;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RusPharmMedicine {
    private Long amount;
    private String name;
    private BigDecimal price;
}
