package com.isaac;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Kviz extends Application{

    // Příkaz k připojení do databáze
    static String jdbc_url = "jdbc:mysql://localhost:3306/kviz_maturita";
    static String user = "root";
    static String pwd = "";
    
    // Primární stage, která spustí původní formulář kde si hráč vybírá téma otázek, délku kvízu a zadává jméno
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Maturitní Práce - Kvíz");

        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        Label welcomeLabel = new Label("Jméno: ");
        TextField jmeno = new TextField();
        jmeno.setPromptText("Zadejte jméno...");

        Label delkaKvizu = new Label("Délka kvízu:");
        ComboBox<Integer> pocetOtazek = new ComboBox<>();
        pocetOtazek.getItems().addAll(5, 10, 15, 20);
        pocetOtazek.setPromptText("Vyberte počet otázek");

        Label zamereni = new Label("Zaměření: ");
        ComboBox<String> tema = new ComboBox<>();
        tema.getItems().addAll("Hry", "Filmy", "Hudba", "Zeměpis");
        tema.setPromptText("Vyberte téma");

        // Tlačítko spustí další okno "NoveOkno" kde už se hráče aplikace ptá na otázky
        Button hrat = new Button("Hrát");
        hrat.setDisable(true);

        // Vypnutí tlačítka, dokud hráč nevyplní všechny pole (jelikož jsou nutné)
        hrat.disableProperty().bind(
            Bindings.isEmpty(jmeno.textProperty())
            .or(pocetOtazek.valueProperty().isNull())
            .or(tema.valueProperty().isNull()));

        root.getChildren().addAll(welcomeLabel, jmeno, zamereni, pocetOtazek, delkaKvizu, tema, hrat);

        hrat.setOnAction(event -> {
            NoveOkno noveOkno = new NoveOkno(jmeno.getText(), pocetOtazek.getValue(), tema.getValue(), primaryStage);
            noveOkno.zobrazOtazky();
        });


        Scene scene = new Scene(root, 400, 300);

        // Načítá css styl pro aplikaci
        String css = this.getClass().getResource("/com/isaac/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}