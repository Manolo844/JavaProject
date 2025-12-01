package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Joueur {

    private final String nom;
    protected final List<Carte> CartesJoueur;

    public Joueur(String nom) {
        this.nom = nom;
        this.CartesJoueur = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public List<Carte> getCartesJoueur() {
        return CartesJoueur;
    }

    public void ajouterCarte(Carte carte) {
        CartesJoueur.add(carte);
    }

    public Carte retirerCarte(int index) {
        return CartesJoueur.remove(index);
    }

    public boolean aEncoreDesCartes() {
        return !CartesJoueur.isEmpty();
    }

    @Override
    public String toString() {
        return nom;
    }
}
