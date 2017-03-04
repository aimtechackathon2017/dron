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

public class MaterialDao extends AbstractDao {

    public MaterialDao(String serverUrl, int port, String databaseName, String user, String password) {
        super(serverUrl, port, databaseName, user, password);
    }

    public List<Material> getMaterialy() {
        return getMaterialy("SELECT * FROM Materialy");
    }

    private List<Material> getMaterialy(String sqlQuery) {
        List<Material> materialy = new LinkedList<Material>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Material material = new Material();
                material.setId(resultSet.getInt("id"));
                material.setNazev(resultSet.getString("nazev"));
                materialy.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closePreparedStatement(preparedStatement);
        }
        return materialy;
    }

    public static Material getMaterialById(List<Material> materialy, int id) {
        for (Material m : materialy) {
            if (m.getId() == id) {
                return m;
            }
        }
        return null;
    }

    @Override    
    public void save(IPersistableEntry p) {
        if (p instanceof Material) {
            Material m = (Material)p;
            Context.materialDao.commitSQL(
                    "INSERT INTO Materialy (id, nazev) VALUES ("
                    + m.getId() + ", "
                    + m.getNazev() + ")"
            );
        }
    }

}
