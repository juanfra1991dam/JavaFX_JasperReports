package org.example.javafx_jasperreports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.view.JasperViewer;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ArtistasController {

    @FXML
    private ListView<String> listViewArtistas;

    private final ObservableList<String> artistasList = FXCollections.observableArrayList();

    @FXML
    private Button btnCerrar;

    @FXML
    private TextField searchField;

    public void cargarArtistasDesdeDB() {
        String query = "SELECT ArtistId, Name FROM artists";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            artistasList.clear();

            while (rs.next()) {
                int artistId = rs.getInt("ArtistId");
                String nombreArtista = rs.getString("Name");
                artistasList.add(artistId + " - " + nombreArtista);
            }

            if (artistasList.isEmpty()) {
                System.out.println("No se encontraron artistas en la base de datos.");
            } else {
                System.out.println("Artistas cargados correctamente");
            }

            listViewArtistas.setItems(artistasList);

        } catch (SQLException e) {
            System.err.println("Error al cargar artistas desde la base de datos: " + e.getMessage());
        }
    }

    @FXML
    private void generarInforme() {
        String artistaSeleccionado = listViewArtistas.getSelectionModel().getSelectedItem();
        if (artistaSeleccionado != null) {
            System.out.println("Generando informe para: " + artistaSeleccionado);

            try {
                String[] artistaParts = artistaSeleccionado.split(" - ");
                int artistId = Integer.parseInt(artistaParts[0]);
                String nombreArtista = artistaParts[1];

                generarInformeDeArtistas(artistId, nombreArtista);
            } catch (NumberFormatException e) {
                System.err.println("Error al procesar el ID del artista seleccionado: " + artistaSeleccionado);
            }

        } else {
            System.out.println("Por favor, seleccione un artista antes de generar el informe.");
        }
    }

    private void generarInformeDeArtistas(int artistId, String nombreArtista) {
        Connection connection;
        InputStream reportStream = null;

        try {
            connection = DatabaseConnection.getConnection();

            reportStream = getClass().getResourceAsStream("/jasper/ReportArtistas.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se encontró el archivo de informe 'ReportArtistas.jrxml' en los recursos.");
            }

            JasperReport report = JasperCompileManager.compileReport(reportStream);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ARTISTA_ID", artistId);
            parameters.put("ARTISTA_NOMBRE", nombreArtista);

            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);

            JasperViewer.viewReport(print, false);
            System.out.println("Informe generado correctamente para el artista: " + nombreArtista);

        } catch (JRException e) {
            System.err.println("Error al generar el informe Jasper: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión con la base de datos al generar el informe: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inesperado al generar el informe: " + e.getMessage());
        } finally {
            if (reportStream != null) {
                try {
                    reportStream.close();
                } catch (Exception e) {
                    System.err.println("Error al cerrar el archivo de informe: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            listViewArtistas.setItems(artistasList);
        } else {
            List<String> artistasFiltrados = artistasList.stream()
                    .filter(artista -> artista.toLowerCase().contains(searchText))
                    .collect(Collectors.toList());

            listViewArtistas.setItems(FXCollections.observableArrayList(artistasFiltrados));
        }
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
