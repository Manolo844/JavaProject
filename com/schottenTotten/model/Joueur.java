package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public abstract class Joueur {

    private final String nom;
    protected final List<Carte> cartesJoueur;

    public Joueur(String nom) {
        this.nom = nom;
        this.cartesJoueur = new ArrayList<>();
    }

    public String getNom() {
        return nom;
    }

    public List<Carte> getCartesJoueur() {
        return new ArrayList<>(cartesJoueur);
    }

    public void ajouterCarte(Carte carte) {
        if (carte != null) {
            cartesJoueur.add(carte);
        }
    }

    public Carte retirerCarte(int index) throws Exception {
        if (index < 0 || index >= cartesJoueur.size()) {
            throw new Exception("Index invalide : impossible de jouer cette carte.");
        }
        return cartesJoueur.remove(index);
    }

    public boolean aEncoreDesCartes() {
        return !cartesJoueur.isEmpty();
    }

    @Override
    public String toString() {
        return nom;
    }
}
