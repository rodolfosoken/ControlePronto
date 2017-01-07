package com.example.rodolfo.controlepronto;

/**
 * Created by Rodolfo on 07/01/2017.
 */

public class Edredom {
    private long rol;
    private int prateleira;
    private boolean retirado;

    public Edredom(long rol, int prateleira){
        this.rol = rol;
        this.prateleira = prateleira;
        this.retirado = false;
    }

    public String toString(){
        return this.rol + " |   " + this.prateleira;
    }


    public long getRol() {
        return rol;
    }

    public void setRol(long rol) {
        this.rol = rol;
    }

    public int getPrateleira() {
        return prateleira;
    }

    public void setPrateleira(int prateleira) {
        this.prateleira = prateleira;
    }

    public boolean isRetirado() {
        return retirado;
    }

    public void setRetirado(boolean retirado) {
        this.retirado = retirado;
    }
}
