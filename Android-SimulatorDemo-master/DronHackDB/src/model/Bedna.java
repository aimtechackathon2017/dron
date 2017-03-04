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
public class Bedna implements IPersistableEntry {

    private int bednaId;    
    private Pozice pozice;
    private Material material;
       
    
    @Override
    public String toString() {
        return "Bedna{" 
                + "bednaId=" + bednaId
                + ", pozice=" + pozice.toString()
                + ", material=" + (material!=null ? material.toString() : "/null/") + '}';
    }

    
    
    public int getBednaId() {
        return bednaId;
    }

    public void setBednaId(int bednaId) {
        this.bednaId = bednaId;
    }

    public Pozice getPozice() {
        return pozice;
    }

    public void setPozice(Pozice pozice) {
        this.pozice = pozice;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

}
