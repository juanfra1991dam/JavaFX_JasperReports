package org.example.javafx_jasperreports;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class JasperReportsController {

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnArtistas;

    @FXML
    private Button btnCerrar;

    @FXML
    private void initialize() {
        btnClientes.setOnAction(event -> generarInforme());
        btnArtistas.setOnAction(event -> abrirVentanaArtistas());
        btnCerrar.setOnAction(event -> cerrarAplicacion());
    }

    private void generarInforme() {
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Cargar el archivo JRXML desde los recursos
            InputStream reportStream = getClass().getResourceAsStream("/jasper/ReportClientes.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo de informe: " + "/jasper/ReportClientes.jrxml");
            }

            // Compilar el informe
            JasperReport report = JasperCompileManager.compileReport(reportStream);

            // Llenar el informe con datos de la base de datos
            JasperPrint print = JasperFillManager.fillReport(report, null, connection);

            // Mostrar el informe
            JasperViewer.viewReport(print, false);

        } catch (JRException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void abrirVentanaArtistas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ArtistasView.fxml"));
            Parent page = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Seleccionar Artista");
            Scene scene = new Scene(page);
            stage.setScene(scene);
            // Obtener el controlador y cargar los datos de la BD
            ArtistasController controller = loader.getController();
            controller.cargarArtistasDesdeDB();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarAplicacion() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
