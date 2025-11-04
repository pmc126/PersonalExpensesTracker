module com.expensetracker {
    requires transitive javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.xml;
    requires org.apache.commons.csv;
    requires java.logging; // Added for logging

    // Keep reflective access for FXML and JavaFX
    opens com.expensetracker to javafx.fxml;
    opens com.expensetracker.model to javafx.fxml, javafx.base;
    opens com.expensetracker.util to javafx.base;

    exports com.expensetracker;
    exports com.expensetracker.model;
    exports com.expensetracker.util;
    exports com.expensetracker.managers;
}