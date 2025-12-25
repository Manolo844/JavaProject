/**
 * Test unitaire de la logique de résolution des bornes.
 * On simule une situation de conflit (ici un Brelan contre une Somme) pour vérifier
 * que l'algorithme désigne le bon vainqueur et verrouille la borne.
 */

package test;
import org.junit.Test;
import static org.junit.Assert.*;
import com.schottenTotten.model.*;

public class BorneTest {
    @Test
    public void testBrelanBatSomme() {
        Borne borne = new Borne(1);
        // brelan de 5
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.ROUGE, 5));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.VERT, 5));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.BLEU, 5));
        
        // somme (9, 8, 1 sans suite)
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.ROUGE, 9));
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.ROUGE, 8)); 
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.BLEU, 1));

        assertTrue(borne.estComplete());
        assertEquals(0, borne.determinerGagnantLocal());
    }
}
