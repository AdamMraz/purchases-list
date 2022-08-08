package ru.adamdev.purchases.list.configuration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import ru.adamdev.purchases.list.exception.PurchasesListException;

import java.sql.Connection;
import java.sql.SQLException;

import static ru.adamdev.purchases.list.constant.ExceptionConstants.MESSAGE_MIGRATION_ERROR;
import static ru.adamdev.purchases.list.constant.ExceptionConstants.TYPE_ERROR;

public class LiquibaseConfiguration {

    private static final String CHANGE_LOG_FILE = "db/changelog-master.xml";

    public static void init() throws PurchasesListException {
        try {
            initLiquibase();
        } catch (LiquibaseException | SQLException e) {
            throw new PurchasesListException(TYPE_ERROR, MESSAGE_MIGRATION_ERROR);
        }
    }

    public static void initLiquibase() throws LiquibaseException, SQLException {
        Connection connection = JdbcConnection.getConnection();
        Database database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(connection));
        try (Liquibase liquibase = new liquibase.Liquibase(CHANGE_LOG_FILE, new ClassLoaderResourceAccessor(), database)){
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
