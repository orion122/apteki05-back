package ru.apteki05.service;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Medicine;
import ru.apteki05.output.MedicineOutputModel;
import ru.apteki05.repository.MedicineRepository;
import ru.apteki05.service.webparser.AptechniyDomParser;
import ru.apteki05.service.webparser.DagAptekiParser;
import ru.apteki05.service.webparser.DagPharmParser;
import ru.apteki05.service.webparser.WebParser;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.apteki05.utils.Utils.mapList;

@Component
@RequiredArgsConstructor
public class SearchService {

    private final static ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(15);
    private final MedicineRepository medicineRepository;
    private final DagAptekiParser dagAptekiParser;
    private final DagPharmParser dagPharmParser;
    private final AptechniyDomParser aptechniyDomParser;
    private final EntityManager entityManager;

    public List<MedicineOutputModel> search(String word) {
        List<Medicine> medicines = medicineRepository.findAllByNameContainingIgnoreCase(word);

        return mapList(medicines, MedicineOutputModel::new);
    }

    public List<MedicineOutputModel> outsideSearch(String medicineNameFilter) {

        Long maxMedicineId = medicineRepository.findMaxId();
        List<MedicineOutputModel> result = new CopyOnWriteArrayList<>();

        List<WebParser> webParsers = List.of(dagAptekiParser, dagPharmParser, aptechniyDomParser);

        for (int i = 0; i < webParsers.size(); i++) {
            int idOffset = i * 1000;
            WebParser webParser = webParsers.get(i);

            CompletableFuture<List<MedicineOutputModel>> pharmacyFuture =
                    CompletableFuture.supplyAsync(() -> webParser.request(medicineNameFilter, maxMedicineId + idOffset), EXECUTOR_SERVICE);

            pharmacyFuture.thenAccept(result::addAll);
            pharmacyFuture.join();
        }

        return result;
    }

    public List<Medicine> fuzzySearch(String word) {
        //Get the FullTextEntityManager
        FullTextEntityManager fullTextEntityManager
                = Search.getFullTextEntityManager(entityManager);

        //Create a Hibernate Search DSL query builder for the required entity
        QueryBuilder queryBuilder = fullTextEntityManager.getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Medicine.class)
                .get();

        //Generate a Lucene query using the builder
        Query query = queryBuilder
                .keyword()
                .fuzzy()
                .withEditDistanceUpTo(2)
//                .withPrefixLength(0)
                .onField("name")
                .matching(word)
                .createQuery();

        FullTextQuery fullTextQuery
                = fullTextEntityManager.createFullTextQuery(query, Medicine.class);

        //returns JPA managed entities
        return fullTextQuery.getResultList();
    }
}
