/**
 * Test unitaire de la logique de résolution des bornes.
 * On simule des situations de conflit pour vérifier
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
    
    @Test
    public void testSuiteCouleurBatBrelan() {
        Borne borne = new Borne(2);
        
        // brelan de 9 
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.ROUGE, 9));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.VERT, 9));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.BLEU, 9));

        // suite-couleur (1, 2, 3 ROUGE) 
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.ROUGE, 1));
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.ROUGE, 2));
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.ROUGE, 3));

        assertTrue(borne.estComplete());
        assertEquals(1, borne.determinerGagnantLocal());
    }
    
    @Test
    public void testEgaliteParfaitePremierArrive() {
        Borne borne = new Borne(3);

        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.ROUGE, 5));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.VERT, 7));
        borne.ajouterCartePourJoueur(0, new CarteClan(Couleur.BLEU, 0)); 
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.JAUNE, 5));
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.MAUVE, 7));
        borne.ajouterCartePourJoueur(1, new CarteClan(Couleur.MARRON, 0)); 

        assertTrue(borne.estComplete());
        assertEquals(0, borne.determinerGagnantLocal());
    }
    
    
    
}
