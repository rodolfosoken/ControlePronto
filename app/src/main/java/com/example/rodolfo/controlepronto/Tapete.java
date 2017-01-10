package com.example.rodolfo.controlepronto;

/**
 * Created by Rodolfo on 10/01/2017.
 */

public class Tapete {
    private int id;
    private long rol;
    private double metragem;
    private int retirado;

    public Tapete(long rol, double metragem) {
        this.rol = rol;
        this.metragem = metragem;
    }

    public Tapete(int id, long rol, double metragem) {
        this.id = id;
        this.rol = rol;
        this.metragem = metragem;
    }

    public String toString(){
        return "Tapete Rol:     "+this.rol+"    |        "+this.metragem+ "    m²";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRol() {
        return rol;
    }

    public void setRol(long rol) {
        this.rol = rol;
    }

    public double getMetragem() {
        return metragem;
    }

    public void setMetragem(double metragem) {
        this.metragem = metragem;
    }

    public int getRetirado() {
        return retirado;
    }

    public void setRetirado(int retirado) {
        this.retirado = retirado;
    }
}
