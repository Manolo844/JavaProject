package com.schottenTotten.model;

public class CarteClan extends Carte{
    private Couleur couleur;
    private int valeur;

    public CarteClan(Couleur couleur, int valeur) {
        this.couleur = couleur;
        this.valeur = valeur;
    }

    @Override
    public Couleur getCouleur() {
        return couleur;
    }

    @Override
    public int getValeur() {
        return valeur;
    }

    @Override
    public String getNom() {
        return "CarteClan : " + couleur +", valeur : " + valeur;
    }
}
