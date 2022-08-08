package ru.adamdev.purchases.list.service;

import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.model.InputParams;
import ru.adamdev.purchases.list.model.results.Result;

public interface PurchasesListService {

    Result getResult(InputParams inputParams) throws PurchasesListException;
}
