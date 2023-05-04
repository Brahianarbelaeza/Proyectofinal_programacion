package controlador;

import aplicacion.Aplicacion;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ControllerLoginView{



    Aplicacion aplicacion;
    ModelFactoryController modelFactoryController;
    ControladorMarketplaceView controladorMarketplaceView;


    @FXML
    void initialize () {
        modelFactoryController = ModelFactoryController.getInstance();
    }



    @FXML
    private Button bttInicioSesion;

    @FXML
    private TextField txtLoginContrasena;

    @FXML
    private TextField txtLoginUsuario;

    @FXML
    void iniciarSesion(ActionEvent event) {
        String usuario = txtLoginUsuario.getText();
        String contrasena = txtLoginContrasena.getText();
        if (modelFactoryController.iniciarSesion(usuario, contrasena)) {
            aplicacion.mostrarVentanaPrincipal();
        }else {
            mostrarMensaje("Notificación", "Datos incorrectos", "El usuario o la contraseña son incorrectos, verifica e intenta nuevamente", Alert.AlertType.WARNING );
            txtLoginUsuario.setText("");
            txtLoginContrasena.setText("");
        }

    }
    private void mostrarMensaje(String titulo, String header, String contenido, Alert.AlertType alertType) {

        Alert aler = new Alert(alertType);
        aler.setTitle(titulo);
        aler.setHeaderText(header);
        aler.setContentText(contenido);
        aler.showAndWait();
    }
    public void setAplicacion(Aplicacion aplicacion) {
        this.aplicacion = aplicacion;
    }

}

