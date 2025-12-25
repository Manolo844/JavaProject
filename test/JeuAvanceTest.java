/**
 * Tests unitaires des fonctionnalités avancées et de la robustesse.
 * On vérifie ici que l'initialisation du mode Tactique est correcte (7 cartes) et
 * on s'assure que l'IA est capable de calculer un coup sans provoquer d'erreur.
 */

package test; 

import org.junit.Test;
import static org.junit.Assert.*;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.*;
import com.schottenTotten.ai.JoueurIA;

public class JeuAvanceTest {

    @Test
    public void testInitialisationModeTactique() {
        Jeu jeu = new Jeu();
        //mode tactique = true
        jeu.initialisationJeu("J1", "J2", true, 1);
        
        Joueur j1 = jeu.getJoueurs().get(0);
        // vérifie qu'on a bien 7 cartes
        assertEquals(7, j1.getCartesJoueur().size());
    }

    @Test
    public void testIA_NePlantePas() {
        Jeu jeu = new Jeu();
        jeu.initialisationJeu("Humain", "IA", false, 2); 
        
        JoueurIA ia = null;
        for (Joueur j : jeu.getJoueurs()) {
            if (j instanceof JoueurIA) {
                ia = (JoueurIA) j;
                break;
            }
        }

        assertNotNull("Le jeu aurait dû créer au moins une IA avec le niveau 2 !", ia);
        
        Borne[] bornes = jeu.getBornes();
        int[] coup = ia.reflechirCoup(bornes, 1, 0, 0);
        
        assertNotNull("L'IA doit trouver un coup au début du jeu", coup);
    }

    @Test
    public void testCarteTactiqueSurBorne() {
        Borne borne = new Borne(1);
        TypeTactique unTypeAuHasard = TypeTactique.values()[0];

        Carte carteTest = new CarteTactique(unTypeAuHasard);

        borne.ajouterCartePourJoueur(0, carteTest);
        
        assertEquals(1, borne.getCartesPourJoueur(0).size());
        assertTrue(borne.getCartesPourJoueur(0).get(0) instanceof CarteTactique);
    }
}
