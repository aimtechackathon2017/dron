/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import DronHackContext.Context;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import model.IPersistableEntry;
import model.Material;
import model.Pozice;

/**
 *
 * @author Jirka
 */
public class PoziceDao extends AbstractDao {

    public PoziceDao(String serverUrl, int port, String databaseName, String user, String password) {
        super(serverUrl, port, databaseName, user, password);
    }

    public List<Pozice> getPozice() {
        return getPozice("SELECT * FROM Pozice");
    }

    private List<Pozice> getPozice(String sqlQuery) {
        List<Pozice> pozices = new LinkedList<Pozice>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Pozice pozice = new Pozice();
                pozice.setPoziceId(resultSet.getInt("id"));
                pozice.setXY(resultSet.getInt("XY"));
                pozice.setX(resultSet.getFloat("x"));
                pozice.setY(resultSet.getFloat("y"));
                pozice.setZ(resultSet.getFloat("z"));
                pozice.setAz(resultSet.getFloat("az"));
                pozices.add(pozice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closePreparedStatement(preparedStatement);
        }
        return pozices;
    }

    public void save(Pozice poz) {
        commitSQL(
                "INSERT INTO Pozice (XY, x, y, z) VALUES ("
                + poz.getXY() + ", "
                + poz.getX() + ", "
                + poz.getY() + ", "
                + poz.getZ() + ")"
        );
    }

}
