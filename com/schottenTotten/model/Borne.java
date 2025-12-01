package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public class Borne {

    private final int index; 
    private final List<Carte> cartesJoueur1; // numJoueur 0
    private final List<Carte> cartesJoueur2; // numJoueur 1
    private Joueur proprietaire ; // null au début de la partie

    // -1 tant que personne n'a déposé sa 3e carte
    private int premier_a_deposer_3eme_carte = -1;

    public Borne(int index) {
        this.index = index;
        this.cartesJoueur1 = new ArrayList<>();
        this.cartesJoueur2 = new ArrayList<>();
    }

    public int getIndex() {
        return index;
    }

    public Joueur getProprietaire() {
        return proprietaire;
    }

    public void setProprietaire(Joueur proprietaire) {
        this.proprietaire = proprietaire;
    }

    public List<Carte> getCartesPourJoueur(int numJoueur) {
        return numJoueur == 0 ? cartesJoueur1 : cartesJoueur2;
    }

    public boolean estRevendiquee() {
        return proprietaire != null;
    }

    public boolean estCompletePourJoueur(int numJoueur) {
        return getCartesPourJoueur(numJoueur).size() == 3;
    }

    public boolean estComplete() {
        return estCompletePourJoueur(0) && estCompletePourJoueur(1);
    }


    public void ajouterCartePourJoueur(int numJoueur, Carte carte) {
        if (estRevendiquee()) {
            throw new IllegalStateException("La borne est déjà revendiquée.");
        }
        if (getCartesPourJoueur(numJoueur).size() == 3) {
            throw new IllegalStateException("Le joueur a déjà déposé 3 cartes sur cette borne.");
        }

        List<Carte> liste = (numJoueur == 0) ? cartesJoueur1 : cartesJoueur2;
        liste.add(carte);

        // si ce joueur vient d'atteindre 3 cartes, on le mémorise
        if (liste.size() == 3 && premier_a_deposer_3eme_carte == -1) {
            premier_a_deposer_3eme_carte = numJoueur;
        }
    }

    public int determinerGagnantLocal() {
        if (!estComplete()) {
            throw new IllegalStateException("La borne n'est pas encore complète.");
        }

        CombinaisonBorne c1 = CombinaisonBorne.analyser(cartesJoueur1);
        CombinaisonBorne c2 = CombinaisonBorne.analyser(cartesJoueur2);

        int cmp = c1.compareTo(c2);
        if (cmp > 0) {
            return 0;
        } else if (cmp < 0) {
            return 1; 
        } else {
            // même type + même somme  :
            return premier_a_deposer_3eme_carte;  //celui qui a déposé sa 3e carte en premier GAGNE
        }
    }
}
