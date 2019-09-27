package com.chriniko.lunatech.movies.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import java.util.List;


@Component
public class DataInjector {

    private final EntityManager entityManager;
    private final TransactionTemplate transactionTemplate;

    @Autowired
    public DataInjector(EntityManager entityManager, TransactionTemplate transactionTemplate) {
        this.entityManager = entityManager;
        this.transactionTemplate = transactionTemplate;
    }

    <T> void perform(List<T> batchedData) {

        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                for (T datum : batchedData) {
                    entityManager.persist(datum);
                }
            }
        });

    }
}
