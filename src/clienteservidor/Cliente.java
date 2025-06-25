package clienteservidor;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
//implementacion del cliente
public class Cliente {

    public static void main(String[] args) {
        try {
            //registra el puerto y la configuracion con la que se trabaja en este caso local 
            Registry registro = LocateRegistry.getRegistry("localhost", 1099);
            //mira por el servicio de busqueda para saber si esta disponible
            Busqueda stub = (Busqueda) registro.lookup("BusquedaService");

            try (Scanner scanner = new Scanner(System.in)) {
                //menu para la seleccion del tipo de busqueda ya sea con el arreglo o con un por definir
                System.out.println("¿Dónde deseas realizar la búsqueda?");
                System.out.println("1. Arreglo fijo del servidor");
                System.out.println("2. Ingresar un arreglo personalizado");
                System.out.print("Opción: ");
                int opcion = scanner.nextInt();
                //ingresa el numero a buscar
                System.out.print("Ingrese el número a buscar: ");
                int numero = scanner.nextInt();
                //inicializa para guardar el objetio de tipo Resultado de busqueda y la duracion
                ResultadoBusqueda res = null; 
                long duracion = 0;

                switch (opcion) {
                    case 1 -> {
                        //inicializa el cronometro en ms para tomar la duracion
                        long inicio = System.currentTimeMillis();
                        //guarda en res la respuesta del servicio buscar numero
                        res = stub.buscarNumero(numero);
                        //finaliza el tiempo
                        long fin = System.currentTimeMillis();
                        //calcula la duracion
                        duracion = fin - inicio;
                    }
                    case 2 -> {
                        //llenado del nuevo arreglo de busqueda
                        System.out.print("¿Cuántos elementos tendrá el arreglo?: ");
                        int n = scanner.nextInt();
                        //si el arreglo no contiene elementos
                        if (n <= 0) {
                            System.out.println("El arreglo debe tener al menos un elemento.");
                            return;
                        }
                        //en otro caso se inicializa un nuevo arreglo con el tamano n 
                        int[] arreglo = new int[n];
                        System.out.println("Ingrese los elementos del arreglo:");
                        //se comienza el llenado del arreglo
                        for (int i = 0; i < n; i++) {
                            System.out.print("Elemento " + (i + 1) + ": ");
                            arreglo[i] = scanner.nextInt();
                        }
                        //misma logica para calcular el tiempo
                        long inicio = System.currentTimeMillis();
                        res = stub.buscarNumeroEnArreglo(numero, arreglo);
                        long fin = System.currentTimeMillis();
                        duracion = fin - inicio;
                    }
                    default -> {
                        System.out.println("Opción no válida.");
                        return;
                    }
                }

                // Validamos que res no sea null antes de continuar
                if (res != null) {
                    if (res.indice != -1) {
                        System.out.println("Número encontrado en el índice: " + res.indice);
                    } else {
                        System.out.println("Número no encontrado.");
                    }
                    System.out.println("Tiempo de procesamiento en el servidor: " + res.tiempoMs + " ms");
                    System.out.println("Tiempo total de búsqueda (cliente): " + duracion + " ms");
                } else {
                    System.out.println("No se pudo realizar la búsqueda.");
                }
            }
        } catch (NotBoundException | RemoteException e) {
            System.err.println("Error en el cliente: " + e.getMessage());
        }
    }
}
