package ba.unsa.etf.rpr;



import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;

/*
 URL je oblika: jdbc:sqlite:filename gdje je filename
  kompletan put do datoteke. Put je relativan na korijen vašeg projekta.

  Projekat treba da sadrži SQLite bazu podataka baza.db (relativni put je samo "baza.db" tj.
  datoteka treba da se nalazi u korijenskom direktoriju projekta) koja sadrži dvije tabele:


 */

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private static Connection conn;
    private Statement stmt;
    private String url = "jdbc:sqlite:baza.db";
    private PreparedStatement upit;
    private ArrayList<Grad> gradovi;
    private ArrayList<Drzava> drzave;

    public ArrayList<Drzava> getDrzave() { return drzave; }

    private static void initialize() {
        instance = new GeografijaDAO();
    }

    private void napuniPodacima() {
        Grad pariz = new Grad(1, "Pariz", 2229621, null);
        Grad london = new Grad(2, "London", 7355400, null);
        Grad bech = new Grad(3, "Beč", 1867582, null);
        Grad manchester = new Grad(4, "Manchester", 441200, null);
        Grad graz = new Grad(5, "Graz", 286686, null);
        Drzava francuska = new Drzava(1, "Francuska", pariz);
        Drzava engleska = new Drzava(2, "Velika Britanija", london);
        Drzava austrija = new Drzava(3, "Austrija", bech);
        pariz.setDrzava(francuska);
        london.setDrzava(engleska);
        bech.setDrzava(austrija);
        manchester.setDrzava(engleska);
        graz.setDrzava(austrija);
        gradovi.add(pariz);
        gradovi.add(london);
        gradovi.add(bech);
        gradovi.add(manchester);
        gradovi.add(graz);
        drzave.add(francuska);
        drzave.add(engleska);
        drzave.add(austrija);
    }

   public void kreirajTabele () throws SQLException {

        String sql = "CREATE TABLE IF NOT EXISTS gradovi(\n"
                + " id integer PRIMARY KEY,\n"
                + " naziv text NOT NULL UNIQUE,\n"
                + " brojStanovnika integer,\n"
                + " drzava integer REFERENCES drzave\n"
                + ");";

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();

        //Tabela drzava sadrži kolone: id (int, primarni ključ), naziv (text), glavni_grad (int, strani ključ)

        sql = "CREATE TABLE IF NOT EXISTS drzave(\n"
                + " id integer PRIMARY KEY,\n"
                + " naziv text NOT NULL UNIQUE,\n"
                + " glavni_grad integer REFERENCES gradovi\n"
                + ");";

        stmt = conn.prepareStatement(sql);
        stmt.execute();

    }

    private GeografijaDAO() {
        gradovi = new ArrayList<>();
        drzave = new ArrayList<>();

        try {

            //Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url);

            kreirajTabele();
            napuniPodacima();


            upit = conn.prepareStatement("INSERT INTO grad VALUES (?, ?, ?, NULL)");
            for (var grad : gradovi) {
                try {
                    upit.setInt(1, grad.getId());
                    upit.setString(2, grad.getNaziv());
                    upit.setInt(3, grad.getBrojStanovnika());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            upit = conn.prepareStatement("INSERT  INTO drzava VALUES(?, ?, ?)");
            for (var drzava : drzave) {
                try {
                    upit.setInt(1, drzava.getId());
                    upit.setString(2, drzava.getNaziv());
                    upit.setInt(3, drzava.getGlavniGrad().getId());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }
            upit = conn.prepareStatement("UPDATE grad SET drzava = ? WHERE id = ?");
            for (var grad : gradovi) {
                try {
                    upit.setInt(1, grad.getDrzava().getId());
                    upit.setInt(2, grad.getId());
                    upit.executeUpdate();
                } catch (SQLException ignored) {
                }
            }

            upit = conn.prepareStatement("insert into drzava values (?,?,?)");
                try {
                    Grad Sarajevo = new Grad(6,"Sarajevo",400000,null);
                    Drzava BiH = new Drzava(4,"BiH",Sarajevo);
                    Sarajevo.setDrzava(BiH);
                    drzave.add(BiH);
                    gradovi.add(Sarajevo);

                    upit.setInt(1,BiH.getId());
                    upit.setString(2,BiH.getNaziv());
                    upit.setInt(3,BiH.getGlavniGrad().getId());
                    upit.executeUpdate();



                }

                catch (SQLException ignored) {
                }

        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static void removeInstance() {
        instance = null;
    }

    public static GeografijaDAO getInstance() {
        if (instance == null)
            initialize();
        return instance;
    }

    public void obrisiDrzavu(String drzava) {
        for (int i = 0; i < drzave.size(); i++) {
            if (drzave.get(i).getNaziv().equals(drzava)) {
                drzave.remove(i);
                break;
            }
        }
    }

    public ArrayList<Grad> gradovi() {

        for(int i=0; i<gradovi.size(); i++)
            for(int j=i+1; j<gradovi.size(); j++) {

                if(gradovi.get(j).getBrojStanovnika() > gradovi.get(i).getBrojStanovnika())
                    Collections.swap(gradovi, i, j);
            }

        return gradovi;
    }



    public Grad glavniGrad(String drzava) {
        for (int i = 0; i < drzave.size(); i++) {
            if (drzave.get(i).getNaziv().equals(drzava))
                return drzave.get(i).getGlavniGrad();
        }
        return null;
    }

    public Drzava nadjiDrzavu(String drzava) {
        for (int i = 0; i < drzave.size(); i++) {
            if (drzave.get(i).getNaziv().equals(drzava))
                return drzave.get(i);
        }
        return null;
    }

    public void dodajGrad(Grad grad) {
        if (gradovi.contains(grad))
            throw new IllegalArgumentException("Grad vec postoji");
        gradovi.add(grad);
    }

    public void dodajDrzavu(Drzava drzava) {
        if (drzave.contains(drzava))
            throw new IllegalArgumentException("Drzava vec postoji");
        drzave.add(drzava);
    }

    public void izmijeniGrad(Grad grad) {
        for (int i = 0; i < gradovi.size(); i++) {
            if (gradovi.get(i).equals(grad)) {
                gradovi.get(i).setNaziv(grad.getNaziv());
                gradovi.get(i).setBrojStanovnika(grad.getBrojStanovnika());
                gradovi.get(i).setDrzava(grad.getDrzava());
                gradovi.get(i).setId(grad.getId());
                break;
            }
        }
        throw new IllegalArgumentException("Grad ne postoji");
    }

}
