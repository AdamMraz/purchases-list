package ru.adamdev.purchases.list.configuration;

import ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.PASSWORD;
import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.URL;
import static ru.adamdev.purchases.list.util.HibernateSessionFactoryUtil.USER;

public class JdbcConnection {

    private static final Logger LOGGER = Logger.getLogger(JdbcConnection.class.getName());

    private static Connection connection = null;

    public static Connection getConnection() throws SQLException {
        String url = HibernateSessionFactoryUtil.getConfigurationProperty(URL);
        String user = HibernateSessionFactoryUtil.getConfigurationProperty(USER);
        String password = HibernateSessionFactoryUtil.getConfigurationProperty(PASSWORD);
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Не удалось соеденитьс с базой данных", e);
            }
        }
        return connection;
    }
}
