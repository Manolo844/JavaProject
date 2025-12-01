package com.schottenTotten.model;

public class Carte {

    private final Couleur couleur;
    private final int valeur;

    public Carte(Couleur couleur, int valeur) {
        this.couleur = couleur;
        this.valeur = valeur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public int getValeur() {
        return valeur;
    }

    @Override
    public String toString() {
        return couleur + "-" + valeur;
    }
}
