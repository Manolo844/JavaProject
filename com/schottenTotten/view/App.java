package com.schottenTotten.view;

import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.Borne;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.Joueur;

import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Jeu jeu = new Jeu();

        System.out.println("=========================================");
        System.out.println("   SCHOTTEN TOTTEN - VERSION CONSOLE");
        System.out.println("=========================================");

        System.out.print("Entrez le nom du Joueur 1 : ");
        String nomJ1 = scanner.nextLine();
        System.out.print("Entrez le nom du Joueur 2 : ");
        String nomJ2 = scanner.nextLine();

        System.out.print("Activer la variante tactique ? (o/n) : ");
        String choixVariante = scanner.nextLine().trim().toLowerCase();

        boolean varianteTactique = choixVariante.equals("o");

        // Lancement du jeu avec ou sans variante
        jeu.initialisationJeu(nomJ1, nomJ2, varianteTactique);

        System.out.println("\nLa partie commence !");

        boolean partieTerminee = false;

        while (!partieTerminee) {
            // 1. Afficher l'état du jeu
            afficherPlateau(jeu);

            // 2. Identifier le joueur courant
            Joueur joueurActuel = jeu.getJoueurCourant();
            System.out.println("\n-----------------------------------------");
            System.out.println("TOUR DE : " + joueurActuel.getNom());
            
            // 3. Afficher sa main
            System.out.println("Votre main :");
            List<Carte> main = joueurActuel.getCartesJoueur();
            for (int i = 0; i < main.size(); i++) {
                System.out.println("  [" + i + "] " + main.get(i).toString());
            }

            // 4. Demander le coup
            try {
                System.out.print("\n> Quelle carte jouer (numéro index) ? ");
                String inputCarte = scanner.nextLine();
                int indexCarte = Integer.parseInt(inputCarte);

                System.out.print("> Sur quelle borne (1 à 9) ? ");
                String inputBorne = scanner.nextLine();
                int numBorne = Integer.parseInt(inputBorne);
                
                // Conversion 1-9 (utilisateur) vers 0-8 (tableau)
                int indexBorne = numBorne - 1;

                // 5. Exécuter le coup via le contrôleur
                jeu.jouerTour(indexCarte, indexBorne);

                // 6. Vérifier si quelqu'un a gagné
                Joueur gagnant = jeu.verifierVictoire();
                if (gagnant != null) {
                    System.out.println("\n=========================================");
                    System.out.println(" VICTOIRE !!! " + gagnant.getNom() + " remporte la partie !");
                    System.out.println("=========================================");
                    afficherPlateau(jeu); // Affichage final
                    partieTerminee = true;
                }

            } catch (NumberFormatException e) {
                System.out.println(">>> ERREUR : Veuillez entrer un chiffre valide !");
            } catch (Exception e) {
                System.out.println(">>> ERREUR JEU : " + e.getMessage());
                // On ne change pas de joueur, la boucle recommence
            }
        }
        scanner.close();
    }

    // Méthode pour afficher joliment les 9 bornes
    private static void afficherPlateau(Jeu jeu) {
        System.out.println("\n=== FRONTIÈRE ===");
        for (Borne b : jeu.getBornes()) {
            String j1Cartes = b.getCartesPourJoueur(0).toString();
            String j2Cartes = b.getCartesPourJoueur(1).toString();
            
            String etatBorne;
            if (b.estRevendiquee()) {
                etatBorne = "[ GAGNÉE PAR " + b.getProprietaire().getNom() + " ]";
            } else {
                etatBorne = "( Borne " + b.getIndex() + " )";
            }

            // Affichage : J1 [cartes] -- (Borne X) -- [cartes] J2
            System.out.printf("%-40s %-20s %s%n", 
                "J1 " + j1Cartes, 
                etatBorne, 
                "J2 " + j2Cartes);
        }
    }
}