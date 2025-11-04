# Personal Expenses Tracker

A desktop application built with Java and JavaFX for managing personal finances, tracking expenses, categorizing spending, and generating financial reports.

## Features

*   **Expense Tracking:** Record and manage daily expenses with details like amount, category, and date.
*   **Category Management:** Create, edit, and delete custom expense categories.
*   **Budgeting:** Set and monitor budgets for different categories to help control spending.
*   **Recurring Expenses:** Manage recurring expenses to automate tracking of regular payments.
*   **Reporting:** Generate insightful reports to visualize spending patterns and financial summaries.
*   **CSV Integration:** Import and export expense data using CSV files.

## Technologies Used

*   **Java:** The core programming language.
*   **JavaFX:** For building the graphical user interface (GUI).
*   **Maven:** Dependency management and build automation.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

*   Java Development Kit (JDK) 17 or higher
*   Maven 3.6.3 or higher

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/PersonalExpensesTracker.git
    cd PersonalExpensesTracker
    ```
    *(Note: Replace `https://github.com/your-username/PersonalExpensesTracker.git` with the actual repository URL if available.)*

2.  **Build the project using Maven:**
    ```bash
    mvn clean install
    ```

### Running the Application

After building the project, you can run the application using Maven:

```bash
mvn javafx:run
```

Alternatively, you can run the compiled JAR file (located in the `target` directory):

```bash
java -jar target/PersonalExpensesTracker-1.0-SNAPSHOT.jar
```
*(Note: The exact JAR file name might vary based on your `pom.xml` configuration.)*

## Usage

Upon launching the application, you will be presented with a dashboard where you can:
*   Add new expenses.
*   Manage categories and budgets.
*   View and generate financial reports.
*   Import/export data.

## Contributing

Contributions are welcome! Please feel free to fork the repository, create a new branch, and submit a pull request for any improvements or bug fixes.

## License

This project is licensed under the MIT License
