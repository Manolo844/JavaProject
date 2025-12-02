package com.schottenTotten.model;

import java.util.ArrayList;
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

        boolean contientJoker = cartes.stream().anyMatch(c -> c instanceof CarteTactique tact && tact.getType() == TypeTactique.JOKER);

        boolean contientEspion = cartes.stream().anyMatch(c -> c instanceof CarteTactique tact && tact.getType() == TypeTactique.ESPION);

        boolean contientPB = cartes.stream().anyMatch(c -> c instanceof CarteTactique tact && tact.getType() == TypeTactique.PORTE_BOUCLIER);

        // Joker = traitement le plus fort → tester avant
        if (contientJoker) {
            return analyserAvecJoker(cartes);
        }

        // Espion = couleur flexible seulement
        if (contientEspion) {
            return analyserAvecEspion(cartes);
        }

        // Porte Bouclier = somme 123
        if (contientPB) {
            return analyserAvecPorteBouclier(cartes);
        }

        // normal
        return analyserSansJoker(cartes);
    }

    private static CombinaisonBorne analyserSansJoker(List<Carte> cartes) {
        boolean memeCouleur = memeCouleur(cartes);
        boolean memeValeur = memeValeur(cartes);
        boolean suite = estSuite(cartes);
        int somme = somme(cartes);

        TypeCombinaison type;

        if (memeCouleur && suite) type = TypeCombinaison.SUITE_COULEUR;
        else if (memeValeur) type = TypeCombinaison.BRELAN;
        else if (memeCouleur) type = TypeCombinaison.COULEUR;
        else if (suite) type = TypeCombinaison.SUITE;
        else type = TypeCombinaison.SOMME;

        return new CombinaisonBorne(type, somme);
    }

    private static CombinaisonBorne analyserAvecJoker(List<Carte> cartes) {

        // indices des jokers
        List<Integer> jokers = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            if (cartes.get(i) instanceof CarteTactique tact &&
                tact.getType() == TypeTactique.JOKER) 
            {
                jokers.add(i);
            }
        }

        CombinaisonBorne meilleure = null;

        // essai pour toutes combinaisons possibles 
        for (Couleur c1 : Couleur.values()) {
            for (int v1 = 1; v1 <= 9; v1++) {

                for (Couleur c2 : Couleur.values()) {
                    for (int v2 = 1; v2 <= 9; v2++) {

                        // nouvelle copie pour ce test
                        List<Carte> copie = new ArrayList<>(cartes);

                        if (jokers.size() >= 1)
                            copie.set(jokers.get(0), new CarteClan(c1, v1));

                        if (jokers.size() == 2)
                            copie.set(jokers.get(1), new CarteClan(c2, v2));

                        CombinaisonBorne test = analyserSansJoker(copie);

                        if (meilleure == null || test.compareTo(meilleure) > 0)
                            meilleure = test;
                    }
                }
            }
        }

        return meilleure;
    }
    private static CombinaisonBorne analyserAvecEspion(List<Carte> cartes) {
        CombinaisonBorne meilleure = null;

        List<Carte> copie = new ArrayList<>(cartes);

        // indices où il y a un ESPION
        List<Integer> espions = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            if (cartes.get(i) instanceof CarteTactique tact
                && tact.getType() == TypeTactique.ESPION) {
                espions.add(i);
            }
        }

        for (Couleur couleur : Couleur.values()) {

            List<Carte> testList = new ArrayList<>(cartes);

            for (int idx : espions) {
                testList.set(idx, new CarteClan(couleur, 7));
            }

            CombinaisonBorne test = analyserSansJoker(testList);

            if (meilleure == null || test.compareTo(meilleure) > 0)
                meilleure = test;
        }

        return meilleure;
    }


    private static CombinaisonBorne analyserAvecPorteBouclier(List<Carte> cartes) {
        CombinaisonBorne meilleure = null;

        // indices des PB
        List<Integer> pbs = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            if (cartes.get(i) instanceof CarteTactique tact &&
                tact.getType() == TypeTactique.PORTE_BOUCLIER) 
            {
                pbs.add(i);
            }
        }

        // valeur possible : 1, 2 ou 3
        int[] valeursPB = {1, 2, 3};

        for (Couleur couleur : Couleur.values()) {
            for (int valeur : valeursPB) {

                // nouvelle copie pour ce test
                List<Carte> testList = new ArrayList<>(cartes);

                // remplace chaque PB par la fausse carte correspondante
                for (int idx : pbs) {
                    testList.set(idx, new CarteClan(couleur, valeur));
                }

                CombinaisonBorne test = analyserSansJoker(testList);

                if (meilleure == null || test.compareTo(meilleure) > 0) {
                    meilleure = test;
                }
            }
        }

        return meilleure;
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
