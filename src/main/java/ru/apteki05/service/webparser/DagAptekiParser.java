package ru.apteki05.service.webparser;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import ru.apteki05.output.MedicineOutputModel;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static ru.apteki05.service.webparser.ParserConstants.NOT_DIGITS_AND_DOT;

@Slf4j
@Service
public class DagAptekiParser implements WebParser {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy.dd.MM");
    private static final String RIGHT_TIME = "2020.%s";
    private static final int TIMEOUT = 3000;
    private static final String URL = "http://dagapteki.ru/search/";


//    public static void main(String[] args) {
//        DagaptekiParser dagaptekiParser = new DagaptekiParser();
//        List<MedicineOutputModel> tableItems = dagaptekiParser.request("нос");
//        tableItems.forEach(System.out::println);
//    }

    public List<MedicineOutputModel> request(String searchQuery, Long maxMedicineId) {
        try {
            return requestInner(searchQuery, maxMedicineId);
        } catch (IOException | RuntimeException e) {
            log.error("Error while sending request {} to dagapteki: {}", searchQuery, e);
            return emptyList();
        }
    }

    private List<MedicineOutputModel> requestInner(String searchQuery, long id) throws IOException {

        String encodedMedicineName = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);

        Document doc = Jsoup.connect(URL)
                .timeout(TIMEOUT)
                .requestBody("search_text_s=&search_text=" + encodedMedicineName)
                .post();

        if (doc.getElementsByTag("tbody").isEmpty()) {
            log.info("Nothing found in dagapteki");
            return emptyList();
        }

        Elements table = doc.getElementsByTag("tbody").iterator().next().children();

        List<MedicineOutputModel> result = new ArrayList<>();
        MedicineOutputModel medicineOutputModel = new MedicineOutputModel();

        for (Element tableElement : table) {
            if (tableElement.toString().contains("width=\"30%\">адрес</td>")) {
                continue;
            }


            if (!tableElement.getElementsByTag("th").isEmpty()) {
                medicineOutputModel.setMedicineName(tableElement.text());
                continue;
            }

            Elements children = tableElement.children();
            for (int i = 0; i < children.size(); i++) {
                Element child = children.get(i);
                if (i == 0) {
                    medicineOutputModel.setShopName(child.text());
                }

                if (i == 1) {
                    medicineOutputModel.setShopAddress(child.text());
                }

                if (i == 2) {
//                    Номер телефона: \t" + child.text();
                }

                if (i == 3) {
                    String price = child.text()
                            .replaceAll(NOT_DIGITS_AND_DOT, "");
//                            .replaceAll("\\s+", "")
//                            .replace(" ", "");
                    medicineOutputModel.setPrice(new BigDecimal(price));
                }

                if (i == 4) {
                    medicineOutputModel.setUpdateDate(LocalDateTime.from(parseUpdateTime(child.text())));
                }

            }

            id += 1;

            medicineOutputModel.setCount(1L);
            medicineOutputModel.setId(id);
            result.add(medicineOutputModel);
            // если дошли до сюды, значит обработали объект полностью
            String oldMedicineName = medicineOutputModel.getMedicineName();
            medicineOutputModel = new MedicineOutputModel();
            medicineOutputModel.setMedicineName(oldMedicineName);
        }

        return result.stream()
                .filter(x -> x.getUpdateDate().isAfter(LocalDateTime.now().minusWeeks(3)))
                .collect(toList());
    }

    private static LocalDate parseUpdateTime(String str) {
        LocalDate updateTime;
        try {
            updateTime = LocalDate.parse(String.format(RIGHT_TIME, str), TIME_FORMATTER);

            // Не знаем год, поэтому сконкатенированная дата может быть в будущем
            if (updateTime.isAfter(LocalDate.now())) {
                updateTime = LocalDate.MIN;
            }

        } catch (DateTimeParseException e) {
            log.error("Can not parse date at dagapteki: {}", e.getMessage());
            updateTime = LocalDate.now();
        }
        return updateTime;
    }

}
