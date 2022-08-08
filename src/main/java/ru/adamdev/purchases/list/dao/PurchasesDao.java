package ru.adamdev.purchases.list.dao;

import ru.adamdev.purchases.list.dao.entity.PurchasesEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.time.LocalDate;
import java.util.List;

public class PurchasesDao extends Dao<PurchasesEntity> {

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    private final String WHERE_DATE_BETWEEN = " WHERE " + SIMPLE_TABLE_NAME + "date_of_purchase BETWEEN :"
            + START_DATE + " AND :" + END_DATE;

    public PurchasesDao() throws PurchasesListException {
        super(PurchasesEntity.class);
    }

    public List<PurchasesEntity> findByBetweenDate(LocalDate startDate, LocalDate endDate) throws PurchasesListException {
        return HibernateSessionFactoryUtil
                .wrapSession(session -> session.createNativeQuery(selectAllNativePlusThis(WHERE_DATE_BETWEEN), tClass)
                        .setParameter(START_DATE, startDate)
                        .setParameter(END_DATE, endDate)
                        .list());
    }
}
