package cz.hackathon.dron.model;

/**
 * A sample of data from drone sensors at specific time moment.
 * 
 */
public class FlyMoment {

	private final long timestamp;

	private final int xCoordinate;

	private final int yCoordinate;

	private final int zCoordinate;

	private final int xVelocity;

	private final int yVelocity;

	private final int zVelocity;

	private final double latitude;

	private final double longitude;

	public FlyMoment(long timestamp, int xCoordinate, int yCoordinate, int zCoordinate, int xVelocity,
			int yVelocity, int zVelocity, double latitude, double longitude) {
		super();
		this.timestamp = timestamp;
		this.xCoordinate = xCoordinate;
		this.yCoordinate = yCoordinate;
		this.zCoordinate = zCoordinate;
		this.xVelocity = xVelocity;
		this.yVelocity = yVelocity;
		this.zVelocity = zVelocity;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public int getxCoordinate() {
		return xCoordinate;
	}

	public int getyCoordinate() {
		return yCoordinate;
	}

	public int getzCoordinate() {
		return zCoordinate;
	}

	public int getxVelocity() {
		return xVelocity;
	}

	public int getyVelocity() {
		return yVelocity;
	}

	public int getzVelocity() {
		return zVelocity;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
