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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.apteki05.service.webparser.ParserConstants.NOT_DIGITS;
import static ru.apteki05.service.webparser.ParserConstants.NOT_DIGITS_AND_DOT;

@Slf4j
@Service
public class AptechniyDomParser implements WebParser {
    private static final String PHARMACY_NAME = "Аптечный Дом";
    private static final String FARMACY_ADDRESS = "г. Махачкала, ул. имама Шамиля, 18Д,";
    private static final int TIMEOUT = 2000;
    private static final String URL = "https://www.05apteka.ru/search.php";

//    public static void main(String[] args) {
//        AptechniyDomParser aptechniyDomParser = new AptechniyDomParser();
//        List<MedicineOutputModel> tableItems = aptechniyDomParser.request("Маска медицинская");
//        tableItems.forEach(System.out::println);
//    }

    public List<MedicineOutputModel> request(String searchQuery, Long maxMedicineId) {
        try {
            return requestInner(searchQuery, maxMedicineId);
        } catch (IOException | RuntimeException e) {
            log.error("Error while sending request {} to aptechniyDom: {}", searchQuery, e);
            return emptyList();
        }
    }

    public List<MedicineOutputModel> requestInner(String searchQuery, long id) throws IOException {

        String encodedDrugName = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);

        Document doc = Jsoup.connect(URL)
                .timeout(TIMEOUT)
                .requestBody("referal=" + encodedDrugName)
                .post();


        Elements allElements = doc.getElementsByTag("li");

        if (allElements.isEmpty()) {
            log.info("Nothing found in aptechniyDom");
            return emptyList();
        }

        List<MedicineOutputModel> result = new ArrayList<>();
        MedicineOutputModel medicineOutputModel = new MedicineOutputModel();

        for (Element tableElement : allElements) {
            String text = tableElement.text();

            int lastIndexSlash = text.lastIndexOf("/");
            String price = text.substring(lastIndexSlash + 1);

            text = text.substring(0, lastIndexSlash);
            lastIndexSlash = text.lastIndexOf("/");
            String count = text.substring(lastIndexSlash + 1);

            String medicineName = text.substring(0, lastIndexSlash);

            count = count.replaceAll(NOT_DIGITS, "");

            price = price.replaceAll(NOT_DIGITS_AND_DOT, "");

            id += 1;

            medicineOutputModel.setId(id);
            medicineOutputModel.setMedicineName(medicineName);
            medicineOutputModel.setPrice(new BigDecimal(price));
            medicineOutputModel.setCount(Long.valueOf(count));
            medicineOutputModel.setShopName(PHARMACY_NAME);
            medicineOutputModel.setShopAddress(FARMACY_ADDRESS);
            medicineOutputModel.setUpdateDate(now().minusDays(1));

            result.add(medicineOutputModel);

            medicineOutputModel = new MedicineOutputModel();
        }

        return result.stream()
                .filter(x -> x.getUpdateDate().isAfter(now().minusWeeks(3)))
                .collect(toList());
    }

}
