package com.example.rodolfo.controlepronto;

/**
 * Created by Rodolfo on 10/01/2017.
 */

public class Tapete {
    private int id;
    private long rol;
    private double metragem;
    private int posicao;

    public Tapete(long rol, double metragem, int posicao) {
        this.rol = rol;
        this.metragem = metragem;
        this.posicao = posicao;
    }

    public Tapete(int id, long rol, double metragem, int posicao) {
        this.id = id;
        this.rol = rol;
        this.metragem = metragem;
        this.posicao = posicao;
    }

    public String toString(){
        return "Tap. Rol:     "+this.rol+"    |   "+this.metragem+ "    m²   |  Pos.:  "+this.posicao;
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


    public int getPosicao() {
        return posicao;
    }

    public void setPosicao(int posicao) {
        this.posicao = posicao;
    }
}
