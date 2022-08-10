package ru.adamdev.purchases.list;

import ru.adamdev.purchases.list.configuration.LiquibaseConfiguration;
import ru.adamdev.purchases.list.dao.BuyerDao;
import ru.adamdev.purchases.list.dao.PurchasesDao;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.helper.impl.PurchasesListExceptionWrapperImpl;
import ru.adamdev.purchases.list.mapper.DtoMapper;
import ru.adamdev.purchases.list.mapper.DtoMapperImpl;
import ru.adamdev.purchases.list.model.InputParams;
import ru.adamdev.purchases.list.model.results.Result;
import ru.adamdev.purchases.list.service.PurchasesListService;
import ru.adamdev.purchases.list.service.impl.PurchasesListServiceImpl;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;
import ru.adamdev.purchases.list.util.ValidationUtil;

public class Source {

    public static void main(String[] args) {
        new PurchasesListExceptionWrapperImpl().wrap(() -> Source.execute(args));
        HibernateSessionFactoryUtil.close();
    }

    private static Result execute(String[] args) throws PurchasesListException {
        InputParams inputParams = ValidationUtil.validateInputParams(args);
        LiquibaseConfiguration.init();
        BuyerDao buyerDao = new BuyerDao();
        PurchasesDao purchasesDao = new PurchasesDao();
        DtoMapper dtoMapper = new DtoMapperImpl();
        PurchasesListService purchasesListService = new PurchasesListServiceImpl(buyerDao, purchasesDao, dtoMapper);
        return purchasesListService.getResult(inputParams);
    }
}
