package ru.apteki05.model.input.xml;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceItems {

    public String barCode;

    private String country;

    private String itemName;

    private String quantity;

    private String farmItemId;

    private BigDecimal price;

    private String manufName;

}
