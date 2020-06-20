package ru.apteki05.model.input.xml;

import lombok.Data;

@Data
public class UnikoXml {
    private Agent agent;

    private String spoType;

    private String schemaVer;

    private String creationDate;

    private Prices prices;
}
