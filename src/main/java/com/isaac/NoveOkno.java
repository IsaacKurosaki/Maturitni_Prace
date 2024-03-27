package com.isaac;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class NoveOkno {

    private String jmeno;
    private int pocetOtazek;
    private String tema;
    private List<Otazka> otazky;
    private int aktualniIndexOtazky = 0;
    private int skore = 0;
    private Stage stage;
    private VBox layout;
    private final List<String> odpovediUzivatele = new ArrayList<>();

    // Přijímá data z předchozí třídy "Kvíz"
    public NoveOkno(String jmeno, int pocetOtazek, String tema, Stage uvodniStage) {
        this.jmeno = jmeno;
        this.pocetOtazek = pocetOtazek;
        this.tema = tema;
        this.stage = uvodniStage;
        this.otazky = PripojeniDatabaze.nactiOtazky(tema, pocetOtazek);
    }

    // Zobrazení otázek z databáze
    public void zobrazOtazky() {
        layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        aktualizujObsahOtazky();
        //ScrollPane místo layout, kdyby byla otázka moc dlouhá
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(layout);
        scrollPane.setFitToWidth(true);
        Scene scene = new Scene(scrollPane, 800, 400);

        // Načtení css stylu pro kvíz
        String css = this.getClass().getResource("/com/isaac/kviz.css").toExternalForm();
        scene.getStylesheets().add(css);
    
        stage.setScene(scene);
        stage.setTitle("Kvíz - Otázky");
        stage.show();
    }

    // Aktualizuje UI otázek do konce kvízu (pak zobrazí výsledky)
    private void aktualizujObsahOtazky() {
        layout.getChildren().clear();
        if (aktualniIndexOtazky < otazky.size()) {
            Otazka otazka = otazky.get(aktualniIndexOtazky);
            Label lblOtazka = new Label((aktualniIndexOtazky + 1) + ". " + otazka.getTextOtazky());
            lblOtazka.setWrapText(true);

            HBox answersAlingmentBox = new HBox();
            answersAlingmentBox.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(answersAlingmentBox, new Insets(0, 0, 0, layout.getWidth() * 0.2));

            VBox answersBox = new VBox(5);
            ToggleGroup skupina = new ToggleGroup();
            
            RadioButton btnA = new RadioButton("A) " + otazka.getMoznostA());
            btnA.setToggleGroup(skupina);
            btnA.setUserData("A");
            RadioButton btnB = new RadioButton("B) " + otazka.getMoznostB());
            btnB.setToggleGroup(skupina);
            btnB.setUserData("B");
            RadioButton btnC = new RadioButton("C) " + otazka.getMoznostC());
            btnC.setToggleGroup(skupina);
            btnC.setUserData("C");

            answersBox.getChildren().addAll(btnA, btnB, btnC);
            answersAlingmentBox.getChildren().add(answersBox);


            layout.getChildren().addAll(lblOtazka, answersAlingmentBox);

            Button btnDalsiOtazka = new Button("Další otázka");
            btnDalsiOtazka.setOnAction(e -> {
                String vybranaOdpoved = (String) skupina.getSelectedToggle().getUserData();
                if (vybranaOdpoved.equals(otazka.getSpravnaOdpoved())) {
                    skore++;
                }
                aktualniIndexOtazky++;
                aktualizujObsahOtazky();
            });
            layout.getChildren().add(btnDalsiOtazka);
        } else {
            zobrazVysledky();
        }
    }
    
    // Zobrazí jméno, skóre a otázky se správnými odpovědmi
    private void zobrazVysledky() {
        PripojeniDatabaze.ulozSkoreHrace(jmeno, skore);

        layout.getChildren().clear();

        Label lblSkore = new Label("Skóre: " + skore + "/" + otazky.size());
        Label lblJmeno = new Label("Jméno: " + jmeno);
        layout.getChildren().addAll(lblSkore, lblJmeno);

        for (Otazka otazka : otazky) {
            VBox questionBox = new VBox(5);
            questionBox.getStyleClass().add("question-box");

            Label lblQuestion = new Label(otazka.getTextOtazky());
            lblQuestion.setWrapText(true);
            
            String correctAnswerText;
            switch (otazka.getSpravnaOdpoved()) {
                case "A":
                    correctAnswerText = otazka.getMoznostA();
                    break;
                case "B":
                    correctAnswerText = otazka.getMoznostB();
                    break;
                case "C":
                    correctAnswerText = otazka.getMoznostC();
                    break;
                default:
                    correctAnswerText = "Unknown";
                    break;
            }

            Label lblCorrectAnswer = new Label("Správná odpověď: " + correctAnswerText);
            lblCorrectAnswer.getStyleClass().add("correct-answer");

            questionBox.getChildren().addAll(lblQuestion, lblCorrectAnswer);
            layout.getChildren().add(questionBox);
        }

        // Vrátí hráče na úvodní formulář
        Button btnZnovu = new Button("Hrát znovu");
        btnZnovu.setOnAction(e -> {
            Kviz kviz = new Kviz();
            kviz.start(stage);
        });

        layout.getChildren().addAll(btnZnovu);
    }

}
