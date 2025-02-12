package org.example.javafx_jasperreports;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

public class JasperReportsApp extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException, SQLException {
        // Abrir la conexión a la base de datos al iniciar la aplicación
        DatabaseConnection.getConnection();

        // Cargar la vista FXML
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("JasperReportsView.fxml")));

        // Configurar la escena y mostrarla
        Scene scene = new Scene(root);
        primaryStage.setTitle("Aplicación JavaFX con SQLite");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    @Override
    public void stop() {
        // Cerrar la conexión a la base de datos cuando la aplicación se cierre
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
