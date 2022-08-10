package ru.adamdev.purchases.list.configuration;

import org.apache.log4j.Logger;
import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static ru.adamdev.purchases.list.constant.ErrorConstants.MESSAGE_CANNOT_CONNECT_DB;
import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.PASSWORD;
import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.URL;
import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.USER;

public class JdbcConnection {

    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class);

    private static Connection connection = null;

    public static Connection getConnection() {
        String url = HibernateSessionFactoryUtil.getConfigurationProperty(URL);
        String user = HibernateSessionFactoryUtil.getConfigurationProperty(USER);
        String password = HibernateSessionFactoryUtil.getConfigurationProperty(PASSWORD);
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            LOGGER.error(MESSAGE_CANNOT_CONNECT_DB + " " + e.getMessage());
        }
        return connection;
    }
}
