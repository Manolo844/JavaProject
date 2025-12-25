/* Joueur IA, random par défaut */

package com.schottenTotten.ai;

import com.schottenTotten.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JoueurIA extends Joueur {

    private final Random random;

    public JoueurIA(String nom) {
        super(nom);
        this.random = new Random();
    }


    public int[] reflechirCoup(Borne[] bornes, int monIndex, int tactiquesMoi, int tactiquesAutre) {
    
        List<Integer> bornesValides = new ArrayList<>();
        for (int i = 0; i < bornes.length; i++) {
            Borne b = bornes[i];
            if (!b.estRevendiquee() && !b.estCompletePourJoueur(monIndex)) {
                bornesValides.add(i);
            }
        }

        if (bornesValides.isEmpty() || cartesJoueur.isEmpty()) {
            return null; 
        }

        //logique de gestion d'erreurs, pour éviter le blocage du plateau par l'IA
        for(int k=0; k<100; k++) {
            int iCarte = random.nextInt(cartesJoueur.size());
            Carte c = cartesJoueur.get(iCarte);
            int iBorne = bornesValides.get(random.nextInt(bornesValides.size()));

            // gestion d'erreur
            if (c instanceof CarteTactique) {
                if (tactiquesMoi >= tactiquesAutre + 1) {
                    continue; // on n'a pas le droit, on cherche une autre carte
                }
            }
            
            return new int[]{iCarte, iBorne};
        }

    return null;
    }
}
