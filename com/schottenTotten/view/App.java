package com.schottenTotten.view;

import com.schottenTotten.ai.*;
import com.schottenTotten.controller.JeuFactory;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.*;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Jeu jeu = new Jeu();

        System.out.println("=========================================");
        System.out.println("   SCHOTTEN TOTTEN - IA & TACTIQUE");
        System.out.println("=========================================");

        System.out.print("Nom du Joueur 1 (Vous) : ");
        String nomJ1 = scanner.nextLine();
        
        String nomJ2 = "";
        
        while(true) {
            System.out.println("Qui sera le Joueur 2 ?");
            System.out.println("1. Une IA");
            System.out.println("2. Un joueur humain");
            System.out.print("Votre choix (1 ou 2) : ");
            
            String choixAdv = scanner.nextLine().trim();
            
            if (choixAdv.equals("1")) {
                nomJ2 = "IA"; 
                break;
            } else if (choixAdv.equals("2")) {
                System.out.print("Entrez le nom du Joueur 2 : ");
                nomJ2 = scanner.nextLine();
                if (nomJ2.equalsIgnoreCase("IA")) {
                    nomJ2 = "Joueur 2"; 
                }
                break;
            } else {
                System.out.println("Choix invalide, réessayez.");
            }
        }

        System.out.print("Activer la variante tactique ? (o/n) : ");
        String repVar = scanner.nextLine();
        boolean variante = repVar.equalsIgnoreCase("o");

        boolean j2EstIA = nomJ2.equals("IA");
        jeu = JeuFactory.creerPartie(variante, nomJ1, nomJ2, j2EstIA);
        System.out.println("\nLa partie commence!");

        boolean partieTerminee = false;

        while (!partieTerminee) {
            afficherPlateau(jeu);

            Joueur joueurActuel = jeu.getJoueurCourant();
            
            //IA
            if (joueurActuel instanceof JoueurIA) {
                System.out.println("\n--- TOUR DE L'IA (" + joueurActuel.getNom() + ") ---");
                try {
                    jeu.jouerTourIA(); 
                } catch (Exception e) {
                    System.out.println("Erreur IA : " + e.getMessage());
                    jeu.finirTour(); 
                }
            } 
            
            // humain
            else {
                System.out.println("\n--- C'est à " + joueurActuel.getNom() + " de jouer ---");
                
                System.out.println("Votre main :");
                List<Carte> main = joueurActuel.getCartesJoueur();
                for (int i = 0; i < main.size(); i++) {
                    System.out.println("  [" + i + "] " + main.get(i).toString());
                }

                try {
                    System.out.print("\n> Quelle carte jouer (index) ? ");
                    int cIdx = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Sur quelle borne (1-9) ? ");
                    int bIdx = Integer.parseInt(scanner.nextLine()) - 1;
                    
                    jeu.poserCarte(cIdx, bIdx);
                    System.out.println("-> Carte jouée.");
                    
                    afficherPlateau(jeu); 

                    // revendication
                    while(true) {
                        System.out.print("Voulez-vous revendiquer une borne ? (o/n) : ");
                        String rep = scanner.nextLine();
                        if (!rep.equalsIgnoreCase("o")){
                            break;
                        } 

                        System.out.print("Quelle borne (1-9) ? ");
                        int bRev = Integer.parseInt(scanner.nextLine()) - 1;
                        try {
                            boolean gagne = jeu.revendiquerBorne(bRev);
                            if (gagne){
                                System.out.println(">>> SUCCÈS ! Borne capturée !");
                            } 
                            else {
                                System.out.println(">>> PERDU ! La borne est à l'adversaire.");
                            } 
                        } catch(Exception e) { 
                            System.out.println(">>> Impossible : " + e.getMessage()); 
                        }
                    }
                    jeu.finirTour();
                    
                } catch (NumberFormatException e) {
                    System.out.println(">>> Erreur : Veuillez entrer un nombre valide.");
                } catch (Exception e) {
                    System.out.println(">>> Erreur de jeu : " + e.getMessage());
                    System.out.println(">>> Recommencez votre tour.");
                }
            }

            Joueur grandGagnant = jeu.verifierVictoire();
            if (grandGagnant != null) {
                System.out.println("\n=========================================");
                System.out.println(" VICTOIRE FINALE DE " + grandGagnant.getNom());
                System.out.println("=========================================");
                partieTerminee = true;
            }
        }
        scanner.close();
    }

    private static void afficherPlateau(Jeu jeu) {
        System.out.println("\n=== FRONTIÈRE ===");
        for (Borne b : jeu.getBornes()) {
            String j1Cartes = b.getCartesPourJoueur(0).toString();
            String j2Cartes = b.getCartesPourJoueur(1).toString();
            
            String etatBorne;
            if (b.estRevendiquee()) {
                etatBorne = "[ GAGNÉE PAR " + b.getProprietaire().getNom() + " ]";
            } else {
                String mode = b.getModeCombat() != null ? " (" + b.getModeCombat() + ")" : "";
                etatBorne = "( Borne " + b.getIndex() + mode + " )";
            }

            System.out.printf("%-40s %-30s %s%n", 
                "J1 " + j1Cartes, 
                etatBorne, 
                "J2 " + j2Cartes);
        }
    }
}