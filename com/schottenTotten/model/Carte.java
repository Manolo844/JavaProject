package com.schottenTotten.model;

public abstract class Carte{ // abstract car ne peut pas etre instanciée, juste sous classée
    public Carte(){}

    public abstract String getNom();
    public abstract int getValeur();
    public abstract Couleur getCouleur();

    @Override
    public String toString(){
        return getNom();
    }
 
}