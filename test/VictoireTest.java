/**
 * Validation des conditions de victoire du jeu.
 * On manipule le plateau pour donner artificiellement 5 bornes, 3 adjacentes à un joueur
 * et on s'assure que la méthode verifierVictoire() détecte bien la fin de partie.
 */

package test;
import org.junit.Test;
import static org.junit.Assert.*;
import com.schottenTotten.controller.Jeu;
import com.schottenTotten.model.*;

public class VictoireTest {
    @Test
    public void testVictoire5Bornes() {
        Jeu jeu = new Jeu();
        jeu.initialisationJeu("J1", "J2", false, 1);
        
        Joueur j1 = jeu.getJoueurs().get(0);
        Borne[] bornes = jeu.getBornes();

        bornes[0].setProprietaire(j1);
        bornes[2].setProprietaire(j1);
        bornes[4].setProprietaire(j1);
        bornes[6].setProprietaire(j1);
        bornes[8].setProprietaire(j1);

        assertEquals(j1, jeu.verifierVictoire());
    }

    @Test
    public void testVictoire3BornesAdjacentes() {
        Jeu jeu = new Jeu();
        jeu.initialisationJeu("J1", "J2", false, 1);
        
        Joueur j1 = jeu.getJoueurs().get(0);
        Borne[] bornes = jeu.getBornes();

        bornes[0].setProprietaire(j1);
        bornes[1].setProprietaire(j1);
        bornes[2].setProprietaire(j1);

        assertEquals("Le joueur doit gagner avec 3 bornes adjacentes", j1, jeu.verifierVictoire());
    }
}