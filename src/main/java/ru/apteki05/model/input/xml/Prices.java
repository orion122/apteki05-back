package ru.apteki05.model.input.xml;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Prices {
    private String pricingDate;

    @JacksonXmlProperty(localName = "priceItems")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<PriceItem> priceItems;

    private String roundForPrices;

    private String name;

    private String priceId;

    private String usePrice;
}
