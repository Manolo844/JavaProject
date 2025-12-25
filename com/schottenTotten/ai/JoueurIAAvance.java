/* IA avancée qui se base sur le calcul d'un score sur tous les coups possibles*/

package com.schottenTotten.ai;

import com.schottenTotten.model.*;
import java.util.List;
import java.util.Random;

public class JoueurIAAvance extends JoueurIA { 

    private Random random;

    public JoueurIAAvance(String nom) {
        super(nom);
        this.random = new Random();
    }

    @Override
    public int[] reflechirCoup(Borne[] bornes, int monIndex, int tactiquesMoi, int tactiquesAutre) {
        int[] meilleurCoup = null;
        int meilleurScore = -10000;

        for (int iCarte = 0; iCarte < cartesJoueur.size(); iCarte++) {
            Carte carteEnMain = cartesJoueur.get(iCarte);

            if (carteEnMain instanceof CarteTactique) {
                if (tactiquesMoi >= tactiquesAutre + 1) {
                    continue; //carte suivante
                }
            }

            for (int iBorne = 0; iBorne < bornes.length; iBorne++) {
                Borne b = bornes[iBorne];

                if (!b.estRevendiquee() && !b.estCompletePourJoueur(monIndex)) {
                    
                    int score = calculerScoreCoup(carteEnMain, b, monIndex);

                    if (score > meilleurScore) {
                        meilleurScore = score;
                        meilleurCoup = new int[]{iCarte, iBorne};
                    } 
                    else if (score == meilleurScore) { //éviter le déterminisme en cas d'égalité
                        if (random.nextBoolean()) {
                            meilleurCoup = new int[]{iCarte, iBorne};
                        }
                    }
                }
            }
        }
        return meilleurCoup;
    }

    private int calculerScoreCoup(Carte carte, Borne b, int monIndex) {
        List<Carte> mesCartes = b.getCartesPourJoueur(monIndex);
        int indexAdversaire = (monIndex == 0) ? 1 : 0;
        List<Carte> cartesAdversaire = b.getCartesPourJoueur(indexAdversaire);

        int score = 0;

        if (carte instanceof CarteTactique) {
            return 20; 
        }

        //système de calcul de score, prenant en compte les différentes manières de gagner
        if (mesCartes.isEmpty()) {
            score += carte.getValeur();

            if (carte.getValeur() == 9) score += 30;
            else if (carte.getValeur() == 8) score += 20;
            else if (carte.getValeur() >= 6) score += 10;

            if (!cartesAdversaire.isEmpty()) {
                score += 15;
            }
            
            return score;
        }
        
        score += carte.getValeur(); 

        for (Carte cPosee : mesCartes) {
            if (cPosee instanceof CarteClan && carte instanceof CarteClan) { 
                
                boolean memeCouleur = cPosee.getCouleur() == carte.getCouleur();
                boolean memeValeur = cPosee.getValeur() == carte.getValeur();
                boolean valeursProches = Math.abs(cPosee.getValeur() - carte.getValeur()) == 1;

                if (memeCouleur && valeursProches) {
                    score += 60;
                }
                else if (memeValeur) {
                    score += 40;
                }
                else if (memeCouleur) {
                    score += 5;
                }
                else if (valeursProches) {
                    score += 2;
                }
            }
        }
        
        if (mesCartes.size() == 2 && cartesAdversaire.size() == 2) {
            score += 10;
        }

        if (mesCartes.size() == 2 && score < 15) {
            score -= 50;
        }

        return score;
    }
    
    @Override
    public String toString() {
        return "IA Avancée (" + super.getNom() + ")";
    }
}
