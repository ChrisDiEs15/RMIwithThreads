/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package clienteservidor;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Chris
 */
public interface Busqueda extends Remote {
    //regresa objetos tipo resultado de busqueda para ser presentados o utilizados posteriormente 
    ResultadoBusqueda buscarNumero(int numero) throws RemoteException;
    ResultadoBusqueda buscarNumeroEnArreglo(int numero, int[] arreglo) throws RemoteException;
}
