package ru.apteki05.service.webparser;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.apteki05.output.MedicineOutputModel;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
public class DagPharmParser implements WebParser {

    private static final int TIMEOUT = 3000;
    private static final String PHARMACY_NAME = "ДагФарм";
    private static final String PHARMACY_CITY = "г. Махачкала";
    private static final String URL = "https://dagfarm.ru/search";

//    public static void main(String[] args) {
//        DagPharmParser dagPharmParser = new DagPharmParser();
//        List<Medicine> tableItems = dagPharmParser.request("нурофен");
//        tableItems.forEach(System.out::println);
//    }

    public List<MedicineOutputModel> request(String drugName, Long maxMedicineId) {
        try {
            return requestInner(drugName, maxMedicineId);
        } catch (IOException | RuntimeException e) {
            log.error("Error while sending request {} to dagpharm: {}", drugName, e);
            return emptyList();
        }
    }

    public List<MedicineOutputModel> requestInner(String drugName, long id) throws IOException {
        
        Document doc = Jsoup.connect(URL)
                .data("utf8", "✓", "text", drugName)
                .timeout(TIMEOUT)
                .get();

        if (doc.getElementsByClass("products_list").isEmpty()) {
            log.info("Nothing found in dagPharm");
            return emptyList();
        }

        Elements table = doc.getElementsByClass("products_list").iterator().next().children();

        List<MedicineOutputModel> result = new ArrayList<>();
        MedicineOutputModel medicineOutputModel = new MedicineOutputModel();

        for (Element tableElement : table) {

            String medicineName = tableElement.getElementsByClass("card-hover-title").text();

            if (medicineName == null || medicineName.isBlank()) {
                continue;
            }


            Elements branchesList = tableElement.getElementsByTag("tbody").iterator().next().children();

            for (Element branch : branchesList) {

                Elements addressAndCount = branch.children();
                if (addressAndCount.isEmpty()) {
                    continue;
                }

                String branchAddress = addressAndCount.get(0).text();
                String branchMedicineCountStr = addressAndCount.get(1).text();

                long branchMedicineCount;
                if (branchMedicineCountStr.toLowerCase().contains("много")) {
                    branchMedicineCount = 100L;
                } else {
                    branchMedicineCountStr = branchMedicineCountStr
                            .replace(" шт.", "")
                            .replaceAll(ParserConstants.NOT_DIGITS_AND_DOT, "");
                    branchMedicineCount = Long.parseLong(branchMedicineCountStr);
                }

                String priceStr = tableElement.getElementsByClass("price").text()
                        .replaceAll(ParserConstants.NOT_DIGITS_AND_DOT, "");

                id += 1;

                medicineOutputModel.setId(id);
                medicineOutputModel.setMedicineName(medicineName);
                medicineOutputModel.setPrice(new BigDecimal(priceStr));
                medicineOutputModel.setCount(branchMedicineCount);
                medicineOutputModel.setShopName(PHARMACY_NAME);
                medicineOutputModel.setShopAddress(PHARMACY_CITY + ", " + branchAddress);
                medicineOutputModel.setUpdateDate(now().minusDays(1));

                result.add(medicineOutputModel);

                medicineOutputModel = new MedicineOutputModel();
            }
        }

        return result.stream()
                .filter(x -> x.getUpdateDate().isAfter(now().minusWeeks(3)))
                .collect(toList());
    }
}
