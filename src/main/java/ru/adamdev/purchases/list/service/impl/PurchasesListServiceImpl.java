package ru.adamdev.purchases.list.service.impl;

import lombok.RequiredArgsConstructor;
import ru.adamdev.purchases.list.constant.MethodType;
import ru.adamdev.purchases.list.dao.BuyerDao;
import ru.adamdev.purchases.list.dao.PurchasesDao;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.dao.entity.ProductsEntity;
import ru.adamdev.purchases.list.dao.entity.PurchasesEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.function.ThrowableFunction;
import ru.adamdev.purchases.list.mapper.DtoMapper;
import ru.adamdev.purchases.list.model.InputParams;
import ru.adamdev.purchases.list.model.criterias.Criteria;
import ru.adamdev.purchases.list.model.criterias.InputModel;
import ru.adamdev.purchases.list.model.results.Customers;
import ru.adamdev.purchases.list.model.results.Purchase;
import ru.adamdev.purchases.list.model.results.Result;
import ru.adamdev.purchases.list.model.results.Results;
import ru.adamdev.purchases.list.service.PurchasesListService;
import ru.adamdev.purchases.list.util.FileUtil;
import ru.adamdev.purchases.list.util.JsonUtil;
import ru.adamdev.purchases.list.util.ValidationUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.adamdev.purchases.list.constant.ExceptionConstants.MESSAGE_CANNOT_READ_FILE;
import static ru.adamdev.purchases.list.constant.ExceptionConstants.TYPE_ERROR;
import static ru.adamdev.purchases.list.constant.MethodType.SEARCH;
import static ru.adamdev.purchases.list.constant.MethodType.STAT;

@RequiredArgsConstructor
public class PurchasesListServiceImpl implements PurchasesListService {

    private Map<MethodType, ThrowableFunction<InputModel, Result, ? extends PurchasesListException>> methodMap;

    private final BuyerDao buyerDao;
    private final PurchasesDao purchasesDao;
    private final DtoMapper dtoMapper;

    {
        Map<MethodType, ThrowableFunction<InputModel, Result, ? extends PurchasesListException>> methodMap = new HashMap<>();
        methodMap.put(SEARCH, this::getResultSearchMethod);
        methodMap.put(STAT, this::getResultStatMethod);
        this.methodMap = Collections.unmodifiableMap(methodMap);
    }

    @Override
    public Result getResult(InputParams inputParams) throws PurchasesListException {
        InputModel inputModel = getCriterias(inputParams);
        MethodType methodType = inputParams.getMethodType();
        Result result = this.methodMap.get(methodType).apply(inputModel);
        result.setType(methodType.name().toLowerCase());
        return result;
    }

    private InputModel getCriterias(InputParams inputParams) throws PurchasesListException {
        String json = FileUtil.readFile(inputParams.getInputFile());
        return Optional.ofNullable(JsonUtil.deserialize(json, InputModel.class))
                .orElseThrow(() -> new PurchasesListException(TYPE_ERROR, MESSAGE_CANNOT_READ_FILE));
    }

    private Result getResultStatMethod(InputModel inputModel) throws PurchasesListException {
        LocalDate startDate = inputModel.getStartDate();
        LocalDate endDate = inputModel.getEndDate();
        List<PurchasesEntity> purchasesEntities = findByBetweenDate(startDate, endDate);
        Result result = new Result();
        List<Customers> customersList = new ArrayList<>();
        for (PurchasesEntity purchasesEntity : purchasesEntities) {
            BuyerEntity buyerEntity = purchasesEntity.getBuyer();
            String customerName = buyerEntity.getFirstName() + " " + buyerEntity.getSecondName();
            Customers customers = prepareCustomers(customersList, customerName);
            List<Purchase> purchaseList = customers.getPurchases();
            ProductsEntity productsEntity = purchasesEntity.getProduct();
            Purchase purchase = preparePurchase(purchaseList, productsEntity.getName());
            BigDecimal price = productsEntity.getPrice();
            purchase.setExpenses(purchase.getExpenses().add(price));
            customers.setTotalExpenses(customers.getTotalExpenses().add(price));
        }
        result.setCustomers(customersList);
        result.setTotalDays(Duration.between(startDate.atStartOfDay(), endDate.atStartOfDay()).toDays());
        sortCustomersList(customersList);
        setResultExpenses(result);
        return result;
    }

