package ru.adamdev.purchases.list.dao;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.dao.entity.ProductsEntity;
import ru.adamdev.purchases.list.dao.entity.PurchasesEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;

import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

class DaoTest {

    private static final String SELECT_ALL_NATIVE = "SELECT %s.* FROM %s %s";
    private static final String EXCEPTION_MESSAGE = "Класс " + EntityWithOutAnnotationTable.class.getName() + " не имеет аннотации \"@Table\"";
    private static final String SQL = UUID.randomUUID().toString();

    static class DaoImpl<T> extends Dao<T> {
        DaoImpl(Class<T> tClass) throws PurchasesListException {
            super(tClass);
        }
    }

    static class EntityWithOutAnnotationTable {
    }

    @ParameterizedTest
    @MethodSource("constructorTestParam")
    <T> void constructorTestOk(Pair<Class<T>, String> arg) throws PurchasesListException {
        Class<T> aClass = arg.getKey();
        String expectedTableName = arg.getValue();
        String expectedSimpleTableName = expectedTableName.substring(0, expectedTableName.length() / 2);
        Dao<T> dao = new DaoImpl<>(aClass);
        assertEquals(aClass, dao.tClass);
        assertEquals(expectedTableName, dao.TABLE_NAME);
        assertEquals(expectedSimpleTableName + ".", dao.SIMPLE_TABLE_NAME_WITH_POINT);
        assertEquals(prepareSelectAllNative(expectedTableName, expectedSimpleTableName), dao.SELECT_ALL_NATIVE);
    }

    @Test
    void constructorTestException() {
        Class<EntityWithOutAnnotationTable> aClass = EntityWithOutAnnotationTable.class;
        PurchasesListException exception = assertThrows(PurchasesListException.class, () -> new DaoImpl<>(aClass));
        assertEquals(TYPE_ERROR, exception.getType());
        assertEquals(EXCEPTION_MESSAGE, exception.getMessage());
    }

    @Test
    void selectAllNativePlusThisOk() throws PurchasesListException {
        Class<BuyerEntity> aClass = BuyerEntity.class;
        Dao<BuyerEntity> dao = new DaoImpl<>(aClass);
        assertEquals(dao.SELECT_ALL_NATIVE + SQL, dao.selectAllNativePlusThis(SQL));
    }

    private String prepareSelectAllNative(String tableName, String simpleTableName) {
        return String.format(SELECT_ALL_NATIVE, simpleTableName, tableName, simpleTableName);
    }

    private static Stream<Pair<Class<?>, String>> constructorTestParam() {
        return Stream.of(
                Pair.of(BuyerEntity.class, "buyer"),
                Pair.of(ProductsEntity.class, "products"),
                Pair.of(PurchasesEntity.class, "purchases")
        );
    }
}