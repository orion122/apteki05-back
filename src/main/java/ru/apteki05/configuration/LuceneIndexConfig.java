package ru.apteki05.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.apteki05.lucene.LuceneIndexServiceBean;

import javax.persistence.EntityManagerFactory;

@Configuration
public class LuceneIndexConfig {

    @Bean
    public LuceneIndexServiceBean luceneIndexServiceBean(EntityManagerFactory EntityManagerFactory) {
        LuceneIndexServiceBean luceneIndexServiceBean = new LuceneIndexServiceBean(EntityManagerFactory);
        luceneIndexServiceBean.triggerIndexing();
        return luceneIndexServiceBean;
    }

}
