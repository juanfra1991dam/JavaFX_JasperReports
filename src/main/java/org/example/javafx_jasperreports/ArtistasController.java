package org.example.javafx_jasperreports;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
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
                // Concatenar ID y nombre para mostrarlo
                artistasList.add(artistId + " - " + nombreArtista);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (artistasList.isEmpty()) {
            System.out.println("No se encontraron artistas en la base de datos.");
        }

        // Asignar la lista al ListView
        listViewArtistas.setItems(artistasList);
    }

    @FXML
    private void generarInforme() {
        // Obtener el artista seleccionado
        String artistaSeleccionado = listViewArtistas.getSelectionModel().getSelectedItem();
        if (artistaSeleccionado != null) {
            System.out.println("Generando informe para: " + artistaSeleccionado);

            // Obtener el ID del artista seleccionado (extraído del string en el ListView)
            String[] artistaParts = artistaSeleccionado.split(" - ");
            int artistId = Integer.parseInt(artistaParts[0]);
            String nombreArtista = artistaParts[1];

            // Generar el informe de artistas
            generarInformeDeArtistas(artistId, nombreArtista);
        } else {
            System.out.println("Por favor, seleccione un artista.");
        }
    }

    private void generarInformeDeArtistas(int artistId, String nombreArtista) {
        try {
            // Establecer conexión con la base de datos
            Connection connection = DatabaseConnection.getConnection();

            // Cargar el archivo JRXML desde los recursos
            InputStream reportStream = getClass().getResourceAsStream("/jasper/ReportArtistas.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("No se pudo encontrar el archivo de informe de artistas");
            }

            // Compilar el informe
            JasperReport report = JasperCompileManager.compileReport(reportStream);

            // Establecer los parámetros para el informe (ID del artista y nombre)
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("ARTISTA_ID", artistId);
            parameters.put("ARTISTA_NOMBRE", nombreArtista);

            // Llenar el informe con datos de la base de datos
            JasperPrint print = JasperFillManager.fillReport(report, parameters, connection);

            // Mostrar el informe
            JasperViewer.viewReport(print, false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Metodo de búsqueda
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase().trim();

        if (searchText.isEmpty()) {
            // Restaurar la lista original si no hay filtro
            listViewArtistas.setItems(artistasList);
        } else {
            // Filtrar sobre la lista almacenada en memoria
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
