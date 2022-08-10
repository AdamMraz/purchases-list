package ru.adamdev.purchases.list.dao;

import ru.adamdev.purchases.list.exception.PurchasesListException;

import javax.persistence.Table;
import java.util.Optional;

import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

public abstract class Dao <T> {

    protected final Class<T> tClass;
    protected final String SELECT_ALL_NATIVE;
    protected final String SIMPLE_TABLE_NAME_WITH_POINT;
    protected final String TABLE_NAME;

    Dao(Class<T> tClass) throws PurchasesListException {
        String tableName = Optional.ofNullable(tClass.getAnnotation(Table.class))
                .map(Table::name)
                .orElseThrow(() -> new PurchasesListException(TYPE_ERROR, "Класс " + tClass.getName() + " не имеет аннотации \"@Table\""));
        String substringTableName = tableName.substring(0, tableName.length() / 2);
        this.TABLE_NAME = tableName;
        this.SIMPLE_TABLE_NAME_WITH_POINT = substringTableName + ".";
        this.SELECT_ALL_NATIVE = "SELECT " + SIMPLE_TABLE_NAME_WITH_POINT + "* FROM " + TABLE_NAME + " " + substringTableName;
        this.tClass = tClass;
    }
    
    protected String selectAllNativePlusThis(String sql) {
        return SELECT_ALL_NATIVE + sql;
    }
}
