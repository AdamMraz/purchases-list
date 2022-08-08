package ru.adamdev.purchases.list.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.SessionFactoryObserver;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import ru.adamdev.purchases.list.dao.entity.BuyerEntity;
import ru.adamdev.purchases.list.dao.entity.ProductsEntity;
import ru.adamdev.purchases.list.dao.entity.PurchasesEntity;
import ru.adamdev.purchases.list.exception.PurchasesListException;

import java.util.function.Function;

import static ru.adamdev.purchases.list.constant.ExceptionConstants.MESSAGE_SESSION_FACTORY_ERROR;
import static ru.adamdev.purchases.list.constant.ExceptionConstants.TYPE_ERROR;

public class HibernateSessionFactoryUtil {

    public static final String URL = "hibernate.connection.url";
    public static final String USER = "hibernate.connection.username";
    public static final String PASSWORD = "hibernate.connection.password";

    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;

    public static <R> R wrapSession(Function<Session, R> function) throws PurchasesListException {
        try (SessionFactory sessionFactory = getSessionFactory();
             Session session = sessionFactory.openSession()) {
            return function.apply(session);
        }
    }

    private static SessionFactory getSessionFactory() throws PurchasesListException {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            try {
                Configuration configuration = new Configuration().configure();
                configuration.setSessionFactoryObserver(getSessionFactoryObserver());
                addAnnotatedClass(configuration);
                serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                throw new PurchasesListException(TYPE_ERROR, MESSAGE_SESSION_FACTORY_ERROR);
            }
        }
        return sessionFactory;
    }

    public static String getConfigurationProperty(String propertyName) {
        Configuration configuration = new Configuration().configure();
        return configuration.getProperties().getProperty(propertyName);
    }

    private static SessionFactoryObserver getSessionFactoryObserver() {
        return new SessionFactoryObserver() {
            public void sessionFactoryCreated(SessionFactory factory) {
            }
            public void sessionFactoryClosed(SessionFactory factory) {
                close();
            }
        };
    }

    private static void close() {
        if (serviceRegistry != null) {
            StandardServiceRegistryBuilder.destroy(serviceRegistry);
        }
    }

    private static void addAnnotatedClass(Configuration configuration) {
        configuration.addAnnotatedClass(BuyerEntity.class);
        configuration.addAnnotatedClass(ProductsEntity.class);
        configuration.addAnnotatedClass(PurchasesEntity.class);
    }
}
