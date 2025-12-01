package com.schottenTotten.model;

import java.util.Arrays;
import java.util.List;

public class CombinaisonBorne implements Comparable<CombinaisonBorne> {

    private final TypeCombinaison type;
    private final int somme;

    public CombinaisonBorne(TypeCombinaison type, int somme) {
        this.type = type;
        this.somme = somme;
    }

    public TypeCombinaison getType() {
        return type;
    }

    public int getSomme() {
        return somme;
    }

    public static CombinaisonBorne analyser(List<Carte> cartes) {
        if (cartes.size() != 3) {
            throw new IllegalArgumentException("Une combinaison doit contenir exactement 3 cartes.");
        }

        boolean memeCouleur = memeCouleur(cartes);
        boolean memeValeur = memeValeur(cartes);
        boolean suite = estSuite(cartes);
        int s = somme(cartes);

        TypeCombinaison type;
        if (memeCouleur && suite) {
            type = TypeCombinaison.SUITE_COULEUR;
        } else if (memeValeur) {
            type = TypeCombinaison.BRELAN;
        } else if (memeCouleur) {
            type = TypeCombinaison.COULEUR;
        } else if (suite) {
            type = TypeCombinaison.SUITE;
        } else {
            type = TypeCombinaison.SOMME;
        }

        return new CombinaisonBorne(type, s);
    }

    // méthodes privées d'aide

    private static int somme(List<Carte> cartes) {
        int s = 0;
        for (Carte c : cartes) {
            s += c.getValeur();
        }
        return s;
    }

    private static boolean memeCouleur(List<Carte> cartes) {
        return cartes.get(1).getCouleur() == cartes.get(0).getCouleur()
            && cartes.get(2).getCouleur() == cartes.get(0).getCouleur();
    }

    private static boolean memeValeur(List<Carte> cartes) {
        return cartes.get(1).getValeur() == cartes.get(0).getValeur()
            && cartes.get(2).getValeur() == cartes.get(0).getValeur();
    }

    private static boolean estSuite(List<Carte> cartes) {
        // trier les valeurs pour vérifier la suite
        int[] v = new int[3];
        v[0] = cartes.get(0).getValeur();
        v[1] = cartes.get(1).getValeur();
        v[2] = cartes.get(2).getValeur();
        Arrays.sort(v);

        return v[0] + 1 == v[1] && v[1] + 1 == v[2];
    }


    @Override
    public int compareTo(CombinaisonBorne autre) {
        // d'abord la force
        if (this.type.getForce() != autre.type.getForce()) {
            return Integer.compare(this.type.getForce(), autre.type.getForce());
        }
        //sinon la somme
        return Integer.compare(this.somme, autre.somme);
    }


}
