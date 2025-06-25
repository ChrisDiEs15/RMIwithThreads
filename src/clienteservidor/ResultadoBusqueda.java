/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clienteservidor;

import java.io.Serializable;
//Objeto implementado para encapsular los resultados de la busqueda y puedan ser mostrados
public class ResultadoBusqueda implements Serializable {
    private static final long serialVersionUID = 1L;
//variables para guardar el indice y el tiempo en ms
    public final int indice;
    public final long tiempoMs;
    
    public ResultadoBusqueda(int indice, long tiempoMs) {
        this.indice = indice;
        this.tiempoMs = tiempoMs;
    }
}