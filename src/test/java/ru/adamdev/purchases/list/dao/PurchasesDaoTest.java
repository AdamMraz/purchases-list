package ru.adamdev.purchases.list.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adamdev.purchases.list.dao.entity.PurchasesEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class PurchasesDaoTest {

    private static final Class<PurchasesEntity> T_CLASS = PurchasesEntity.class;
    private static final PodamFactory PODAM_FACTORY = new PodamFactoryImpl();
    private static final List<PurchasesEntity> purchasesEntityList;
    private static final LocalDate START_DATE_VALUE = LocalDate.of(2019, 1, 1);
    private static final LocalDate END_DATE_VALUE = LocalDate.now();

    private static final String START_DATE = "startDate";
    private static final String END_DATE = "endDate";

    private static final String WHERE_DATE_BETWEEN = "SELECT purc.* FROM purchases purc WHERE purc.date_of_purchase BETWEEN :startDate AND :endDate";

    @Spy
    private PurchasesDao dao;

    static {
        List<PurchasesEntity> purchasesEntityTempList = new ArrayList<>();
        purchasesEntityTempList.add(PODAM_FACTORY.manufacturePojo(PurchasesEntity.class));
        purchasesEntityTempList.add(PODAM_FACTORY.manufacturePojo(PurchasesEntity.class));
        purchasesEntityList = Collections.unmodifiableList(purchasesEntityTempList);
    }

    @Test
    void findByBetweenDate() throws PurchasesListException {
        DaoTester.tester(T_CLASS)
                .method(() -> dao.findByBetweenDate(START_DATE_VALUE, END_DATE_VALUE))
                .sql(WHERE_DATE_BETWEEN)
                .param(START_DATE, START_DATE_VALUE)
                .param(END_DATE, END_DATE_VALUE)
                .resultList(purchasesEntityList)
                .test();
    }
}