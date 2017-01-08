package com.example.rodolfo.controlepronto;

/**
 * Created by Rodolfo on 07/01/2017.
 */

public class Edredom {
    private int id;
    private long rol;
    private int prateleira;
    private int retirado;

    public Edredom(long rol, int prateleira){
        this.rol = rol;
        this.prateleira = prateleira;
        this.retirado = 0;
    }

    public Edredom(int id, long rol, int prateleira, int retirado){
        this.id = id;
        this.rol = rol;
        this.prateleira = prateleira;
        this.retirado = retirado;
    }

    public String toString(){
        return "Edredom ROL:    "+this.rol + "     |     Prateleira:     " + this.prateleira;
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

    public int getRetirado() {
        return retirado;
    }

    public void setRetirado(int retirado) {
        this.retirado = retirado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