    private void sortCustomersList(List<Customers> customersList) {
        customersList.sort((c1, c2) -> c2.getTotalExpenses().compareTo(c1.getTotalExpenses()));
        customersList.forEach(customers -> customers.getPurchases().sort((p1, p2) -> p2.getExpenses().compareTo(p1.getExpenses())));
    }

    private void setResultExpenses(Result result) {
        result.setTotalExpenses(new BigDecimal(0));
        long totalExpensesCount = result.getCustomers().stream()
                .peek(customers -> result.setTotalExpenses(result.getTotalExpenses().add(customers.getTotalExpenses())))
                .count();
        result.setAvgExpenses(result.getTotalExpenses().divide(new BigDecimal(totalExpensesCount), RoundingMode.HALF_UP));
    }

    private Customers prepareCustomers(List<Customers> customersList, String name) {
        if (customersList.stream().noneMatch(customers -> name.equals(customers.getName()))) {
            Customers customers = new Customers(name);
            customersList.add(customers);
            return customers;
        }
        return customersList.stream().filter(customers -> name.equals(customers.getName())).findFirst().orElse(null);
    }

    private Purchase preparePurchase(List<Purchase> purchaseList, String name) {
        if (purchaseList.stream().noneMatch(purchase -> name.equals(purchase.getName()))) {
            Purchase purchase = new Purchase(name);
            purchaseList.add(purchase);
            return purchase;
        }
        return purchaseList.stream().filter(customers -> name.equals(customers.getName())).findFirst().orElse(null);
    }

    private Result getResultSearchMethod(InputModel inputModel) throws PurchasesListException {
        List<Results> buyerList = new ArrayList<>();
        for (Criteria criteria : inputModel.getCriterias()) {
            String lastName = criteria.getLastName();
            String productName = criteria.getProductName();
            Integer minTime = criteria.getMinTimes();
            BigDecimal minExpenses = criteria.getMinExpenses();
            BigDecimal maxExpenses = criteria.getMaxExpenses();
            Integer badCustomers = criteria.getBadCustomers();
            List<BuyerEntity> buyerEntityList = new ArrayList<>();
            if (!ValidationUtil.isNullOrEmpty(lastName)) {
                buyerEntityList.addAll(findByFirstName(lastName));
            } else if (!ValidationUtil.isNullOrEmpty(productName, minTime)) {
                buyerEntityList.addAll(findByProductNameAndCount(productName, minTime));
            } else if (!ValidationUtil.isNullOrEmpty(minExpenses, maxExpenses)) {
                buyerEntityList.addAll(findByBetweenSum(minExpenses, maxExpenses));
            } else if (!ValidationUtil.isNullOrEmpty(badCustomers)) {
                buyerEntityList.addAll(findOrderByPurchasesWithLimit(badCustomers));
            }
            buyerList.add(new Results(criteria, buyerEntityList.stream().map(dtoMapper::getBuyerDto).collect(Collectors.toList())));
        }
        return new Result(buyerList);
    }

    private List<BuyerEntity> findByFirstName(String firstName) throws PurchasesListException {
        return buyerDao.findByFirstName(firstName);
    }

    private List<BuyerEntity> findByProductNameAndCount(String productName, Integer minCount) throws PurchasesListException {
        return buyerDao.findByProductNameAndCount(productName, minCount);
    }

    private List<BuyerEntity> findByBetweenSum(BigDecimal minSum, BigDecimal maxSum) throws PurchasesListException {
        return buyerDao.findByBetweenSum(minSum, maxSum);
    }

    private List<BuyerEntity> findOrderByPurchasesWithLimit(Integer limit) throws PurchasesListException {
        return buyerDao.findOrderByPurchasesWithLimit(limit);
    }

    private List<PurchasesEntity> findByBetweenDate(LocalDate startDate, LocalDate endDate) throws PurchasesListException {
        return purchasesDao.findByBetweenDate(startDate, endDate);
    }
}
