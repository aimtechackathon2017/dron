/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author Jirka
 */
public class SensorData implements IPersistableEntry {

    public SensorData() {
        timestamp = System.currentTimeMillis();
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getxCoordinate() {
        return xCoordinate;
    }

    public void setxCoordinate(float xCoordinate) {
        this.xCoordinate = xCoordinate;
    }

    public float getyCoordinate() {
        return yCoordinate;
    }

    public void setyCoordinate(float yCoordinate) {
        this.yCoordinate = yCoordinate;
    }

    public float getzCoordinate() {
        return zCoordinate;
    }

    public void setzCoordinate(float zCoordinate) {
        this.zCoordinate = zCoordinate;
    }

    public float getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(float xVelocity) {
        this.xVelocity = xVelocity;
    }

    public float getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(float yVelocity) {
        this.yVelocity = yVelocity;
    }

    public float getzVelocity() {
        return zVelocity;
    }

    public void setzVelocity(float zVelocity) {
        this.zVelocity = zVelocity;
    }

    public float getxAcceleration() {
        return xAcceleration;
    }

    public void setxAcceleration(float xAcceleration) {
        this.xAcceleration = xAcceleration;
    }

    public float getyAcceleration() {
        return yAcceleration;
    }

    public void setyAcceleration(float yAcceleration) {
        this.yAcceleration = yAcceleration;
    }

    public float getzAcceleration() {
        return zAcceleration;
    }

    public void setzAcceleration(float zAcceleration) {
        this.zAcceleration = zAcceleration;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String toStringAlternative() {
        return "(" + timestamp + ", " + xCoordinate + ", " + yCoordinate + ", " + zCoordinate + ", " + xVelocity
                + ", " + yVelocity + ", " + zVelocity + ", " + xAcceleration + ", " + yAcceleration
                + ", " + zAcceleration + ", " + latitude + ", " + longitude + ')';
    }

    private int id;
    private long timestamp;
    private float xCoordinate;
    private float yCoordinate;
    private float zCoordinate;
    private float xVelocity;
    private float yVelocity;
    private float zVelocity;
    private float xAcceleration;
    private float yAcceleration;
    private float zAcceleration;
    private float latitude;
    private float longitude;
}
