package controlador;

import excepciones.*;

import modelo.*;
import persistencia.Persistencia;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class ModelFactoryController implements Runnable {
    Marketplace marketplace;
    int sesion;
    Thread hiloServicio1_GuardarResourceXml;
    Thread hiloServicio2_RegistrarLog;
    Thread hiloServicio3_GuardarResourceBinario;
    Thread hiloServicio4_GuardarVendedores;
    Thread hiloServicio5_GuardarRespaldoXML;
    Thread hiloServicio6_GuardarProductos;
    BoundedSemaphore semaphore = new BoundedSemaphore(1);
    String mensaje;
    int nivel;
    String accion;



    private static class SingletonHolder {
        private final static ModelFactoryController INSTANCE = new ModelFactoryController();
    }

    public static ModelFactoryController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public ModelFactoryController() {
        System.out.println("invoca clase singleton");
        //inicializarSalvarDatos();

        //2. Cargar los datos de los archivos
        //cargarDatosDesdeArchivos();


        //3. Guardar y Cargar el recurso serializable binario
        //guardarResourceBinario();
        //cargarResourceBinario();


        //4. Guardar y Cargar el recurso serializable XML
        //guardarResourceXML();
        cargarResourceXML();
        if (marketplace == null) {
            respaldoXML();
            //inicializarDatos();
            guardarResourceXML();
        }

    }

    @Override
    public void run() {
        Thread hiloActual = Thread.currentThread();

        try {
            semaphore.ocupar();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (hiloActual == hiloServicio1_GuardarResourceXml) {
            Persistencia.guardarRecursoMarketplaceXML(marketplace);
            liberarSemaforo();
        }
        if (hiloActual == hiloServicio2_RegistrarLog) {
            Persistencia.guardaRegistroLog(mensaje, nivel, accion);
            liberarSemaforo();
        }
        if (hiloActual == hiloServicio3_GuardarResourceBinario) {
            Persistencia.guardarRecursoMarketplaceBinario(marketplace);
            liberarSemaforo();
        }
        if (hiloActual == hiloServicio4_GuardarVendedores) {
            try {
                Persistencia.guardarVendedores(getMarketplace().getAdministrador().getVendedores());
            } catch (IOException e) {
                e.printStackTrace();
            }
            liberarSemaforo();
        }
        if (hiloActual == hiloServicio5_GuardarRespaldoXML) {
            try {
                Persistencia.respaldo();
            } catch (IOException e) {
                e.printStackTrace();
            }
            liberarSemaforo();
        }
        if (hiloActual == hiloServicio6_GuardarProductos) {
            ArrayList<Producto> productos= new ArrayList<Producto>();
            for (Vendedor vendedor : getMarketplace().getAdministrador().getVendedores()) {
                    for (Producto producto : vendedor.getListaProductos()) {
                            productos.add(producto);
                  }
            }
            try {
                Persistencia.guardarProductos(productos);
            } catch (IOException e) {
                e.printStackTrace();
            }
            liberarSemaforo();
        }

    }

    private void liberarSemaforo() {
        try {
            semaphore.liberar();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    // Validar que el usuario y la contraseña sean correctos.
    public int iniciarSesion(String usuario, String contrasena) {
        if (marketplace.getAdministrador().getCuenta().getUsuario().equals(usuario) && marketplace.getAdministrador().getCuenta().getContrasena().equals(contrasena)) {
            registrarAccionesSistema("Inicio de sesión del usuario Admin", 1, "inicioSesión");
            sesion = 0;
            return sesion;
        } else {
            for (Vendedor vendedor : marketplace.getAdministrador().getVendedores()) {
                if (vendedor.getCuenta().getUsuario().equals(usuario) && vendedor.getCuenta().getContrasena().equals(contrasena)) {
                    registrarAccionesSistema("Inicio de sesión del usuario Vendedor " + vendedor.getNombre(), 1, "inicioSesión");
                    sesion = marketplace.getAdministrador().getVendedores().indexOf(vendedor) + 1;
                    return sesion;
                }
            }

        }
        return -1;
    }


    private void inicializarSalvarDatos() {
        inicializarDatos();

        try {
            Persistencia.guardarVendedores(getMarketplace().getAdministrador().getVendedores());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // cambiar el nombre de este metodo
    private void cargarDatosDesdeArchivos() {
        this.marketplace = new Marketplace();
        Administrador admin = crearAdministrador("Brahian", "bar@", "123", "admin", "123");
        marketplace.setAdministrador(admin);

        try {
            ArrayList<Vendedor> vendedores = new ArrayList<Vendedor>();
            vendedores = Persistencia.cargarVendedores();
            getMarketplace().getAdministrador().getVendedores().addAll(vendedores);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void respaldoXML() {
        hiloServicio5_GuardarRespaldoXML = new Thread(this);
        hiloServicio5_GuardarRespaldoXML.start();

    }

    private void cargarResourceXML() {
        marketplace = Persistencia.cargarRecursoMarketplaceXML();
    }

    private void guardarResourceXML() {
        hiloServicio1_GuardarResourceXml = new Thread(this);
        hiloServicio1_GuardarResourceXml.start();
    }

    private void cargarResourceBinario() {
        marketplace = Persistencia.cargarRecursoBancoBinario();
    }

    private void guardarResourceBinario() {
        hiloServicio3_GuardarResourceBinario = new Thread(this);
        hiloServicio3_GuardarResourceBinario.start();
    }

    private void guardarVendedores() {
        hiloServicio4_GuardarVendedores = new Thread(this);
        hiloServicio4_GuardarVendedores.start();
    }
    private void guardarProductos(){
        hiloServicio6_GuardarProductos = new Thread(this);
        hiloServicio6_GuardarProductos.start();
    }

    private void inicializarDatos() {
        marketplace = new Marketplace();
        Administrador admin = crearAdministrador("Brahian", "bar@", "123", "admin", "123");
        marketplace.setAdministrador(admin);
        Vendedor vendedor = new Vendedor("aleja", "Guzman", "123", new Cuenta("aleja@gmail.com", "123"), "calle 2");
        admin.getVendedores().add(vendedor);
    }

    public void registrarAccionesSistema(String mensaje, int nivel, String accion) {

        this.mensaje = mensaje;
        this.nivel = nivel;
        this.accion = accion;
        hiloServicio2_RegistrarLog = new Thread(this);
        hiloServicio2_RegistrarLog.start();

    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public Administrador crearAdministrador(String nombre, String apellidos, String cedula, String nombreUsuario, String contrasena) {
        Administrador admin = new Administrador(nombre, apellidos, cedula, new Cuenta(nombreUsuario, contrasena));

        return admin;
    }

    public Vendedor crearVendedor(Vendedor vendedor) {

        try {
            vendedor = marketplace.getAdministrador().crearVendedor(vendedor);
            if (vendedor != null) {
                registrarAccionesSistema("Vendedor creado con cedula " + vendedor.getCedula(), 1, "Crear vendedor");
                guardarVendedores();
                guardarResourceXML();
                respaldoXML();
            }
        } catch (AdministradorException e) {
            registrarAccionesSistema("Error al crear vendedor con cedula " + vendedor.getCedula(), 3, "Crear vendedor");
        }
        return vendedor;
    }

    //Es la misma logica de crear solo que se le envía la cc anterior
    public Vendedor actualizarVendedor(Vendedor vendedor, String cedulaAnterior) {

        marketplace.getAdministrador().actualizarVendedor(vendedor, cedulaAnterior);
        guardarResourceXML();
        registrarAccionesSistema("Vendedor actualizado con cedula " + vendedor.getCedula(), 1, "Actualizar vendedor");
        try {
            Persistencia.guardarVendedores(marketplace.getAdministrador().getVendedores());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return vendedor;
    }

    public boolean eliminarVendedor(String cedula) {
        Vendedor vendedor = marketplace.getAdministrador().buscarVendedor(cedula);

        try {
            marketplace.getAdministrador().eliminarVendedor(vendedor);
            eliminarVendedorAliado(vendedor);
            registrarAccionesSistema("Vendedor eliminado con cedula " + vendedor.getCedula(), 2, "Eliminar vendedor");
            guardarResourceXML();
            guardarVendedores();
            respaldoXML();
            return true;
        } catch (Exception e) {
            registrarAccionesSistema("Se ha creado una nueva excepción" + e, 2, "Eliminar vendedor");
            return false;
        }
    }

    public void eliminarVendedorAliado(Vendedor vendedor) {
        for (Vendedor v : marketplace.getAdministrador().getVendedores()) {
            if (v.getVendedoresAliados().contains(vendedor)) {
                v.getVendedoresAliados().remove(vendedor);
            }
            if (v.getSolicitudesRecibidas().contains(vendedor)) {
                v.getSolicitudesRecibidas().remove(vendedor);
            }
        }
    }

    public Producto publicarProducto(Producto producto) {

        try {
            producto= ObtenerVendedor().crearProducto(producto);
            if (producto !=  null) {
                registrarAccionesSistema("El vendedor" + ObtenerVendedor().getNombre() + "producto creado con codigo " + producto.getCodigo(), 1, "Crear producto");
                guardarResourceXML();
                respaldoXML();
                guardarProductos();
            }
        } catch (VendedorException e) {
           registrarAccionesSistema("Se ha creado una nueva excepción" + e, 2, "Crear producto");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return producto;
    }

    //Es la misma logica de crear solo que se le envía la cc anterior
    public Producto actualizarProducto(Producto producto, String idAnterior) {
        ObtenerVendedor().actualizarProducto(producto,idAnterior);
        guardarResourceXML();
        respaldoXML();
        guardarProductos();
        registrarAccionesSistema("Producto actualizado con cedula "+producto.getCodigo(),1 , "Actualizar vendedor");
        return producto;
    }

    public boolean eliminarProducto (String codigo) {
        Producto producto = ObtenerVendedor().buscarProducto(codigo);

        try {
            ObtenerVendedor().eliminarProducto(producto);
            guardarResourceXML();
            respaldoXML();
            guardarProductos();
            registrarAccionesSistema("Producto eliminado con codigo " + producto.getCodigo(), 2, "Eliminar producto");
            return true;
        } catch (VendedorException e) {
            registrarAccionesSistema("Se ha creado una nueva excepción" + e, 2, "Eliminar producto");
            return false;
        }
    }

    public ArrayList<Vendedor> llenarTablaSugerencias() {
        ArrayList<Vendedor> vendedoresSugeridos = new ArrayList<>();
        ArrayList<Vendedor> vendedores = marketplace.getAdministrador().getVendedores();

        for (int i = 0; i < vendedores.size(); i++) {
                int sugerenciaAleatoria = (int) (Math.random() * vendedores.size());
                if (!vendedoresSugeridos.contains(vendedores.get(sugerenciaAleatoria)) && !vendedores.get(sugerenciaAleatoria).equals(ObtenerVendedor()) && !ObtenerVendedor().getSolicitudesRecibidas().contains(vendedores.get(sugerenciaAleatoria))) {
                    vendedoresSugeridos.add(vendedores.get(sugerenciaAleatoria));
                }
            }


        return vendedoresSugeridos;
    }

    public boolean crearSolicitudAmistad(Vendedor receptor) throws EnviarSolicitudException {
        Vendedor emisor = ObtenerVendedor();

        try {
            receptor.agregarSolicitudAmistad(emisor);
            registrarAccionesSistema("Solicitud de amistad enviada a " + receptor.getNombre(), 1, "Enviar solicitud de amistad");
            guardarResourceXML();
            respaldoXML();
            return true;
        } catch (EnviarSolicitudException e) {
            registrarAccionesSistema("Se ha creado una nueva excepción " + e, 2, "Enviar solicitud de amistad");
            return false;
        }


    }
    public boolean confirmarSolicitudAmistad(Vendedor vendedor) throws ConfirmarSolicitudException {
        Vendedor receptor = ObtenerVendedor();
        try {
            receptor.confirmarSolicitudAmistad(vendedor);
            vendedor.confirmarSolicitudAmistad(receptor);
            registrarAccionesSistema("Solicitud de amistad confirmada con " + vendedor.getNombre(), 1, "Confirmar solicitud de amistad");
            guardarResourceXML();
            respaldoXML();
            return true;
        } catch (ConfirmarSolicitudException e) {
            registrarAccionesSistema("Se ha creado una nueva excepción " + e, 2, "Confirmar solicitud de amistad");
            return false;
        }
    }
    public ArrayList<Producto> obtenerPublicaciones () throws MuroException {
        ArrayList<Producto> publicaciones = ObtenerVendedor().obtenerPublicaciones();
        publicaciones = añadirPublicacionesPropias(publicaciones);

         if (publicaciones == null){
             throw new MuroException("No hay publicaciones");
         }else {
             return publicaciones;
         }
    }

    private ArrayList<Producto> añadirPublicacionesPropias(ArrayList<Producto> publicaciones) {
        for (Producto p: ObtenerVendedor().getListaProductos()) {
            if (!publicaciones.contains(p)){
                publicaciones.add(p);
            }
        }
        return publicaciones;
    }
    public boolean anadirComentario(Producto producto, String comentario) throws ComentariosException {
        for(Vendedor v: marketplace.getAdministrador().getVendedores()){
            for (Producto p: v.getListaProductos()){
                if (p.equals(producto)){
                    p.anadirComentario(comentario);
                    registrarAccionesSistema("Se ha añadido un comentario al producto " + producto.getCodigo(), 1, "Añadir comentario");
                    guardarResourceXML();
                    respaldoXML();
                    return true;
                }
            }
            if (producto == null){
                throw new ComentariosException("No se ha podido añadir el comentario");
            }
        }
        return false;
    }

    public void anadirMeGusta(Producto producto) throws MegustaException{
        for(Vendedor v: marketplace.getAdministrador().getVendedores()){
            for (Producto p: v.getListaProductos()){
                if (p.equals(producto)){
                    p.anadirMeGusta();
                    registrarAccionesSistema("Se ha añadido un me gusta al producto " + producto.getCodigo(), 1, "Añadir me gusta");
                    guardarResourceXML();
                    respaldoXML();
                }
            }
        }if (producto == null){
            throw new MegustaException("No se ha podido añadir el me gusta");
        }
    }
    public ArrayList<Vendedor> obtenerSolicitudes(){
        return ObtenerVendedor().getSolicitudesRecibidas();
    }
    public ArrayList<Vendedor> obtenerContactos() {
        return ObtenerVendedor().getVendedoresAliados();
    }


    public ArrayList<Vendedor> obtenerVendedores() {
        return getMarketplace().getAdministrador().getVendedores();
    }

    public int getSesion() {
        return sesion;
    }

    public void setSesion(int sesion) {
        this.sesion = sesion;
    }

    public ArrayList<Producto> obtenerProductos() {
        if (sesion == 0 ){
            return null;
        }
        return ObtenerVendedor().getListaProductos();
    }
    public Vendedor ObtenerVendedor () {
        for (Vendedor v: marketplace.getAdministrador().getVendedores()) {
            if (sesion-1 == marketplace.getAdministrador().getVendedores().indexOf(v) ){
                return v;
            }
        }
        return null;
    }
}