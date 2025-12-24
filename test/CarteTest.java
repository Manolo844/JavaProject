/**
 * Test unitaireepour la classe Carte.
 * On vérifie que l'instanciation d'une carte fonctionne bien et que
 * les getters renvoient correctement la couleur et la valeur associées.
 */

package test;
import org.junit.Test;
import static org.junit.Assert.*;
import com.schottenTotten.model.*;

public class CarteTest {
    @Test
    public void testCreationCarte() {
        Carte c = new CarteClan(Couleur.ROUGE, 5);
        assertEquals(5, c.getValeur());
        assertEquals(Couleur.ROUGE, c.getCouleur());
    }
}