package ru.apteki05.model.parser.syrupandpill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceItem {

    public String barCode;

    private String country;

    private String itemName;

    private String quantity;

    private String farmItemId;

    private BigDecimal price;

    private String manufName;

}
