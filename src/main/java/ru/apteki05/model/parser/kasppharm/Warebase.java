package ru.apteki05.model.parser.kasppharm;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Warebase {
    @JacksonXmlProperty(localName = "wares")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Ware> wares;
}
