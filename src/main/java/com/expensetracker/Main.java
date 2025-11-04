package com.expensetracker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Application {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the FXML layout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/ExpenseTracker.fxml"));
            Parent root = loader.load();

            // Set the scene
            Scene scene = new Scene(root);
            URL cssUrl = getClass().getResource("application.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm()); // Optional CSS
            }

            primaryStage.setTitle("💰 Personal Expense Tracker");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch(Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to load main application FXML", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
