package com.expensetracker;

import com.expensetracker.model.CategoryReportData;
import com.expensetracker.model.Expense;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell; // Added import for TableCell

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportViewController {

    @FXML private TableView<CategoryReportData> reportTable;
    @FXML private TableColumn<CategoryReportData, String> categoryColumn;
    @FXML private TableColumn<CategoryReportData, Double> totalAmountColumn;
    @FXML private TableColumn<CategoryReportData, Double> percentageColumn;
    @FXML private Label grandTotalLabel;

    public void initialize() {
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("categoryName"));
        totalAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        // Format totalAmountColumn to currency
        totalAmountColumn.setCellFactory(col -> new TableCell<CategoryReportData, Double>() {
            @Override
            protected void updateItem(Double amount, boolean empty) {
                super.updateItem(amount, empty);
                if (empty || amount == null) {
                    setText(null);
                }
                else {
                    setText(String.format("K %.2f", amount));
                }
            }
        });

        // Format percentageColumn to percentage
        percentageColumn.setCellFactory(col -> new TableCell<CategoryReportData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                }
                else {
                    setText(String.format("%.2f%%", percentage));
                }
            }
        });
    }

    public void setExpenses(List<Expense> expenses) {
        double grandTotal = expenses.stream().mapToDouble(Expense::getAmount).sum();
        grandTotalLabel.setText(String.format("Grand Total: K %.2f", grandTotal));

        Map<String, Double> categoryTotals = expenses.stream()
                .collect(Collectors.groupingBy(Expense::getCategory, Collectors.summingDouble(Expense::getAmount)));

        ObservableList<CategoryReportData> reportData = FXCollections.observableArrayList();
        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            String categoryName = entry.getKey();
            double totalAmount = entry.getValue();
            double percentage = (grandTotal > 0) ? (totalAmount / grandTotal) * 100 : 0;
            reportData.add(new CategoryReportData(categoryName, totalAmount, percentage));
        }
        reportTable.setItems(reportData);
    }
}
