package com.schottenTotten.view;

import java.util.Scanner;
import com.schottenTotten.model.Carte;
import com.schottenTotten.model.Joueur;
import java.util.List;

public class InteractionConsole {
    private static final Scanner scanner = new Scanner(System.in);

    public static void afficherMessage(String message) {
        System.out.println(message);
    }

    public static String demanderChoix(String message) {
        System.out.print(message);
        return scanner.nextLine().trim().toUpperCase();
    }

    public static int demanderEntier(String message) {
        System.out.print(message);
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1; 
        }
    }

    public static void afficherMain(Joueur j) {
        List<Carte> main = j.getCartesJoueur();
        for (int i = 0; i < main.size(); i++) {
            System.out.println("[" + i + "] " + main.get(i));
        }
    }

    public static void afficherCartes(List<Carte> list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println("[" + i + "] " + list.get(i));
        }
    }
}