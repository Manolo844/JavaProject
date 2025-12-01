package com.schottenTotten.controller;

import com.schottenTotten.model.Joueur;
import com.schottentotten.model.*;
import java.util.ArrayList;
import java.util.List;

public class Jeu {
    private final List<Joueur> joueurs; //final car constante et définie une seule fois
    private final Borne[] bornes;
    private final Pioche pioche;

    private int indexJoueurCourant;

    public Jeu() {
        this.joueurs = new ArrayList<>();
        this.bornes = new Borne[9];
        this.pioche = new Pioche();
        this.indexJoueurCourant = 0;
    }

    public void initialisationJeu(String j1, String j2, boolean varianteTactique) {
        joueurs.add(new JoueurHumain(j1));
        joueurs.add(new JoueurHumain(j2));

        pioche.initialisation(varianteTactique); //revoir le nom dans Pioche

        for (int i = 0; i < 9; i++) {
            bornes[i] = new Borne(i+1);
        }

        int nbCartes = varianteTactique ? 7 : 6;
        for (int i = 0; i < nbCartes; i++) {  //distribution
            for (Joueur j : joueurs) {
                Carte c = pioche.piocher();  //revoir
                if (c != null) { 
                    j.ajouterCarte(c);
                }
            }
        }
    }

    public void jouerTour(int indexCarte, int indexBorne) {
        if (indexBorne < 0 || indexBorne >= 9) {
            throw new Exception("Borne invalide");
        }

        Joueur joueurActuel = getJoueurCourant();
        Borne borne = bornes[indexBorne];
        Carte carteJouee = joueurActuel.retirerCarte(indexCarte);
        
        if (carteJouee == null) {
             throw new Exception("Erreur: Carte introuvable ou index invalide");
        }

        try {
            borne.ajouterCarte(carteJouee, joueurActuel);
        } catch (Exception e) {
            joueurActuel.ajouterCarte(carteJouee);
            throw e;
        }

        if (!pioche.estVide()) {
            joueurActuel.ajouterCarte(pioche.piocher());
        }

        indexJoueurCourant = (indexJoueurCourant + 1) % 2;

    }

    public Joueur getJoueurCourant() {
        return joueurs.get(indexJoueurCourant);
    }

    public List<Joueur> getJoueurs() { 
        return joueurs;
     }

    public Borne[] getBornes() { 
        return bornes; 
    }
}
