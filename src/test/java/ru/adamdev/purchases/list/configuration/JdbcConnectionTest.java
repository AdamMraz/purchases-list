package ru.adamdev.purchases.list.configuration;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_CANNOT_CONNECT_DB;

@ExtendWith(MockitoExtension.class)
class JdbcConnectionTest {

    private static final String PASSWORD_VALUE = UUID.randomUUID().toString();
    private static final String URL_VALUE = UUID.randomUUID().toString();
    private static final String USER_VALUE = UUID.randomUUID().toString();
    private static final String SQL_EXCEPTION_MESSAGE = UUID.randomUUID().toString();

    @Mock
    private Connection connection;
    @Mock
    private Appender mockAppender;

    @BeforeEach
    void initBeforeEach() throws NoSuchFieldException, IllegalAccessException {
        Field connectionField = JdbcConnection.class.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionField.set(null, null);
    }

    @Test
    void getConnectionOk() {
        try (MockedStatic<HibernateSessionFactoryUtil> hibernateMockedStatic = Mockito.mockStatic(HibernateSessionFactoryUtil.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            mockHibernateSessionFactoryUtil(hibernateMockedStatic);
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL_VALUE, USER_VALUE, PASSWORD_VALUE))
                    .thenReturn(connection);
            assertEquals(connection, JdbcConnection.getConnection());
            verifyHibernateSessionFactoryUtil(hibernateMockedStatic);
            driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL_VALUE, USER_VALUE, PASSWORD_VALUE));
        }
    }

    @Test
    void getConnectionException() {
        try (MockedStatic<HibernateSessionFactoryUtil> hibernateMockedStatic = Mockito.mockStatic(HibernateSessionFactoryUtil.class);
             MockedStatic<DriverManager> driverManagerMockedStatic = Mockito.mockStatic(DriverManager.class)) {
            Logger logger = Logger.getLogger(JdbcConnection.class);
            logger.addAppender(mockAppender);
            mockHibernateSessionFactoryUtil(hibernateMockedStatic);
            driverManagerMockedStatic.when(() -> DriverManager.getConnection(URL_VALUE, USER_VALUE, PASSWORD_VALUE))
                    .then((Answer<Connection>) invocationOnMock -> {
                        throw new SQLException(SQL_EXCEPTION_MESSAGE);
                    });
            JdbcConnection.getConnection();
            verifyHibernateSessionFactoryUtil(hibernateMockedStatic);
            driverManagerMockedStatic.verify(() -> DriverManager.getConnection(URL_VALUE, USER_VALUE, PASSWORD_VALUE));
            ArgumentCaptor<LoggingEvent> eventArgumentCaptor = ArgumentCaptor.forClass(LoggingEvent.class);
            verify(mockAppender).doAppend(eventArgumentCaptor.capture());
            String logMessage = String.valueOf(eventArgumentCaptor.getAllValues().get(0).getMessage());
            assertNotNull(logMessage);
            assertEquals(MESSAGE_CANNOT_CONNECT_DB + " " + SQL_EXCEPTION_MESSAGE, logMessage);
        }
    }

    private void mockHibernateSessionFactoryUtil(MockedStatic<HibernateSessionFactoryUtil> hibernateMockedStatic) {
        hibernateMockedStatic.when(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.URL))
                .thenReturn(URL_VALUE);
        hibernateMockedStatic.when(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.USER))
                .thenReturn(USER_VALUE);
        hibernateMockedStatic.when(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.PASSWORD))
                .thenReturn(PASSWORD_VALUE);
    }

    private void verifyHibernateSessionFactoryUtil(MockedStatic<HibernateSessionFactoryUtil> hibernateMockedStatic) {
        hibernateMockedStatic.verify(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.URL));
        hibernateMockedStatic.verify(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.USER));
        hibernateMockedStatic.verify(() -> HibernateSessionFactoryUtil.getConfigurationProperty(HibernateSessionFactoryUtil.PASSWORD));
    }
}