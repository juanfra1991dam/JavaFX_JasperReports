module org.example.javafx_jasperreports {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jasperreports;


    opens org.example.javafx_jasperreports to javafx.fxml;
    exports org.example.javafx_jasperreports;
}