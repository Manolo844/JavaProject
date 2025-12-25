package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections; 
import java.util.Comparator;  
import java.util.List;

public class Borne {

    private final int index; 
    private final List<Carte> cartesJoueur1; // numJoueur 0
    private final List<Carte> cartesJoueur2; // numJoueur 1
    private Joueur proprietaire ; // null au début de la partie
    private TypeTactique modeCombat = null; 

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
                throw new IllegalStateException("Borne pleine (Combat de Boue).");
            }
        } else {
            if (getCartesPourJoueur(numJoueur).size() == 3) {
                throw new IllegalStateException("Borne pleine.");
            }
        }

        List<Carte> liste = (numJoueur == 0) ? cartesJoueur1 : cartesJoueur2;
        liste.add(carte);

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
        }
        else if (cmp < 0) {
            return 1;
        }
        else {
            return premier_a_deposer_3eme_carte;
        } 
    }

    public void setModeCombat(TypeTactique t) { 
        this.modeCombat = t; 
    }
    
    public TypeTactique getModeCombat() { return modeCombat; }

    private int determinerGagnantSuivantSommeSeule() {
        int s1 = 0;
        for(Carte c : cartesJoueur1){
         s1 += c.getValeur();
        }
        
        int s2 = 0;
        for(Carte c : cartesJoueur2) {
            s2 += c.getValeur();
        }

        if (s1 > s2) {
            return 0;
        }
        if (s2 > s1) {
            return 1;
        }

        return premier_a_deposer_3eme_carte;
    }

    private int determinerGagnantCombatDeBoue() {
        List<Carte> j1best = get3Meilleures(cartesJoueur1);
        List<Carte> j2best = get3Meilleures(cartesJoueur2);

        CombinaisonBorne c1 = CombinaisonBorne.analyser(j1best);
        CombinaisonBorne c2 = CombinaisonBorne.analyser(j2best);

        int cmp = c1.compareTo(c2);
        if (cmp > 0) {
            return 0;
        }
        if (cmp < 0) {
            return 1;
        }
        return premier_a_deposer_3eme_carte;
    }

    private List<Carte> get3Meilleures(List<Carte> cartes) {
        List<Carte> copie = new ArrayList<>(cartes);
        
        Collections.sort(copie, new Comparator<Carte>() {
            @Override
            public int compare(Carte c1, Carte c2) {
                return c2.getValeur() - c1.getValeur(); 
            }
        });

        // on garde les 3 meilleures
        List<Carte> resultat = new ArrayList<>();
        for(int i=0; i<3 && i<copie.size(); i++) {
            resultat.add(copie.get(i));
        }
        return resultat;
    }
    
    public List<Carte> cartesJoueur(int indexJoueur) { 
       return getCartesPourJoueur(indexJoueur);
    }
}