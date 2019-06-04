package com.granado.java.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

/**
 * @Author: Granado
 * @Date: 2019-05-31 09:36
 */
public class SQLUtils {

    public static boolean executeUpdate(String sql, DataSource dataSource) {
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            if (statement.executeUpdate(sql) == 0) {
                //connection.rollback();
                return false;
            } else {
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e.getCause());
        }
    }

    public static String constructSQLByOneKeyValue(String sql, String key, Object value) {
        return constructSQL(sql, Map.of(key, value));
    }

    public static String constructSQL(String sql, Map<String, Object> values) {
        if (sql == null || sql.length() == 0
          || values == null || values.isEmpty()) {
            return sql;
        }

        TokenHandler handler = token -> {
            if (!values.containsKey(token)) {
                throw new RuntimeException("#{" + token + "}" + " not bind value");
            } else {
                return "'" + values.get(token) + "'";
            }
        };

        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", handler);
        return tokenParser.parse(sql);
    }

    public static DataSource createDatasource(String url, String user, String password) {
        try {
            ComboPooledDataSource dataSource = new ComboPooledDataSource();
            dataSource.setJdbcUrl(url);
            dataSource.setUser(user);
            dataSource.setPassword(password);
            dataSource.setInitialPoolSize(20);
            dataSource.setMaxPoolSize(100);
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
            return dataSource;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
