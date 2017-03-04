package cz.hackathon.dron;

/**
 * Allows access to dron data sensors.
 */
public interface DronSensorDataProvider {

	int getXCoordinate();

	int getYCoordinate();

	int getZCoordinate();

	int getXVelocity();

	int getYVelocity();

	int getZVelocity();

	int getLatitude();

	int getLongitude();

}
