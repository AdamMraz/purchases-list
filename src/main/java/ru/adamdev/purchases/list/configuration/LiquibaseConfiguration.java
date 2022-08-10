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

import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_CANNOT_CONNECT_DB;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_MIGRATION_ERROR;
import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

public class LiquibaseConfiguration {

    private static final String CHANGE_LOG_FILE = "db/changelog-master.xml";

    public static void init() throws PurchasesListException {
        try {
            initLiquibase();
        } catch (LiquibaseException | SQLException e) {
            throw new PurchasesListException(TYPE_ERROR, MESSAGE_MIGRATION_ERROR, e);
        }
    }

    private static void initLiquibase() throws LiquibaseException, PurchasesListException, SQLException {
        Connection connection = JdbcConnection.getConnection();
        if (connection == null) {
            throw new PurchasesListException(TYPE_ERROR, MESSAGE_MIGRATION_ERROR + " " + MESSAGE_CANNOT_CONNECT_DB);
        }
        Database database =
                DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new liquibase.database.jvm.JdbcConnection(connection));
        try (Liquibase liquibase = new Liquibase(CHANGE_LOG_FILE, new ClassLoaderResourceAccessor(), database)){
            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
