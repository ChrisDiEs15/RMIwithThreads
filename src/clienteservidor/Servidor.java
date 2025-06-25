package clienteservidor;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

//configuracion del servicio de servidor
public class Servidor {

    public static void main(String[] args) {

        try {
            //instancia busqueda como obj para acceder a ella
            BusquedaImpl obj = new BusquedaImpl();
            //inicializa el registro para el puerto de conexion
            Registry registro;

            try {
                //define el puerto de conexion
                registro = LocateRegistry.createRegistry(1099);
                System.out.println("Registro RMI creado en el puerto 1099.");
                //imprime si ya existe el regsitro del puerto
            } catch (RemoteException e) {
                System.out.println("Registro RMI ya existente, accediendo...");
                registro = LocateRegistry.getRegistry(1099);
            }
            //se registra el nombre con el que se puede buscar el servicio y se da a el objeto remoto a acceder en este caso una instancia de busquedaIMP
            registro.rebind("BusquedaService", obj);
            //se imprime que el servicio ha quedado registrado correctamente
            System.out.println("Objeto 'BusquedaService' registrado correctamente.");
            System.out.println("Servidor RMI listo y escuchando en puerto 1099...");
        } catch (RemoteException e) {
            //imprime si existe algun error
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
