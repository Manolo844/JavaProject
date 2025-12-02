package com.schottenTotten.controller;

import com.schottenTotten.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jeu {
    private final List<Joueur> joueurs; //final car constante et définie une seule fois
    private final Borne[] bornes;
    private  Pioche pioche; //pas final car créé à l'init

    private int indexJoueurCourant;

    public Jeu() {
        this.joueurs = new ArrayList<>();
        this.bornes = new Borne[9];
        this.pioche = new Pioche(new ArrayList<>());
        this.indexJoueurCourant = 0;
    }

    public void initialisationJeu(String j1, String j2, boolean varianteTactique) {
        joueurs.add(new JoueurHumain(j1));
        joueurs.add(new JoueurHumain(j2));

        List<Carte> paquet = new ArrayList<>();
        for (Couleur c : Couleur.values()) {
            for (int i = 1; i <= 9; i++) {
                paquet.add(new CarteClan(c, i));
            }
        }
        Collections.shuffle(paquet);

        this.pioche = new Pioche(paquet);

        for (int i = 0; i < 9; i++) {
            bornes[i] = new Borne(i+1);
        }

        int nbCartes = varianteTactique ? 7 : 6;
        for (int i = 0; i < nbCartes; i++) {  //distribution
            for (Joueur j : joueurs) {
                Carte c = pioche.piocher();  
                if (c != null) { 
                    j.ajouterCarte(c);
                }
            }
        }
    }

    public void jouerTour(int indexCarte, int indexBorne) throws Exception{
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
            borne.ajouterCartePourJoueur(indexJoueurCourant, carteJouee);
        } catch (IllegalStateException e) {
            joueurActuel.ajouterCarte(carteJouee);
            throw e;
        }

        verifierRevendications();

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

    private void verifierRevendications() {
        for (Borne b : bornes) {
            if (!b.estRevendiquee() && b.estComplete()) {
                int gagnantIndex = b.determinerGagnantLocal(); 
                
                Joueur gagnant = joueurs.get(gagnantIndex);
                b.setProprietaire(gagnant);
                
                System.out.println(">>> LA BORNE " + b.getIndex() + " EST GAGNÉE PAR " + gagnant.getNom() + " !");
            }
        }
    }

    public Joueur verifierVictoire() {
        int bornesJ1 = 0;
        int bornesJ2 = 0;

        for (Borne b : bornes) {
            if (b.getProprietaire() == joueurs.get(0)) bornesJ1++;
            if (b.getProprietaire() == joueurs.get(1)) bornesJ2++;
        }

        if (bornesJ1 >= 5) return joueurs.get(0);
        if (bornesJ2 >= 5) return joueurs.get(1);

        int suiteJ1 = 0;
        int suiteJ2 = 0;

        for (Borne b : bornes) {
            if (b.getProprietaire() == joueurs.get(0)) {
                suiteJ1++;
                suiteJ2 = 0;
            } else if (b.getProprietaire() == joueurs.get(1)) {
                suiteJ2++;
                suiteJ1 = 0;
            } else { 
                suiteJ1 = 0;
                suiteJ2 = 0;
            }

            if (suiteJ1 >= 3) return joueurs.get(0);
            if (suiteJ2 >= 3) return joueurs.get(1);
        }

        return null; 
    }
}
