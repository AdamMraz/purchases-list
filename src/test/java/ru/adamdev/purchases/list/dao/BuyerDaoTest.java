package ru.adamdev.purchases.list.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
class BuyerDaoTest {

    private static final Class<BuyerEntity> T_CLASS = BuyerEntity.class;
    private static final Random RANDOM = new Random();
    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();
    private static final List<BuyerEntity> buyerEntityList;
    private static final String SECOND_NAME_VALUE = UUID.randomUUID().toString();
    private static final String PRODUCT_NAME_VALUE = UUID.randomUUID().toString();
    private static final Integer MIN_COUNT_VALUE = RANDOM.nextInt();
    private static final BigDecimal MIN_SUM_VALUE = BigDecimal.valueOf(RANDOM.nextDouble());
    private static final BigDecimal MAX_SUM_VALUE = BigDecimal.valueOf(RANDOM.nextDouble());
    private static final Integer LIMIT_VALUE = RANDOM.nextInt();

    private static final String SECOND_NAME = "secondName";
    private static final String PRODUCT_NAME = "name";
    private static final String MIN_COUNT = "count";
    private static final String MIN_SUM = "minSum";
    private static final String MAX_SUM = "maxSum";
    private static final String LIMIT = "limit";

    private static final String WHERE_SECOND_NAME = "SELECT bu.* FROM buyer bu WHERE SECOND_NAME = :secondName";
    private static final String WHERE_PRODUCT_NAME_AND_COUNT = "SELECT bu.* FROM buyer bu WHERE (SELECT COUNT(*) FROM Purchases pu" +
            " WHERE pu.buyer_id = bu.id AND pu.product_id = (SELECT pr.id FROM products pr WHERE pr.name = :name)) >= :count";
    private static final String WHERE_SUM_BETWEEN = "SELECT bu.* FROM buyer bu WHERE (SELECT COALESCE(SUM(pr.price), 0) FROM products pr" +
            " WHERE pr.id IN (SELECT pu.product_id FROM purchases pu WHERE pu.buyer_id = bu.id)) BETWEEN :minSum AND :maxSum";
    private static final String ORDER_BY_PURCHASES_WITH_LIMIT = "SELECT bu.* FROM buyer bu ORDER BY (SELECT COUNT(*) FROM purchases pu" +
            " WHERE bu.id = pu.buyer_id) LIMIT :limit";

    @Spy
    private BuyerDao dao;

    static {
        List<BuyerEntity> buyerEntityTempList = new ArrayList<>();
        buyerEntityTempList.add(PODAM_FACTORY.manufacturePojo(BuyerEntity.class));
        buyerEntityTempList.add(PODAM_FACTORY.manufacturePojo(BuyerEntity.class));
        buyerEntityList = Collections.unmodifiableList(buyerEntityTempList);
    }

    @Test
    void findBySecondName() throws PurchasesListException {
        DaoTester.tester(T_CLASS)
                .method(() -> dao.findBySecondName(SECOND_NAME_VALUE))
                .sql(WHERE_SECOND_NAME)
                .param(SECOND_NAME, SECOND_NAME_VALUE)
                .resultList(buyerEntityList)
                .test();
    }

    @Test
    void findByProductNameAndCount() throws PurchasesListException {
        DaoTester.tester(T_CLASS)
                .method(() -> dao.findByProductNameAndCount(PRODUCT_NAME_VALUE, MIN_COUNT_VALUE))
                .sql(WHERE_PRODUCT_NAME_AND_COUNT)
                .param(PRODUCT_NAME, PRODUCT_NAME_VALUE)
                .param(MIN_COUNT, MIN_COUNT_VALUE)
                .resultList(buyerEntityList)
                .test();
    }

    @Test
    void findByBetweenSum() throws PurchasesListException {
        DaoTester.tester(T_CLASS)
                .method(() -> dao.findByBetweenSum(MIN_SUM_VALUE, MAX_SUM_VALUE))
                .sql(WHERE_SUM_BETWEEN)
                .param(MIN_SUM, MIN_SUM_VALUE)
                .param(MAX_SUM, MAX_SUM_VALUE)
                .resultList(buyerEntityList)
                .test();
    }

    @Test
    void findOrderByPurchasesWithLimit() throws PurchasesListException {
        DaoTester.tester(T_CLASS)
                .method(() -> dao.findOrderByPurchasesWithLimit(LIMIT_VALUE))
                .sql(ORDER_BY_PURCHASES_WITH_LIMIT)
                .param(LIMIT, LIMIT_VALUE)
                .resultList(buyerEntityList)
                .test();
    }
}