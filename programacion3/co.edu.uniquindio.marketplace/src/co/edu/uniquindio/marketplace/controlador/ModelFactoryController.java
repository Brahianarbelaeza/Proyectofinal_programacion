package controlador;

import excepciones.AdministradorException;
import excepciones.VendedorException;
import modelo.Administrador;
import modelo.Cuenta;
import modelo.Marketplace;
import modelo.Vendedor;

import java.util.ArrayList;

public class ModelFactoryController {
    Marketplace marketplace;



    private static class SingletonHolder {
        private final static ModelFactoryController INSTANCE = new ModelFactoryController();
    }
    public static ModelFactoryController getInstance() {
        return SingletonHolder.INSTANCE;
    }
    private ModelFactoryController() {
        System.out.println("invoca clase singleton");
        inicializarDatos();
    }
    private void inicializarDatos() {
        marketplace = new Marketplace();
        Administrador admin = crearAdministrador("Brahian", "bar@", "123", "admin", "123");
        marketplace.setAdministrador(admin);
        Vendedor vendedor = new Vendedor("aleja", "Guzman", "123", new Cuenta("aleja", "123"), "calle 2") ;
        admin.getVendedores().add(vendedor);


    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public Administrador crearAdministrador(String nombre, String email, String password, String nombreUsuario, String contrasena) {
        Administrador admin = new Administrador(nombre, email, password, new Cuenta(nombreUsuario, contrasena));
//        marketplace.setAdministrador(admin);
//        if (marketplace.getAdministrador() == null) {
//            throw new AdministradorException("No se pudo crear el administrador");
//        }
        return admin;
    }
    public Vendedor crearVendedor(Vendedor vendedor)throws VendedorException {
        try {
            marketplace.getAdministrador().crearVendedor(vendedor);
        } catch (AdministradorException e) {
            throw new RuntimeException("Error al crear al vendedor"+e);
        }
        return vendedor;
    }
    public void eliminarVendedor(Vendedor vendedor) throws VendedorException{
        try {
            marketplace.getAdministrador().eliminarVendedor(vendedor);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar al vendedor"+e);
        }
    }
    public void actualizarVendedor(Vendedor vendedorSeleccionado, Vendedor vendedorNuevo){
        marketplace.getAdministrador().actualizarVendedor(vendedorSeleccionado, vendedorNuevo);
    }
    public ArrayList<Vendedor> obtenerVendedores() {
        return getMarketplace().getAdministrador().getVendedores();
    }
}