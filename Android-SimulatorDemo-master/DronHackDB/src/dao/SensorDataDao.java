/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import model.SensorData;

/**
 *
 * @author Jirka
 */
public class SensorDataDao extends AbstractDao {

    public SensorDataDao(String serverUrl, int port, String databaseName, String user, String password) {
        super(serverUrl, port, databaseName, user, password);
    }

    public List<SensorData> getSensorData() {
        return getSensorData("SELECT * FROM SensorData");
    }

    private List<SensorData> getSensorData(String sqlQuery) {
        List<SensorData> sDs = new LinkedList<SensorData>();

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(sqlQuery);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                SensorData sd = new SensorData();
                sd.setId(resultSet.getInt("id"));
                sd.setTimestamp(resultSet.getLong("timestamp"));
                sd.setxCoordinate(resultSet.getFloat("xcoordinate"));
                sd.setyCoordinate(resultSet.getFloat("ycoordinate"));
                sd.setzCoordinate(resultSet.getFloat("zcoordinate"));
                sd.setxVelocity(resultSet.getFloat("xvelocity"));
                sd.setyVelocity(resultSet.getFloat("yvelocity"));
                sd.setzVelocity(resultSet.getFloat("zvelocity"));
                sd.setxAcceleration(resultSet.getFloat("xacceleration"));
                sd.setyAcceleration(resultSet.getFloat("yacceleration"));
                sd.setzAcceleration(resultSet.getFloat("zacceleration"));
                sd.setLatitude(resultSet.getFloat("latitude"));
                sd.setLongitude(resultSet.getFloat("longitude"));
                sDs.add(sd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection(connection);
            closePreparedStatement(preparedStatement);
        }
        return sDs;
    }

    private List<SensorData> sDs;
    private final int batchSize = 20;

    public void save(SensorData sD) {
        if (sDs == null) {
            sDs = new LinkedList<SensorData>();
        }
        sDs.add(sD);

        if (sDs.size() >= batchSize) {
            commitSQL(listSDtoSQL(sDs));
            sDs = new LinkedList<SensorData>();
        }
    }

    public void saveRest() {
        commitSQL(listSDtoSQL(sDs));
        sDs = new LinkedList<SensorData>();
    }

    public String listSDtoSQL(List<SensorData> sDs) {
        String sqlQuery = "INSERT INTO SensorData ("
                + "timestamp, xcoordinate, ycoordinate, zcoordinate, xvelocity, yvelocity, zvelocity, "
                + "xacceleration, yacceleration, zacceleration, latitude, longitude"
                + ") VALUES ";
        String delimiter = "";
        for (int i = 0; i < sDs.size(); i++) {
            sqlQuery += delimiter + sDs.get(i).toStringAlternative();
            delimiter = ", ";
        }
        return sqlQuery;
    }

}
