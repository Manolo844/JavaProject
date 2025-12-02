package com.schottenTotten.controller;

import com.schottenTotten.model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Jeu {
    private final List<Joueur> joueurs; //final car constante et définie une seule fois
    private final Borne[] bornes;
    private  Pioche pioche; //pas final car créé à l'init
    private Pioche piocheTactique;
    private List<Carte> defausse = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);



    private int tactiquesJoueesJoueur1 = 0;
    private int tactiquesJoueesJoueur2 = 0;
    private boolean varianteTactique;



    private int indexJoueurCourant;

    public Jeu() {
        this.joueurs = new ArrayList<>();
        this.bornes = new Borne[9];
        this.pioche = new Pioche(new ArrayList<>());
        this.piocheTactique = new Pioche(new ArrayList<>());
        

        this.indexJoueurCourant = 0;
    }

    public void initialisationJeu(String j1, String j2, boolean varianteTactique) {
        joueurs.add(new JoueurHumain(j1));
        joueurs.add(new JoueurHumain(j2));
        this.varianteTactique = varianteTactique;

        // création du paquet de cartes
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


        if (varianteTactique) {
            List<Carte> paquetTactique = new ArrayList<>();

            // Ajouter 2 jokers
            paquetTactique.add(new CarteTactique(TypeTactique.JOKER));
            paquetTactique.add(new CarteTactique(TypeTactique.JOKER));

            // Ajouter toutes les autres cartes tactiques une fois
            for (TypeTactique t : TypeTactique.values()) {
                if (t != TypeTactique.JOKER) {
                    paquetTactique.add(new CarteTactique(t));
                }
            }
          
            Collections.shuffle(paquetTactique);
            this.piocheTactique = new Pioche(paquetTactique);
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

        boolean estTactique = carteJouee instanceof CarteTactique;
        
         
        if (estTactique) {
            TypeTactique t = ((CarteTactique) carteJouee).getType();

            int tJ1 = tactiquesJoueesJoueur1;
            int tJ2 = tactiquesJoueesJoueur2;

            // Règle : interdiction de jouer plus d'une carte tactique de plus que l’adversaire
            if (indexJoueurCourant == 0 && tJ1 >= tJ2+1) {
                joueurActuel.ajouterCarte(carteJouee); // remettre la carte dans la main    
                throw new Exception("Vous avez déjà joué plus de cartes tactiques que l'adversaire !");
            }
            if (indexJoueurCourant == 1 && tJ2 >= tJ1+1) {
                joueurActuel.ajouterCarte(carteJouee);
                throw new Exception("Vous avez déjà joué plus de cartes tactiques que l'adversaire !");
            }

            //Si c'est une carte RUSE → NE SE POSE PAS SUR LA BORNE
            if (t == TypeTactique.CHASSEUR_DE_TETE ||t == TypeTactique.STRATEGE ||t == TypeTactique.BANSHEE ||t == TypeTactique.TRAITRE)
            {
                switch (t) {
                    case CHASSEUR_DE_TETE:
                        effetChasseurDeTete(joueurActuel);
                        break;

                    case STRATEGE:
                        effetStrategie(joueurActuel);
                        break;

                    case BANSHEE:
                        effetBanshee(joueurActuel);
                        break;

                    case TRAITRE:
                        effetTraitre(joueurActuel);
                        break;

                    default: break;
                }
                // mettre la carte en défausse
                defausse.add(carteJouee);

                // compter carte tactique jouée
                if (indexJoueurCourant == 0) tactiquesJoueesJoueur1++;
                else tactiquesJoueesJoueur2++;

                // FIN DU TOUR : NE PAS POSER LA CARTE SUR LA BORNE
                return;
            }  
            // Sinon → autres cartes tactiques (COLIN / BOUE)
            TypeTactique modeActuel = borne.getModeCombat();
            boolean estModeCombat = (t == TypeTactique.COLIN_MAILLARD || t == TypeTactique.COMBAT_DE_BOUE);

            if (modeActuel != null && estModeCombat) {
                joueurActuel.ajouterCarte(carteJouee);
                throw new Exception("Impossible : cette borne a déjà un mode de combat tactique actif !");
            }

        }

        try {
            borne.ajouterCartePourJoueur(indexJoueurCourant, carteJouee);
            if (estTactique) {
                TypeTactique t = ((CarteTactique) carteJouee).getType();
                if (t == TypeTactique.COLIN_MAILLARD || t == TypeTactique.COMBAT_DE_BOUE) {
                    borne.setModeCombat(t);
                }
            }
        } catch (IllegalStateException e) {
            joueurActuel.ajouterCarte(carteJouee);
            throw e;
        }

        if (estTactique) {
            if (indexJoueurCourant == 0) tactiquesJoueesJoueur1++;
            else tactiquesJoueesJoueur2++;
        }

        verifierRevendications();

        if (varianteTactique) {
            if (!pioche.estVide() && !piocheTactique.estVide()) {

                // === DEMANDE AU JOUEUR ===
                System.out.print("Voulez-vous piocher une carte tactique (T) ou normale (N) ? ");
                String choix = scanner.nextLine().trim().toUpperCase();

                if (choix.equals("T")) {
                    joueurActuel.ajouterCarte(piocheTactique.piocher());
                } else {
                    joueurActuel.ajouterCarte(pioche.piocher());
                }
            }
            else if (!piocheTactique.estVide()) {
                joueurActuel.ajouterCarte(piocheTactique.piocher());
            }
            else if (!pioche.estVide()) {
                joueurActuel.ajouterCarte(pioche.piocher());
            }
        }
        else {
            if (!pioche.estVide()) {
                joueurActuel.ajouterCarte(pioche.piocher());
            }
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

    
    private void effetChasseurDeTete(Joueur j) throws Exception {

        System.out.println("Chasseur de Tête : choisir la source de pioche :");
        System.out.println("1 = pioche normale (3 cartes)");
        System.out.println("2 = pioche tactique (3 cartes)");
        System.out.println("3 = choisir pour chaque carte (normal/tactique)");
        int mode = Integer.parseInt(scanner.nextLine());

        // 1) PIOCHER 3 CARTES
        for (int i = 0; i < 3; i++) {
            if (mode == 1) {
                if (!pioche.estVide()) j.ajouterCarte(pioche.piocher());
                continue;
            }
            if (mode == 2) {
                if (!piocheTactique.estVide()) j.ajouterCarte(piocheTactique.piocher());
                continue;
            }
            // mode == 3 : libre à chaque carte
            System.out.print("Piocher (N)ormale ou (T)actique ? ");
            String src = scanner.nextLine().trim().toUpperCase();

            if (src.equals("T") && !piocheTactique.estVide())
                j.ajouterCarte(piocheTactique.piocher());
            else if (!pioche.estVide())
                j.ajouterCarte(pioche.piocher());
        }

        // 2) REMETTRE 2 CARTES AUTOMATIQUEMENT SOUS LA BONNE PIOCHE
        System.out.println("\nSélectionnez 2 cartes à remettre SOUS la pioche correspondante :");

        for (int k = 0; k < 2; k++) {
            afficherMain(j);
            int idx = Integer.parseInt(scanner.nextLine());
            Carte c = j.retirerCarte(idx);

            if (c instanceof CarteTactique)
                piocheTactique.mettreSous(c);
            else
                pioche.mettreSous(c);

            System.out.println("→ Carte remise sous la " + (c instanceof CarteTactique ? "pioche tactique" : "pioche normale"));
        }
    }



    private void effetStrategie(Joueur j) throws Exception {

        System.out.println("Stratège : choisissez une Borne source :");
        int bSrc = Integer.parseInt(scanner.nextLine()) - 1;
        if (bornes[bSrc].estRevendiquee())
            throw new Exception("Impossible : cette Borne est déjà revendiquée !");


        System.out.println("Choisissez l’index de la carte à déplacer :");
        Borne src = bornes[bSrc];
        List<Carte> list = src.cartesJoueur(joueurs.indexOf(j));
        if (list.isEmpty())
            throw new Exception("Aucune carte à déplacer sur cette Borne !");


        afficherCartes(list);
        int idx = Integer.parseInt(scanner.nextLine());

        Carte c = list.remove(idx);

        System.out.println("1 = Déplacer vers autre Borne");
        System.out.println("2 = Défausser");
        int choix = Integer.parseInt(scanner.nextLine());

        if (choix == 2) {
            defausse.add(c);
            return;
        }

        System.out.println("Vers quelle Borne ?");
        int bDest = Integer.parseInt(scanner.nextLine()) - 1;
        if (bornes[bDest].estRevendiquee())
            throw new Exception("Impossible : la Borne cible est déjà revendiquée !");

        if (bornes[bDest].estComplete())
            throw new Exception("Impossible : cette Borne est déjà pleine !");

        bornes[bDest].ajouterCartePourJoueur(joueurs.indexOf(j), c);
    }


    private void effetBanshee(Joueur j) throws Exception {

        System.out.println("Banshee : choisissez une Borne cible :");
        int b = Integer.parseInt(scanner.nextLine()) - 1;

        Borne borne = bornes[b];
        int adv = (joueurs.indexOf(j) == 0 ? 1 : 0);

        List<Carte> list = borne.cartesJoueur(adv);
        if (list.isEmpty())
            throw new Exception("L’adversaire n’a aucune carte sur cette Borne !");

        afficherCartes(list);

        System.out.println("Choisir carte à défausser :");
        int idx = Integer.parseInt(scanner.nextLine());

        Carte c = list.remove(idx);
        defausse.add(c);
    }

    private void effetTraitre(Joueur j) throws Exception {

        System.out.println("Traître : choisir Borne source adverse :");
        int bSrc = Integer.parseInt(scanner.nextLine()) - 1;

        int adv = (joueurs.indexOf(j) == 0 ? 1 : 0);
        Borne borneSrc = bornes[bSrc];

        List<Carte> list = borneSrc.cartesJoueur(adv);
        if (list.isEmpty())
            throw new Exception("L’adversaire n’a aucune carte à voler !");

        afficherCartes(list);

        System.out.println("Carte à voler : ");
        int idx = Integer.parseInt(scanner.nextLine());
        Carte vol = list.remove(idx);

        System.out.println("Choisir Borne destination :");
        int bDest = Integer.parseInt(scanner.nextLine()) - 1;

        if (bornes[bDest].estComplete())
            throw new Exception("Impossible : cette Borne est déjà pleine !");

        bornes[bDest].ajouterCartePourJoueur(joueurs.indexOf(j), vol);
    }


    // Affiche la main d'un joueur avec index
    private void afficherMain(Joueur j) {
        List<Carte> main = j.getCartesJoueur();
        for (int i = 0; i < main.size(); i++) {
            System.out.println("[" + i + "] " + main.get(i));
        }
    }

    // Affiche une liste de cartes avec index
    private void afficherCartes(List<Carte> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + i + "] " + list.get(i));
        }
    }



}
