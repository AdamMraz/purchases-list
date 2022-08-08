package ru.adamdev.purchases.list;

import ru.adamdev.purchases.list.configuration.LiquibaseConfiguration;
import ru.adamdev.purchases.list.dao.BuyerDao;
import ru.adamdev.purchases.list.dao.PurchasesDao;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.helper.impl.PurchasesListExceptionWrapperImpl;
import ru.adamdev.purchases.list.mapper.DtoMapper;
import ru.adamdev.purchases.list.mapper.DtoMapperImpl;
import ru.adamdev.purchases.list.model.InputParams;
import ru.adamdev.purchases.list.service.PurchasesListService;
import ru.adamdev.purchases.list.service.impl.PurchasesListServiceImpl;
import ru.adamdev.purchases.list.util.ValidationUtil;

public class Source {

    public static void main(String[] args) throws PurchasesListException {
        LiquibaseConfiguration.init();
        InputParams inputParams = ValidationUtil.validateInputParams(args);
        BuyerDao buyerDao = new BuyerDao();
        PurchasesDao purchasesDao = new PurchasesDao();
        DtoMapper dtoMapper = new DtoMapperImpl();
        PurchasesListService purchasesListService = new PurchasesListServiceImpl(buyerDao, purchasesDao, dtoMapper);
        new PurchasesListExceptionWrapperImpl().wrap(() -> purchasesListService.getResult(inputParams), inputParams.getOutputFile());
    }
}
