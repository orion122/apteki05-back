package ru.apteki05.service;

import org.apache.lucene.search.Query;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.apteki05.model.Medicine;
import ru.apteki05.repository.MedicineRepository;

import javax.persistence.EntityManager;
import java.util.List;

@Component
public class SearchService {

    private final MedicineRepository medicineRepository;
    private final EntityManager entityManager;

    @Autowired
    public SearchService(MedicineRepository medicineRepository, EntityManager entityManager) {
        this.medicineRepository = medicineRepository;
        this.entityManager = entityManager;
    }

    public List<Medicine> search(String word) {
        return medicineRepository.findAllByNameContainingIgnoreCase(word);
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
