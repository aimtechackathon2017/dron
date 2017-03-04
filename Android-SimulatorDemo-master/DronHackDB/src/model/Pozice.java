package model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jirka
 */
public class Pozice {

    @Override
    public String toString() {
        return "Pozice{" + "poziceId=" + poziceId + ", x=" + x + ", y=" + y + ", z=" + z + ", az=" + az + '}';
    }

    private int poziceId;
    private int XY;
    private float x;
    private float y;
    private float z;
    private float az;

    public Pozice(int poziceId, float x, float y, float z, float az) {
        this.poziceId = poziceId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.az = az;
    }

    public Pozice() {
    }


    public int getXY() {
        return XY;
    }

    public void setXY(int XY) {
        this.XY = XY;
    }
    
    public int getPoziceId() {
        return poziceId;
    }

    public void setPoziceId(int poziceId) {
        this.poziceId = poziceId;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getAz() {
        return az;
    }

    public void setAz(float az) {
        this.az = az;
    }

}
