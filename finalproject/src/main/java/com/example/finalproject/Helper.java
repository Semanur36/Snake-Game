package com.example.finalproject;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Helper {

    public static int score = 0;
    public static String user = "";
    public static String pass = "";

    public static void display() {
    Stage window = new Stage();
    window.centerOnScreen();
    window.initModality(Modality.APPLICATION_MODAL);
    window.setTitle("User Auth Form");
    window.setMinWidth(400);

    TextField nameField = new TextField();
    PasswordField passwordField = new PasswordField();

    Button saveButton = new Button("ENTER");
    saveButton.setOnAction(e -> {
        String name = nameField.getText().trim();
        String password = passwordField.getText().trim();

        if (!name.isEmpty() && !password.isEmpty()) {
            Integer score = tryLoginOrPromptCreate(name, password, window);
            if (score != null) {
                showInfo("Giriş Başarılı", "Hoşgeldin " + name + "! Skorun: " + score);
                window.close(); // giriş tamam
            }
        } else {
            showError("Hata", "Lütfen tüm alanları doldurun.");
        }
    });

    GridPane grid = new GridPane();
    grid.setPadding(new Insets(20));
    grid.setHgap(10);
    grid.setVgap(10);

    grid.add(new Label("Name:"), 0, 0);
    grid.add(nameField, 1, 0);

    grid.add(new Label("Password:"), 0, 1);
    grid.add(passwordField, 1, 1);

    grid.add(saveButton, 1, 2);

    Scene scene = new Scene(grid);
    window.setScene(scene);
    window.showAndWait();
}

    public static Integer tryLoginOrPromptCreate(String name, String password, Stage parent) {
        String filePath = "scores.txt";
        File file = new File(filePath);

        try {
            if (file.createNewFile()) {
                System.out.println("Dosya oluşturuldu: " + filePath);
            }
        } catch (IOException e) {
            showError("Hata", "Dosya oluşturulamadı.");
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String existingName = parts[0].trim();
                    String existingPassword = parts[1].trim();
                    int existingScore = Integer.parseInt(parts[2].trim());
                    score = existingScore;
                    user = existingName;
                    pass = existingPassword;
                    if (existingName.equals(name) && existingPassword.equals(password)) {
                        return existingScore;
                    }
                }
            }
        } catch (IOException e) {
            showError("Hata", "Dosya okuma hatası.");
            return null;
        }

        // Kullanıcı bulunamadı, onay iste
        boolean confirm = confirmDialog("Kullanıcı bulunamadı",
                "Bu isim ve şifreyle kayıtlı bir kullanıcı yok.\nYeni kullanıcı oluşturulsun mu?");

        if (confirm) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.write(name + "," + password + ",0");
                writer.newLine();
                user = name;
                pass = password;
                System.out.println("Yeni kullanıcı oluşturuldu.");
                return 0;
            } catch (IOException e) {
                showError("Hata", "Kayıt oluşturulamadı.");
            }
        }

        return null; // kullanıcı vazgeçti
    }


    public static void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirmDialog(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK;
    }


    public static void updateScore(int newScore) {
        String filePath = "scores.txt";
        File file = new File(filePath);
        List<String> updatedLines = new ArrayList<>();
        boolean userFound = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String existingName = parts[0].trim();
                    String existingPassword = parts[1].trim();

                    if (existingName.equals(user) && existingPassword.equals(pass)) {
                        // Yeni skorla güncelle
                        line = existingName + "," + existingPassword + "," + newScore;
                        userFound = true;
                    }
                }
                updatedLines.add(line);
            }
        } catch (IOException e) {
            showError("Hata", "Dosya okunamadı: " + e.getMessage());
            return;
        }

        if (!userFound) {
            showError("Kullanıcı Bulunamadı", "Skor güncellenemedi, kullanıcı bilgileri uyuşmuyor.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String updatedLine : updatedLines) {
                writer.write(updatedLine);
                writer.newLine();
            }
            showInfo("Başarılı", "Skor başarıyla güncellendi.");
        } catch (IOException e) {
            showError("Hata", "Dosya yazılamadı: " + e.getMessage());
        }
    }



}
