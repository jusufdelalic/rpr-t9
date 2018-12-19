package ba.unsa.etf.rpr;

import java.io.Serializable;

public class Grad implements Serializable {
    private int id;
    private String naziv;
    private int brojStanovnika;
    private Drzava drzava;

    public Grad() {
    }

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.id = id;
        this.naziv = naziv;
        this.brojStanovnika = brojStanovnika;
        this.drzava = drzava;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getBrojStanovnika() {
        return brojStanovnika;
    }

    public void setBrojStanovnika(int brojStanovnika) {
        this.brojStanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }
}