package com.schottenTotten.controller;

import com.schottenTotten.view.InteractionConsole;

public class JeuFactory {

    public static Jeu creerPartie(boolean modeTactique, String nomJ1, String nomJ2, boolean j2EstIA) {
        Jeu jeu = new Jeu();

        int niveauIA = 1; 
        String nomJoueur2Final = nomJ2;

        if (j2EstIA) {
            nomJoueur2Final = "IA";
            niveauIA = InteractionConsole.demanderEntier("Niveau de difficulté de l'IA (1 : facile, 2 : difficile) : ");
        }
        jeu.initialisationJeu(nomJ1, nomJoueur2Final, modeTactique, niveauIA);
        
        return jeu;
    }
}
