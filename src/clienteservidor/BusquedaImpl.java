package clienteservidor;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BusquedaImpl extends UnicastRemoteObject implements Busqueda {

    //identificacion de version para serializable
    private static final long serialVersionUID = 1L;

    //constructor de la clase, se agrega remote Exception para manejar cualquier tipo de errores que tengan que ver con la red 
    public BusquedaImpl() throws RemoteException {
        super();
    }

    //tamano para la insercion en masa recibe desde el cliente
    public int tamanoMasa(int b) throws RemoteException {
        return b;
    }

    //arreglo de 100 enteros recibe del cliente el numero a buscar
    public ResultadoBusqueda busquedaEntera(int a) throws RemoteException {
        int[] datos = {872, 203, 94, 569, 441, 786, 150, 39, 640, 913,
            58, 727, 331, 402, 814, 275, 683, 112, 967, 57,
            336, 192, 445, 704, 821, 60, 398, 229, 536, 119,
            789, 250, 371, 654, 83, 947, 312, 176, 427, 698,
            540, 63, 210, 785, 333, 479, 601, 105, 814, 390,
            268, 743, 11, 914, 527, 183, 632, 999, 45, 384,
            572, 709, 298, 154, 835, 413, 221, 690, 57, 879,
            482, 131, 765, 348, 607, 290, 454, 720, 198, 563,
            681, 77, 329, 540, 897, 250, 399, 613, 145, 758,
            310, 494, 825, 61, 703, 219, 576, 88, 462, 347};
        return busquedaConcurrente(a, datos);
    }

    //recibe desde el cliente el arreglo personalizado los mete en busqueda concurrente
    public ResultadoBusqueda busquedaMasa(int c, int[] numero) throws RemoteException {
        return busquedaConcurrente(c, numero);
    }

    //funcion que sirve para registrar en un log las busquedas, recibe el tipo de busqueda, el entero a buscar, el resultado de la busqueda y el tiempo
    private void registrarLog(String tipo, int numero, int resultado, long tiempoMs) {
        //instancia un objeto de tipo writer para escribir en el log, append true es para no sobreescribir y escribir en la siguiente linea con la siguiente buisqueda
        try (FileWriter writer = new FileWriter("busquedas_log.txt", true)) {
            //formato del tiempo 
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            //escribe los resultados 
            writer.write(String.format("[%s] Tipo: %s | Número: %d | Resultado: %d | Tiempo: %d ms%n",
                    timestamp, tipo, numero, resultado, tiempoMs));
        } catch (IOException e) {
            System.err.println("Error al escribir en el log: " + e.getMessage());
        }
    }

    //implementacion de la busqueda concurrente, recibe el numero a buscar y ek arreglo de acuerdo a la eleccion del usuario
    private ResultadoBusqueda busquedaConcurrente(int objetivo, int[] arreglo) {
        //si el arreglo es nulo o la longitud es 0 no inicia la busqueda y queda rgistrado en el log
        if (arreglo == null || arreglo.length == 0) {
            registrarLog("Concurrente", objetivo, -1, 0);
            return new ResultadoBusqueda(-1, 0);
        }
        //variables para realizar la busqueda
        final int[] resultado = {-1};
        final Object lock = new Object();
        //inicializa el tiempo
        long inicio = System.currentTimeMillis();

        //instancia de dos hilos uno para empezar de derecha a izquierda y el otro al reves
        Thread[] hilos = new Thread[2];

        //logica para el primer hilo o hilo 0 (hilo izquierdo)
        hilos[0] = new Thread(() -> {
            for (int i = 0; i < arreglo.length; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                if (arreglo[i] == objetivo) {
                    synchronized (lock) {
                        if (resultado[0] == -1) {
                            resultado[0] = i;
                            hilos[1].interrupt(); // interrumpe el otro hilo 1
                        }
                    }
                    break;
                }
            }
        });

        //logica de busqueda para el segundo hilo o hilo 1 (hilo derecho)
        hilos[1] = new Thread(() -> {
            //recorre el arreglo de derecha a izquierda
            for (int i = arreglo.length - 1; i >= 0; i--) {
                //si la condicion de interrupcion es true, termina el hilo haciendo return
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                // si el dato en la posicion i del arreglo que se menciono es igual al numero buscado
                if (arreglo[i] == objetivo) {
                    //sychrnized evita condicion de carerra, para evitar que ambos hilos quieran escribir el resultado y evitar errores en la obtencion del mismo
                    synchronized (lock) {
                        //verifica si el valor de resultado es -1 
                        if (resultado[0] == -1) {
                            //escribe el resultado
                            resultado[0] = i;
                            //interrumpe la ejecucuion del hilo izquierdo
                            hilos[0].interrupt();
                        }
                    }
                    break;
                }
            }
        });

        //inicializa los hilos
        hilos[0].start();
        hilos[1].start();

        try {
            //la funcion join asegura que ambos hilos terminen antes d econtinuar la ejecucion
            hilos[0].join();
            hilos[1].join();
        } catch (InterruptedException e) {
            //en caso de encontrar alguan exepcion en la ejecucion detiene los hilos
            Thread.currentThread().interrupt();
            System.err.println("Hilo principal interrumpido: " + e.getMessage());
        }
        //registra el tiempo al finalizar la ejecucion de los hilos 
        long fin = System.currentTimeMillis();
        //registra los resultados en el log restando el tiempo final menos el tiempo inicial, el numero que se busca y el tipo de resultado que se obtuvo si no hay resultado la ejecucion termina en -1 si el resultado existe
        //se escribe la posicion en el que se encontro el dato
        registrarLog("Concurrente", objetivo, resultado[0], fin - inicio);

        //regresa el resultado para imprimir
        return new ResultadoBusqueda(resultado[0], fin - inicio);
    }

    //implementa los metodos de la interface
    //funcione que busca un numero accediendo a busqueda entera con el arreglo definido
    @Override
    public ResultadoBusqueda buscarNumero(int numero) throws RemoteException {
        return busquedaEntera(numero);
    }

    //funcion que busca en un arreglo definido por el usuario
    @Override
    public ResultadoBusqueda buscarNumeroEnArreglo(int numero, int[] arreglo) throws RemoteException {
        return busquedaMasa(numero, arreglo);
    }

}
