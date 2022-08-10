package ru.adamdev.purchases.list.configuration;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.adamdev.purchases.list.exception.PurchasesListException;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_MIGRATION_ERROR;
import static ru.adamdev.purchases.list.constant.ErrorConstants.TYPE_ERROR;

@ExtendWith(MockitoExtension.class)
class LiquibaseConfigurationTest {

    @Mock
    private Connection connection;
    @Mock
    private DatabaseFactory databaseFactory;
    @Mock
    private Database database;

    @Test
    void initOk() throws LiquibaseException, PurchasesListException {
        try (MockedStatic<JdbcConnection> jdbcConnectionMockedStatic = Mockito.mockStatic(JdbcConnection.class);
             MockedStatic<DatabaseFactory> databaseFactoryMockedStatic = Mockito.mockStatic(DatabaseFactory.class);
             MockedConstruction<liquibase.database.jvm.JdbcConnection> jdbcConnectionMockedConstruction
                     = mockConstruction(liquibase.database.jvm.JdbcConnection.class);
             MockedConstruction<Liquibase> liquibaseMockedConstruction = mockConstruction(Liquibase.class)) {
            jdbcConnectionMockedStatic.when(JdbcConnection::getConnection).thenReturn(connection);
            databaseFactoryMockedStatic.when(DatabaseFactory::getInstance).thenReturn(databaseFactory);
            when(databaseFactory.findCorrectDatabaseImplementation(any())).thenReturn(database);
            LiquibaseConfiguration.init();
            List<liquibase.database.jvm.JdbcConnection> jdbcConnectionConstructed = jdbcConnectionMockedConstruction.constructed();
            List<Liquibase> liquibaseConstructed = liquibaseMockedConstruction.constructed();
            assertEquals(1, jdbcConnectionConstructed.size());
            assertEquals(1, liquibaseConstructed.size());
            liquibase.database.jvm.JdbcConnection jdbcConnection = jdbcConnectionConstructed.get(0);
            Liquibase liquibase = liquibaseConstructed.get(0);
            jdbcConnectionMockedStatic.verify(JdbcConnection::getConnection);
            databaseFactoryMockedStatic.verify(DatabaseFactory::getInstance);
            verify(databaseFactory).findCorrectDatabaseImplementation(jdbcConnection);
            verify(liquibase).update(any(Contexts.class), any(LabelExpression.class));
        }
    }

    @Test
    void initException() throws DatabaseException {
        try (MockedStatic<JdbcConnection> jdbcConnectionMockedStatic = Mockito.mockStatic(JdbcConnection.class);
             MockedStatic<DatabaseFactory> databaseFactoryMockedStatic = Mockito.mockStatic(DatabaseFactory.class)) {
            jdbcConnectionMockedStatic.when(JdbcConnection::getConnection).thenReturn(connection);
            databaseFactoryMockedStatic.when(DatabaseFactory::getInstance).thenReturn(databaseFactory);
            when(databaseFactory.findCorrectDatabaseImplementation(any())).thenThrow(DatabaseException.class);
            PurchasesListException purchasesListException = assertThrows(PurchasesListException.class, LiquibaseConfiguration::init);
            assertEquals(TYPE_ERROR, purchasesListException.getType());
            assertEquals(MESSAGE_MIGRATION_ERROR, purchasesListException.getMessage());
        }
    }
}