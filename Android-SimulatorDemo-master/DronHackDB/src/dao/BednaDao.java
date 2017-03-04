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
import model.*;

/**
 *
 * @author Jirka
 */
public class BednaDao extends AbstractDao {

    public BednaDao(String serverUrl, int port, String databaseName, String user, String password) {
        super(serverUrl, port, databaseName, user, password);
    }

    public List<Bedna> getBedny() {
        return getBedny("SELECT * FROM Bedny LEFT JOIN Pozice ON Bedny.poziceID = Pozice.id");
    }

    private List<Bedna> getBedny(String sqlQuery) {
        List<Bedna> bedny = new LinkedList<>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Material> materialy = Context.materialDao.getMaterialy();

            while (resultSet.next()) {
                Bedna bedna = new Bedna();
                bedna.setBednaId(resultSet.getInt("id"));
                Pozice pozice = new Pozice(
                        resultSet.getInt("XY"),
                        resultSet.getFloat("x"),
                        resultSet.getFloat("y"),
                        resultSet.getFloat("z"),
                        resultSet.getFloat("az")
                );
                bedna.setPozice(pozice);
                Material material = MaterialDao.getMaterialById(materialy, resultSet.getInt("materialId"));
                if (material != null) {
                    bedna.setMaterial(material);
                } else {
                    System.err.println("Bedna nemá materiál " + bedna.toString());
                }
                bedny.add(bedna);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closePreparedStatement(preparedStatement);
        }
        return bedny;
    }

    public List<Bedna> getBednyByMaterial(Material material) {
        return getBedny("SELECT * FROM Materialy WHERE Materialy.id = " + material.getId());
    }

    @Override
    public void save(IPersistableEntry p) {
        if (p instanceof Bedna) {
            Bedna b = (Bedna) p;
            Context.bednaDao.commitSQL(
                    "INSERT INTO Bedny (id, poziceID, materialID, mnozstvi) VALUES ("
                    + b.getBednaId() + ", "
                    + b.getPozice().getPoziceId() + ", "
                    + b.getMaterial().getId() + ", "
                    + " 1)"
            );
        }
    }

}
