package ru.somecompany.loadmodule.auth.repository;


import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.somecompany.loadmodule.auth.models.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


@Repository
@Transactional
public class UserSearchRepository {

    private String[] fields = {"username"};

    // Spring will inject here the entity manager object
    @PersistenceContext
    private EntityManager entityManager;

    public int getResultSize(String text){
        // get the full text entity manager
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.
                        getFullTextEntityManager(entityManager);

        // create the query using Hibernate Search query DSL
        QueryBuilder queryBuilder =
                fullTextEntityManager.getSearchFactory()
                        .buildQueryBuilder().forEntity(User.class).get();

        // a very basic query by keywords
        org.apache.lucene.search.Query query =
                queryBuilder
                        .keyword()
                        .onFields(fields)
                        .matching(text)
                        .createQuery();

        // wrap Lucene query in an Hibernate Query object
        org.hibernate.search.jpa.FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, User.class);
        return jpaQuery.getResultSize();

    }

    public List<User> search(String text, int startAt, int count) {

        // get the full text entity manager
        FullTextEntityManager fullTextEntityManager =
                org.hibernate.search.jpa.Search.
                        getFullTextEntityManager(entityManager);

        // create the query using Hibernate Search query DSL
        QueryBuilder queryBuilder =
                fullTextEntityManager.getSearchFactory()
                        .buildQueryBuilder().forEntity(User.class).get();

        // a very basic query by keywords
        org.apache.lucene.search.Query query =
                queryBuilder
                        .keyword()
                        .onFields(fields)
                        .matching(text)
                        .createQuery();

        // wrap Lucene query in an Hibernate Query object
        org.hibernate.search.jpa.FullTextQuery jpaQuery = fullTextEntityManager.createFullTextQuery(query, User.class);
        jpaQuery.setFirstResult(startAt);
        jpaQuery.setMaxResults(count);

        // execute search and return results (sorted by relevance as default)
        @SuppressWarnings("unchecked")
        List<User> results = jpaQuery.getResultList();
        return results;
    }
}
