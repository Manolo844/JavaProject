package com.schottenTotten.controller;

import com.schottenTotten.ai.*;
import com.schottenTotten.model.*;
import com.schottenTotten.view.InteractionConsole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Jeu {
    private final List<Joueur> joueurs;
    private final Borne[] bornes;
    private Pioche pioche;
    private Pioche piocheTactique; 
    private List<Carte> defausse = new ArrayList<>(); 
    private int indexJoueurCourant;
    
    // pas jouer plus d'une tactique de plus que l'adversaire
    private int tactiquesJoueesJoueur1 = 0;
    private int tactiquesJoueesJoueur2 = 0;
    private boolean varianteTactique;

    public Jeu() {
        this.joueurs = new ArrayList<>();
        this.bornes = new Borne[9];
        this.pioche = new Pioche(new ArrayList<>());
        this.piocheTactique = new Pioche(new ArrayList<>());
        this.indexJoueurCourant = 0;
    }

    public void initialisationJeu(String j1, String j2, boolean varianteTactique, int niveauIA) {
        this.varianteTactique = varianteTactique;
        joueurs.clear();
        joueurs.add(new JoueurHumain(j1));
        
        if (j2.equals("IA")) {
        //modularité pour l'ajout d'IA avec des niveaux différents
            switch (niveauIA) {
                case 1:
                    joueurs.add(new JoueurIA("IA (Niveau 1)")); //IA qui joue random
                    break;
                case 2:
                    joueurs.add(new JoueurIAAvance("IA Avancée")); //IA qui joue selon une stratégie
                    break;
                //dans le futur, on peut ajouter d'autres case avec d'autres IA plus avancées
                default:
                    joueurs.add(new JoueurIA("IA"));
            }
        } else {
            joueurs.add(new JoueurHumain(j2));
        }

        // paquet Clan
        List<Carte> paquet = new ArrayList<>();
        for (Couleur c : Couleur.values()) {
            for (int i = 1; i <= 9; i++) {
                paquet.add(new CarteClan(c, i));
            }
        }
        Collections.shuffle(paquet);
        this.pioche = new Pioche(paquet);

        // paquet Tactique (si activé)
        if (varianteTactique) {
            List<Carte> paquetTactique = new ArrayList<>();
            paquetTactique.add(new CarteTactique(TypeTactique.JOKER));
            paquetTactique.add(new CarteTactique(TypeTactique.JOKER));
            for (TypeTactique t : TypeTactique.values()) {
                if (t != TypeTactique.JOKER) {
                    paquetTactique.add(new CarteTactique(t));
                }
            }
            Collections.shuffle(paquetTactique);
            this.piocheTactique = new Pioche(paquetTactique);
        }

        // bornes
        for (int i = 0; i < 9; i++) {
            bornes[i] = new Borne(i + 1);
        }

        // distribution
        int nbCartes = varianteTactique ? 7 : 6;
        for (int i = 0; i < nbCartes; i++) {
            for (Joueur j : joueurs) {
                Carte c = pioche.piocher();
                if (c != null) j.ajouterCarte(c);
            }
        }
    }

    public void poserCarte(int indexCarte, int indexBorne) throws Exception {
        if (indexBorne < 0 || indexBorne >= 9) throw new Exception("Borne invalide");

        Joueur joueurActuel = getJoueurCourant();
        Borne borne = bornes[indexBorne];
        Carte carteJouee = joueurActuel.retirerCarte(indexCarte);
        
        if (carteJouee == null) throw new Exception("Erreur: Carte introuvable");

        boolean estTactique = carteJouee instanceof CarteTactique;

        // gestion des règles tactiques
        if (estTactique) {
            TypeTactique t = ((CarteTactique) carteJouee).getType();
            int tJ1 = tactiquesJoueesJoueur1;
            int tJ2 = tactiquesJoueesJoueur2;

            // on ne peut pas jouer plus d'une tactique de plus que l'adversaire
            if (indexJoueurCourant == 0 && tJ1 >= tJ2 + 1) {
                joueurActuel.ajouterCarte(carteJouee);
                throw new Exception("Trop de cartes tactiques jouées par rapport à l'adversaire !");
            }
            if (indexJoueurCourant == 1 && tJ2 >= tJ1 + 1) {
                joueurActuel.ajouterCarte(carteJouee);
                throw new Exception("Trop de cartes tactiques jouées par rapport à l'adversaire !");
            }

            // les Ruses ne se posent pas sur la borne
            if (t == TypeTactique.CHASSEUR_DE_TETE || t == TypeTactique.STRATEGE || 
                t == TypeTactique.BANSHEE || t == TypeTactique.TRAITRE) {
                
                executerEffetRuse(t, joueurActuel);
                
                defausse.add(carteJouee);
                incrementerCompteurTactique();
                return; // le tour continue vers la pioche
            }

            // gestion modes de combat
            boolean estModeCombat = (t == TypeTactique.COLIN_MAILLARD || t == TypeTactique.COMBAT_DE_BOUE);
            if (borne.getModeCombat() != null && estModeCombat) {
                joueurActuel.ajouterCarte(carteJouee);
                throw new Exception("Cette borne a déjà un mode de combat !");
            }
        }

        try {
            borne.ajouterCartePourJoueur(indexJoueurCourant, carteJouee);
            
            // activation du mode de combat si c'en est un
            if (estTactique) {
                TypeTactique t = ((CarteTactique) carteJouee).getType();
                if (t == TypeTactique.COLIN_MAILLARD || t == TypeTactique.COMBAT_DE_BOUE) {
                    borne.setModeCombat(t);
                }
                incrementerCompteurTactique();
            }
        } catch (IllegalStateException e) {
            joueurActuel.ajouterCarte(carteJouee); // on rend la carte en cas d'erreur
            throw e;
        }
    }

    private void incrementerCompteurTactique() {
        if (indexJoueurCourant == 0) tactiquesJoueesJoueur1++;
        else tactiquesJoueesJoueur2++;
    }

    private void executerEffetRuse(TypeTactique t, Joueur j) throws Exception {
        if (j instanceof JoueurIA) {
            System.out.println("L'IA joue " + t + " mais l'effet complexe est ignoré (pour l'instant, pour simplifier).");
            return;
        }

        // si c'est un humain
        switch (t) {
            case CHASSEUR_DE_TETE: effetChasseurDeTete(j); break;
            case STRATEGE: effetStrategie(j); break;
            case BANSHEE: effetBanshee(j); break;
            case TRAITRE: effetTraitre(j); break;
            default: break;
        }
    }

    public boolean revendiquerBorne(int indexBorne) throws Exception {
        if (indexBorne < 0 || indexBorne >= 9) throw new Exception("Borne invalide");
        Borne b = bornes[indexBorne];
        
        if (b.estRevendiquee()) throw new Exception("Borne déjà prise !");

        if (b.estComplete()) {
            int indexGagnant = b.determinerGagnantLocal(); 
            b.setProprietaire(joueurs.get(indexGagnant));
            return indexGagnant == indexJoueurCourant;
        } else {
            throw new Exception("Borne incomplète ou indécidable.");
        }
    }

    public void finirTour() {
        Joueur joueurActuel = getJoueurCourant();
        
        if (varianteTactique) {
            if (!pioche.estVide() && !piocheTactique.estVide()) {
                if (joueurActuel instanceof JoueurIA) {
                    // IA pioche au hasard 
                    if (Math.random() > 0.8) {
                        joueurActuel.ajouterCarte(piocheTactique.piocher());
                    }
                    else {
                        joueurActuel.ajouterCarte(pioche.piocher());
                    }
                } else {
                    // humain
                    String choix = InteractionConsole.demanderChoix("Piocher : (N)ormale ou (T)actique ? ");
                    if (choix.equals("T")){
                         joueurActuel.ajouterCarte(piocheTactique.piocher());
                    }
                    else {
                        joueurActuel.ajouterCarte(pioche.piocher());
                    }
                }
            } else if (!piocheTactique.estVide()) {
                joueurActuel.ajouterCarte(piocheTactique.piocher());
            } else if (!pioche.estVide()) {
                joueurActuel.ajouterCarte(pioche.piocher());
            }
        } else {
            // version classique
            if (!pioche.estVide()) {
                joueurActuel.ajouterCarte(pioche.piocher());
            }
        }
        
        indexJoueurCourant = (indexJoueurCourant + 1) % 2;
    }

    public void jouerTourIA() throws Exception {
        JoueurIA joueurIA = (JoueurIA) getJoueurCourant();

        //compteurs tactiques
        int mesTactiques = (indexJoueurCourant == 0) ? tactiquesJoueesJoueur1 : tactiquesJoueesJoueur2;
        int sesTactiques = (indexJoueurCourant == 0) ? tactiquesJoueesJoueur2 : tactiquesJoueesJoueur1;
        int[] coup = joueurIA.reflechirCoup(bornes, indexJoueurCourant, mesTactiques, sesTactiques);
        
        if (coup != null) {
            poserCarte(coup[0], coup[1]);
            System.out.println("IA joue sur borne " + (coup[1]+1));
            
            // tentative de revendication
            for(int i=0; i<9; i++) {
                try {
                    if(!bornes[i].estRevendiquee() && bornes[i].estComplete()) {
                       boolean g = revendiquerBorne(i);
                       if(g) {
                        System.out.println(" IA gagne la borne " + (i+1));
                       }
                    }
                } catch(Exception e) {}
            }
        }
        else { //cas critique IA bloquée
            if (!joueurIA.getCartesJoueur().isEmpty()) {
                Carte c = joueurIA.retirerCarte(0); 
                defausse.add(c);
            } else {
                System.out.println("L'IA n'a plus de cartes et ne peut pas jouer.");
            }
        }
        finirTour(); 
    }
    
    private void effetChasseurDeTete(Joueur j) {
            InteractionConsole.afficherMessage("Chasseur de Tête (Auto 3 cartes normales pour aller vite, puis défausse)");

            for(int i=0; i<3 && !pioche.estVide(); i++) j.ajouterCarte(pioche.piocher());
            
            InteractionConsole.afficherMessage("Remettre 2 cartes sous la pioche (Entrez index) :");
            for(int k=0; k<2; k++) {
                InteractionConsole.afficherMain(j);
                try {
                    int idx = InteractionConsole.demanderEntier("> Choix : ");
                    Carte c = j.retirerCarte(idx);
                    if(c instanceof CarteTactique){
                        piocheTactique.mettreSous(c);
                    } 
                    else{
                        pioche.mettreSous(c);
                    }
                } catch(Exception e) { k--; }
            }
        }

    private void effetStrategie(Joueur j) throws Exception {
        int bSrc = InteractionConsole.demanderEntier("Borne Source (1-9): ") - 1;
        Borne src = bornes[bSrc];
        List<Carte> list = src.getCartesPourJoueur(joueurs.indexOf(j));
        
        InteractionConsole.afficherCartes(list);
        int idx = InteractionConsole.demanderEntier("Index carte à bouger: ");
        Carte c = list.remove(idx);
        
        int dest = InteractionConsole.demanderEntier("Destination (1-9) ou 0 pour défausser: ");
        if(dest == 0){
            defausse.add(c);
        } 
        else{
            bornes[dest-1].ajouterCartePourJoueur(joueurs.indexOf(j), c);
        } 
    }
    
    private void effetBanshee(Joueur j) throws Exception {
        int b = InteractionConsole.demanderEntier("Borne Cible Adversaire (1-9): ") - 1;
        int adv = (joueurs.indexOf(j) == 0 ? 1 : 0);
        List<Carte> list = bornes[b].getCartesPourJoueur(adv);
        
        InteractionConsole.afficherCartes(list);
        int idx = InteractionConsole.demanderEntier("Index carte à défausser: ");
        Carte c = list.remove(idx);
        defausse.add(c);
    }

    private void effetTraitre(Joueur j) throws Exception {
        int bSrc = InteractionConsole.demanderEntier("Borne Source Adversaire (1-9): ") - 1;
        int adv = (joueurs.indexOf(j) == 0 ? 1 : 0);
        List<Carte> list = bornes[bSrc].getCartesPourJoueur(adv);
        
        InteractionConsole.afficherCartes(list);
        int idx = InteractionConsole.demanderEntier("Index carte à voler: ");
        Carte c = list.remove(idx);
        
        int bDest = InteractionConsole.demanderEntier("Borne Destination chez vous (1-9): ") - 1;
        bornes[bDest].ajouterCartePourJoueur(joueurs.indexOf(j), c);
    }

    public Joueur getJoueurCourant() { 
        return joueurs.get(indexJoueurCourant); 
    }
    
    public Borne[] getBornes() { 
        return bornes; 
    }

    public List<Joueur> getJoueurs() {
        return joueurs;
    }
    
    public Joueur verifierVictoire() {
        int bornesJ1 = 0; int bornesJ2 = 0;
        for (Borne b : bornes) {
            if (b.getProprietaire() == joueurs.get(0)){
                bornesJ1++;
            } 
            if (b.getProprietaire() == joueurs.get(1)){
                bornesJ2++;
            } 
        }
        if (bornesJ1 >= 5){
            return joueurs.get(0);
        } 
        if (bornesJ2 >= 5){
            return joueurs.get(1);
        } 

        int suiteJ1 = 0; int suiteJ2 = 0;
        for (Borne b : bornes) {
            if (b.getProprietaire() == joueurs.get(0)) { 
                suiteJ1++; suiteJ2 = 0; 
            }
            else if (b.getProprietaire() == joueurs.get(1)) { 
                suiteJ2++; suiteJ1 = 0; 
            }
            else { 
                suiteJ1 = 0; suiteJ2 = 0; 
            }
            if (suiteJ1 >= 3) {
                return joueurs.get(0);
            }
            if (suiteJ2 >= 3){
                return joueurs.get(1);
            }
        }
        return null;
    }
}