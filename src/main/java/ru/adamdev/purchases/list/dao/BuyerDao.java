package ru.adamdev.purchases.list.dao;

import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.math.BigDecimal;
import java.util.List;

public class BuyerDao extends Dao<BuyerEntity> {

    private static final String FIRST_NAME = "firstName";
    private static final String NAME = "name";
    private static final String COUNT = "count";
    private static final String MIN_SUM = "minSum";
    private static final String MAX_SUM = "maxSum";
    private static final String LIMIT = "limit";

    private final String WHERE_FIRST_NAME = " WHERE FIRST_NAME = :" + FIRST_NAME;
    private final String WHERE_PRODUCT_NAME_AND_COUNT = " WHERE (SELECT COUNT(*) FROM Purchases pu" +
            " WHERE pu.buyer_id = " + SIMPLE_TABLE_NAME + "id AND pu.product_id =" +
            " (SELECT pr.id FROM products pr WHERE pr.name = :" + NAME + ")) >= :" + COUNT;
    private final String WHERE_SUM_BETWEEN = " WHERE" +
            " (SELECT COALESCE(SUM(pr.price), 0) FROM products pr WHERE pr.id IN" +
            " (SELECT pu.product_id FROM purchases pu" +
            " WHERE pu.buyer_id = " + SIMPLE_TABLE_NAME + "id)) BETWEEN :" + MIN_SUM + " AND :" + MAX_SUM;
    private final String ORDER_BY_PURCHASES_WITH_LIMIT = " ORDER BY" +
            " (SELECT COUNT(*) FROM purchases pu WHERE " + SIMPLE_TABLE_NAME + "id = pu.buyer_id) LIMIT :" + LIMIT;

    public BuyerDao() throws PurchasesListException {
        super(BuyerEntity.class);
    }

    public List<BuyerEntity> findByFirstName(String firstName) throws PurchasesListException {
        return HibernateSessionFactoryUtil
                .wrapSession(session -> session.createNativeQuery(selectAllNativePlusThis(WHERE_FIRST_NAME), tClass)
                        .setParameter(FIRST_NAME, firstName)
                        .list());
    }

    public List<BuyerEntity> findByProductNameAndCount(String productName, Integer minCount) throws PurchasesListException {
        return HibernateSessionFactoryUtil
                .wrapSession(session -> session.createNativeQuery(selectAllNativePlusThis(WHERE_PRODUCT_NAME_AND_COUNT), tClass)
                        .setParameter(NAME, productName)
                        .setParameter(COUNT, minCount)
                        .list());
    }

    public List<BuyerEntity> findByBetweenSum(BigDecimal minSum, BigDecimal maxSum) throws PurchasesListException {
        return HibernateSessionFactoryUtil
                .wrapSession(session -> session.createNativeQuery(selectAllNativePlusThis(WHERE_SUM_BETWEEN), tClass)
                        .setParameter(MIN_SUM, minSum)
                        .setParameter(MAX_SUM, maxSum)
                        .list());
    }

    public List<BuyerEntity> findOrderByPurchasesWithLimit(Integer limit) throws PurchasesListException {
        return HibernateSessionFactoryUtil
                .wrapSession(session -> session.createNativeQuery(selectAllNativePlusThis(ORDER_BY_PURCHASES_WITH_LIMIT), tClass)
                        .setParameter(LIMIT, limit)
                        .list());
    }
}
