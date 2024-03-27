package com.isaac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

//Přípojení k databázi a SQL příkazy
public class PripojeniDatabaze {
    static String jdbcUrl = "jdbc:mysql://localhost:3306/kviz_maturita";
    static String user = "root";
    static String pwd = "";

    // Výběr otázek podle zvoleného tématu
    public static List<Otazka> nactiOtazky(String tema, int pocetOtazek) {
        List<Otazka> otazky = new ArrayList<>();
        String tabulka;
        // Výběr tématu v databázi podle inputu který zvolil uživatel
        switch (tema) {
            case "Filmy":
                tabulka = "filmove_otazky";
                break;
            case "Hry":
                tabulka = "herni_otazky";
                break;
            case "Hudba":
                tabulka = "hudebni_otazky";
                break;
            case "Zeměpis":
                tabulka = "zemepisne_otazky";
                break;
            case "Otázky od hráčů":
                tabulka = "otazky_od_hracu";
                break;    
            default:
                System.out.println("Neznámé téma: " + tema);
                return otazky;
        }

        String sql = "SELECT id_otazky, otazka, A, B, C, odpoved FROM " + tabulka + " ORDER BY RAND() LIMIT ?";
        
        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pwd);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setInt(1, pocetOtazek);
            
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Otazka otazka = new Otazka(
                        rs.getInt("id_otazky"),
                        rs.getString("otazka"),
                        rs.getString("A"),
                        rs.getString("B"),
                        rs.getString("C"),
                        rs.getString("odpoved")
                );
                otazky.add(otazka);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return otazky;
    }

    // Ukládá jméno a skóre do tabulky v databázi
    public static void ulozSkoreHrace(String jmeno, int skore) {
        String sql = "INSERT INTO hraci (Jmeno, Skore) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pwd);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, jmeno);
            pstmt.setInt(2, skore);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Skóre bylo úspěšně uloženo do databáze.");
            } else {
                System.out.println("Při ukládání skóre došlo k problému.");
            }
        } catch (SQLException e) {
            System.out.println("SQL chyba při ukládání skóre: " + e.getMessage());
        }
    }


    // Přidávání otázky od hráče po dokončení kvízu
    public static void pridatOtazkuDoDatabaze(String otazka, String A, String B, String C, String odpoved) {
        String sql = "INSERT INTO otazky_od_hracu (otazka, A, B, C, odpoved) VALUES (?, ?, ?, ?, ?)";    

        try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pwd);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, otazka);
            pstmt.setString(2, A);
            pstmt.setString(3, B);
            pstmt.setString(4, C);
            pstmt.setString(5, odpoved);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Otázka byla úspěšně přidána do databáze.");
            } else {
                System.out.println("Nepodařilo se přidat otázku do databáze.");
            }
        } catch (SQLException e) {
            System.out.println("SQL chyba při přidávání otázky: " + e.getMessage());
        }
    }

}