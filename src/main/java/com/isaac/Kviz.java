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

public class Kviz extends Application {

    static String jdbc_url = "jdbc:mysql://localhost:3306/kviz_maturita";
    static String user = "root";
    static String pwd = "";

    public void start(Stage primaryStage) {
        primaryStage.setTitle("Maturitní Práce - Kvíz");
        primaryStage.setScene(createMainScene(primaryStage));
        primaryStage.show();
    }

    private Scene createMainScene(Stage primaryStage) {
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
        tema.getItems().addAll("Hry", "Filmy", "Hudba", "Zeměpis", "Otázky od hráčů");
        tema.setPromptText("Vyberte téma");

        Button hrat = new Button("Hrát");
        hrat.setDisable(true);
        hrat.disableProperty().bind(
                Bindings.isEmpty(jmeno.textProperty())
                        .or(pocetOtazek.valueProperty().isNull())
                        .or(tema.valueProperty().isNull()));

        hrat.setOnAction(event -> {
            NoveOkno noveOkno = new NoveOkno(jmeno.getText(), pocetOtazek.getValue(), tema.getValue(), primaryStage);
            noveOkno.zobrazOtazky();
        });

        Button btnPridatOtazku = new Button("Přidat otázku");
        btnPridatOtazku.setOnAction(e -> zobrazFormularProPridaniOtazky(primaryStage));

        root.getChildren().addAll(welcomeLabel, jmeno, delkaKvizu, pocetOtazek, zamereni, tema, hrat, btnPridatOtazku);

        Scene scene = new Scene(root, 400, 300);
        String css = this.getClass().getResource("/com/isaac/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        return scene;
    }

    private void zobrazFormularProPridaniOtazky(Stage primaryStage) {
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);

        TextField txtOtazka = new TextField();
        txtOtazka.setPromptText("Text otázky");
        TextField txtA = new TextField();
        txtA.setPromptText("Možnost A");
        TextField txtB = new TextField();
        txtB.setPromptText("Možnost B");
        TextField txtC = new TextField();
        txtC.setPromptText("Možnost C");

        ComboBox<String> cbOdpoved = new ComboBox<>();
        cbOdpoved.getItems().addAll("A", "B", "C");
        cbOdpoved.setPromptText("Správná odpověď");

        Button btnPridat = new Button("Přidat");
        btnPridat.setOnAction(e -> {
            PripojeniDatabaze.pridatOtazkuDoDatabaze(txtOtazka.getText(), txtA.getText(), txtB.getText(), txtC.getText(), cbOdpoved.getValue());
            primaryStage.setScene(createMainScene(primaryStage));
        });

        root.getChildren().addAll(txtOtazka, txtA, txtB, txtC, cbOdpoved, btnPridat);

        Scene scene = new Scene(root, 400, 300);
        String css = this.getClass().getResource("/com/isaac/kviz.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
