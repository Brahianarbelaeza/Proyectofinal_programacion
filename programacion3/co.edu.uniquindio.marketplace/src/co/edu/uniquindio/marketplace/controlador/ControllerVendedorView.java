package controlador;

import aplicacion.Aplicacion;
import excepciones.*;
import javafx.fxml.FXML;
import modelo.*;

import java.util.ArrayList;

public class ControllerVendedorView {

    Aplicacion aplicacion;
    ModelFactoryController modelFactoryController;
    Marketplace marketplace;


    public ControllerVendedorView(ModelFactoryController modelFactoryController) {
        this.modelFactoryController = modelFactoryController;
        marketplace = modelFactoryController.getMarketplace();
    }

    @FXML
    void initialize () {
        modelFactoryController = ModelFactoryController.getInstance();
    }
    public boolean eliminarProducto(String codigo) {
        return modelFactoryController.eliminarProducto(codigo);

    }
    public Producto publicarProducto(String codigo, String nombreProducto, String rutaImagen, String categoria, double precio, Estado estado)  {

        Producto producto = new Producto(codigo,nombreProducto,rutaImagen,categoria,precio,estado);
        return modelFactoryController.publicarProducto(producto);
    }

    public Producto actualizarProducto (String codigo, String nombreProducto, String rutaImagen, String categoria,  double precio, Estado estado, String idAnterior)   {

        Producto producto = new Producto(codigo,nombreProducto,rutaImagen,categoria,precio,estado);

        return modelFactoryController.actualizarProducto(producto, idAnterior);
    }


    public ArrayList<Vendedor> llenarTablaTablaSugerencias(){
        return modelFactoryController.llenarTablaSugerencias();
    }





    public ArrayList<Producto> obtenerProductos() {
        return modelFactoryController.obtenerProductos();
    }

    public ArrayList<Producto> obtenerPublicaciones() {
        try {
            return modelFactoryController.obtenerPublicaciones();
        } catch (MuroException e) {
            modelFactoryController.registrarAccionesSistema("Error al obtener publicaciones "+ e, 3, "Obtener publicaciones");
            e.printStackTrace();
            return null;
        }
    }

    public boolean crearSolicitudAmistad(Vendedor v)  {
        try {
            return modelFactoryController.crearSolicitudAmistad(v);
        } catch (EnviarSolicitudException e) {
            modelFactoryController.registrarAccionesSistema("Error al enviar solicitud de amistad "+ e, 3, "Crear solicitud de amistad");
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmarSolicitudAmistad(Vendedor vendedor) {
        try {
            return modelFactoryController.confirmarSolicitudAmistad(vendedor);
        } catch (ConfirmarSolicitudException e) {
            modelFactoryController.registrarAccionesSistema("Error al confirmar solicitud de amistad "+ e, 3, "Confirmar solicitud de amistad");
            e.printStackTrace();
            return false;
        }
    }


    public boolean eliminarSolicitudAmistad(Vendedor vendedor) {
        try {
            return modelFactoryController.eliminarSolicitudAmistad(vendedor);
        } catch (EliminarSolicitudException e) {
            modelFactoryController.registrarAccionesSistema("Error al eliminar solicitud de amistad "+ e, 3, "Eliminar solicitud de amistad");
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarContacto(Vendedor vendedor){
        return modelFactoryController.eliminarContacto(vendedor);
    }

    public boolean anadirComentario(Producto producto, String comentario) {
        try {
            return modelFactoryController.anadirComentario(producto, comentario);
        } catch (ComentariosException e) {
            modelFactoryController.registrarAccionesSistema("Error al añadir comentario "+ e, 3, "Añadir comentario");
            return false;
        }
    }

    public void anadirMeGusta(Producto producto) {
        try {
            modelFactoryController.anadirMeGusta(producto);
        } catch (MegustaException e) {
            modelFactoryController.registrarAccionesSistema("Error al añadir me gusta "+ e, 3, "Añadir me gusta");
        }
    }
}
