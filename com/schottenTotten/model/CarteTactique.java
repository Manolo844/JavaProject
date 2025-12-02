package com.schottenTotten.model;

public class CarteTactique extends Carte {

    private TypeTactique type;

    public CarteTactique(TypeTactique type) {
        this.type = type;
    }

    public TypeTactique getType() {
        return type;
    }

    @Override
    public int getValeur() {
        return 0; 
    }

    @Override
    public String getNom() {
        return type.toString();
    }

    @Override
    public Couleur getCouleur() {
        return null;
    }

    
}