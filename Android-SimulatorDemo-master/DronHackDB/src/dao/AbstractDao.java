/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import DronHackContext.Context;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Bedna;
import model.Material;
import model.Pozice;
import model.IPersistableEntry;

//import javax.servlet.http.HttpServlet;
public abstract class AbstractDao<T> {

    private static final long serialVersionUID = 1L;

    private String connectionString;
    /**
     * Pouze pro insert update delete
     * @param sqlQuery 
     */
    void commitSQL(String sqlQuery) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();            
            preparedStatement = connection.prepareStatement(sqlQuery);            
            preparedStatement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closePreparedStatement(preparedStatement);
        }
    }

    public AbstractDao(String serverUrl, int port, String databaseName, String user, String password) {
        connectionString
                = "jdbc:sqlserver://" + serverUrl + ":" + port + ";"
                + "database=" + databaseName + ";"
                + "user=" + user + ";"
                + "password=" + password + ";"
                + "encrypt=true;"
                + "trustServerCertificate=false;"
                + "hostNameInCertificate=*.database.windows.net;"
                + "loginTimeout=30;";
        System.out.println(connectionString);
    }

    protected Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(connectionString);
        return connection;
    }

    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println("Could not close connection");
            }
        }
    }

    protected void closePreparedStatement(PreparedStatement preparedStatement) {
        if (preparedStatement != null) {
            try {
                preparedStatement.close();
            } catch (Exception e) {
                System.out.println("Could not close prepared statement");
            }
        }
    }

   // public abstract void save(IPersistableEntry p);
}
