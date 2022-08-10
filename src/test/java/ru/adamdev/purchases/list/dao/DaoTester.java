package ru.adamdev.purchases.list.dao;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adamdev.purchases.list.exception.PurchasesListException;
import ru.adamdev.purchases.list.function.ThrowableSupplier;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class DaoTester<T> {

    private final Class<T> tClass;
    private String sql;
    private List<Pair> params;
    private ThrowableSupplier<List<T>, PurchasesListException> method;
    private List<T> resultList;

    public static <T> DaoTester<T> tester(Class<T> tClass) {
        return new DaoTester<>(tClass);
    }

    @AllArgsConstructor
    static class Pair {
        String name;
        Object value;
    }

    public DaoTester<T> sql(String sql) {
        this.sql = sql;
        return this;
    }

    public DaoTester<T> param(String name, Object value) {
        if (this.params == null) {
            this.params = new ArrayList<>();
        }
        this.params.add(new Pair(name, value));
        return this;
    }

    public DaoTester<T> method(ThrowableSupplier<List<T>, PurchasesListException> method) {
        this.method = method;
        return this;
    }

    public DaoTester<T> resultList(List<T> resultList) {
        this.resultList = resultList;
        return this;
    }

    public void test() throws PurchasesListException {
        try (MockedStatic<HibernateSessionFactoryUtil> hibernateSessionFactoryUtilMockedStatic
                     = Mockito.mockStatic(HibernateSessionFactoryUtil.class)) {
            hibernateSessionFactoryUtilMockedStatic
                    .when(() -> HibernateSessionFactoryUtil.wrapSession(any()))
                    .then((arg) -> {
                        Function<Session, List<T>> argument = arg.getArgument(0);
                        return wrapSession(argument);
                    });
            List<T> result = method.get();
            assertEquals(resultList, result);
            hibernateSessionFactoryUtilMockedStatic.verify(() -> HibernateSessionFactoryUtil.wrapSession(any()));
        }
    }

    private List<T> wrapSession(Function<Session, List<T>> function) {
        Session session = Mockito.mock(Session.class);
        NativeQuery<T> nativeQuery = Mockito.mock(NativeQuery.class);
        when(session.createNativeQuery(anyString(), any(Class.class))).thenReturn(nativeQuery);
        when(nativeQuery.setParameter(anyString(), any())).thenReturn(nativeQuery);
        when(nativeQuery.list()).thenReturn(resultList);
        List<T> result = function.apply(session);
        Mockito.verify(session).createNativeQuery(sql, tClass);
        params.forEach(pair -> Mockito.verify(nativeQuery).setParameter(pair.name, pair.value));
        return result;
    }
}
