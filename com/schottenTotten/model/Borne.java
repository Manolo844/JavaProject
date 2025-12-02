package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.List;

public class Borne {

    private final int index; 
    private final List<Carte> cartesJoueur1; // numJoueur 0
    private final List<Carte> cartesJoueur2; // numJoueur 1
    private Joueur proprietaire ; // null au début de la partie
    private TypeTactique modeCombat = null; // COLIN_MAILLARD ou COMBAT_DE_BOUE


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
        if (modeCombat == TypeTactique.COMBAT_DE_BOUE) {
            return getCartesPourJoueur(numJoueur).size() == 4;
        }

        return getCartesPourJoueur(numJoueur).size() == 3;
    }


    public boolean estComplete() {
        return estCompletePourJoueur(0) && estCompletePourJoueur(1);
    }


    public void ajouterCartePourJoueur(int numJoueur, Carte carte) {
        if (estRevendiquee()) {
            throw new IllegalStateException("La borne est déjà revendiquée.");
        }
        if (modeCombat == TypeTactique.COMBAT_DE_BOUE) {
            if (getCartesPourJoueur(numJoueur).size() == 4) {
                throw new IllegalStateException("Vous avez déjà déposé 4 cartes sur cette borne (Combat de Boue).");
            }
        } else {
            if (getCartesPourJoueur(numJoueur).size() == 3) {
                throw new IllegalStateException("Vous avez déjà déposé 3 cartes sur cette borne.");
            }
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

        TypeTactique mode = this.modeCombat;

        if (mode == TypeTactique.COLIN_MAILLARD) {
            return determinerGagnantSuivantSommeSeule();
        }

        if (mode == TypeTactique.COMBAT_DE_BOUE) {
            return determinerGagnantCombatDeBoue();
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

    public void setModeCombat(TypeTactique t) {
        this.modeCombat = t;
    }

    public TypeTactique getModeCombat() {
        return modeCombat;
    }

    private int determinerGagnantSuivantSommeSeule() {
        int s1 = cartesJoueur1.stream().mapToInt(Carte::getValeur).sum();
        int s2 = cartesJoueur2.stream().mapToInt(Carte::getValeur).sum();

        if (s1 > s2) return 0;
        if (s2 > s1) return 1;

        return premier_a_deposer_3eme_carte;
    }

    private int determinerGagnantCombatDeBoue() {
        // prendre les trois plus fortes cartes
        List<Carte> j1best = get3Meilleures(cartesJoueur1);
        List<Carte> j2best = get3Meilleures(cartesJoueur2);

        CombinaisonBorne c1 = CombinaisonBorne.analyser(j1best);
        CombinaisonBorne c2 = CombinaisonBorne.analyser(j2best);

        int cmp = c1.compareTo(c2);

        if (cmp > 0) return 0;
        if (cmp < 0) return 1;

        return premier_a_deposer_3eme_carte;
    }

    private List<Carte> get3Meilleures(List<Carte> cartes) {
        return cartes.stream().sorted((a, b) -> b.getValeur() - a.getValeur()).limit(3).toList();
    }

    public List<Carte> cartesJoueur(int indexJoueur) {
        if (indexJoueur == 0) return cartesJoueur1;
        else return cartesJoueur2;
    }


}
