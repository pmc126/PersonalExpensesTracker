package com.expensetracker.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class FileManagerFactory {
    public static FileManager getFileManager() {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream("config.properties")) {
            prop.load(fis);
            
            return new CsvFileManager();
        } catch (IOException e) {
            // If config file is not found, default to CsvFileManager
            return new CsvFileManager();
        }
    }
}
