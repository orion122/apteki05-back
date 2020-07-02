package ru.apteki05.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.persistence.EntityManager;

import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Medicine;
import ru.apteki05.output.MedicineOutputModel;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.service.webparser.AptechniyDomParser;
import ru.apteki05.service.webparser.DagAptekiParser;
import ru.apteki05.service.webparser.DagPharmParser;
import ru.apteki05.service.webparser.WebParser;

import static ru.apteki05.utils.Utils.mapList;

@Component
@RequiredArgsConstructor
public class SearchService {

    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(15);
    private final MedicineRepository medicineRepository;
    private final DagAptekiParser dagAptekiParser;
    private final DagPharmParser dagPharmParser;
    private final AptechniyDomParser aptechniyDomParser;
    private final EntityManager entityManager;

    @Cacheable("medicines")
    public List<MedicineOutputModel> aggregatedSearch(String searchQuery) {
        List<MedicineOutputModel> fromDB = fuzzySearch(searchQuery);
        List<MedicineOutputModel> fromOutside = outsideSearch(searchQuery);

        List<MedicineOutputModel> all = new ArrayList<>(fromDB);
        all.addAll(fromOutside);

        return all;
    }

    private List<MedicineOutputModel> outsideSearch(String medicineNameFilter) {

        Long maxMedicineId = medicineRepository.getMaxId();
        List<MedicineOutputModel> result = new CopyOnWriteArrayList<>();

        List<WebParser> webParsers = List.of(dagAptekiParser, dagPharmParser, aptechniyDomParser);
        List<CompletableFuture<List<MedicineOutputModel>>> pharmacyFutures = new ArrayList<>();

        for (int i = 0; i < webParsers.size(); i++) {
            int idOffset = i * 1000;
            WebParser webParser = webParsers.get(i);

            var pharmacyFuture =
                    CompletableFuture.supplyAsync(() -> webParser.request(medicineNameFilter,
                            maxMedicineId + idOffset), EXECUTOR_SERVICE);

            pharmacyFuture.thenAccept(result::addAll);
            pharmacyFutures.add(pharmacyFuture);
        }

        pharmacyFutures.forEach(CompletableFuture::join);

        return result;
    }

    private List<MedicineOutputModel> fuzzySearch(String searchQuery) {
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);

        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Medicine.class)
                .get();

        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
//                .withPrefixLength(0)
                .onField("name")
                .matching(searchQuery)
                .createQuery();

        FullTextQuery fullTextQuery
                = fullTextEntityManager.createFullTextQuery(query, Medicine.class);

        List<Medicine> resultList = fullTextQuery.getResultList();

        return mapList(resultList, MedicineOutputModel::new);
    }
}
