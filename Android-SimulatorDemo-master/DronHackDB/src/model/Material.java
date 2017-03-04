package model;

import DronHackContext.Context;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jirka
 */
public class Material implements IPersistableEntry {

    private int id;
    private String nazev;
    private List<Bedna> bedny;
    


    @Override
    public String toString() {
        return "Material{" + "id=" + id
                + ", nazev=" + nazev
                + ", bedny počet="
                + (bedny != null && bedny.size() != 0 ? bedny.size() : "zadne")
                + '}';
    }

    public List<Bedna> getBedny() {
        return bedny;
    }

    public void setBedny(List<Bedna> bedny) {
        this.bedny = bedny;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

}
