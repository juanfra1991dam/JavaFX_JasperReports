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

public class JasperReportsController {

    @FXML
    private Button btnClientes;

    @FXML
    private Button btnArtistas;

    @FXML
    private Button btnCerrar;

    @FXML
    private void initialize() {
        btnClientes.setOnAction(event -> generarInformeClientes());
        btnArtistas.setOnAction(event -> abrirVentanaArtistas());
        btnCerrar.setOnAction(event -> cerrarAplicacion());
    }

    private void generarInformeClientes() {
        InputStream reportStream = null;

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Cargar el archivo JRXML desde los recursos
            reportStream = getClass().getResourceAsStream("/jasper/ReportClientes.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo de informe: /jasper/ReportClientes.jrxml");
            }

            // Compilar el informe
            JasperReport report = JasperCompileManager.compileReport(reportStream);

            // Llenar el informe con datos de la base de datos
            JasperPrint print = JasperFillManager.fillReport(report, null, connection);

            // Mostrar el informe
            JasperViewer.viewReport(print, false);
            System.out.println("Informe de clientes generado correctamente");

        } catch (JRException e) {
            System.err.println("Error al generar el informe Jasper: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión con la base de datos: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al generar el informe: " + e.getMessage());
        } finally {
            if (reportStream != null) {
                try {
                    reportStream.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el archivo de informe: " + e.getMessage());
                }
            }
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
            stage.setScene(new Scene(page));

            // Obtener el controlador y cargar los datos de la BD
            ArtistasController controller = loader.getController();
            controller.cargarArtistasDesdeDB();

            stage.show();
            System.out.println("Ventana de artistas abierta correctamente.");

        } catch (IOException e) {
            System.err.println("Error al abrir la ventana de artistas: " + e.getMessage());
        }
    }

    @FXML
    private void cerrarAplicacion() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        System.out.println("Cerrando la aplicación.");
        stage.close();
    }
}
