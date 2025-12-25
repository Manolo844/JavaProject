package com.schottenTotten.model;

import java.util.ArrayList;
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
        
        boolean contientJoker = false;
        boolean contientEspion = false;
        boolean contientPB = false;

        for (Carte c : cartes) {
            if (c instanceof CarteTactique) {
                TypeTactique t = ((CarteTactique) c).getType();
                if (t == TypeTactique.JOKER) {
                    contientJoker = true;
                }
                else if (t == TypeTactique.ESPION) {
                    contientEspion = true;
                }
                else if (t == TypeTactique.PORTE_BOUCLIER){ 
                    contientPB = true;
                }
            }
        }
        if (contientJoker) {
            return analyserAvecJoker(cartes);
        }

        if (contientEspion) {
            return analyserAvecEspion(cartes);
        }

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

        if (memeCouleur && suite){ 
            type = TypeCombinaison.SUITE_COULEUR;
        }
        else if (memeValeur) {
            type = TypeCombinaison.BRELAN;
        }
        else if (memeCouleur){
            type = TypeCombinaison.COULEUR;
        }
        else if (suite) {
            type = TypeCombinaison.SUITE;
        }
        else{
             type = TypeCombinaison.SOMME;
        }
        return new CombinaisonBorne(type, somme);
    }

    private static CombinaisonBorne analyserAvecJoker(List<Carte> cartes) {

        // indices des jokers
        List<Integer> jokers = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            Carte c = cartes.get(i);
            if (c instanceof CarteTactique && ((CarteTactique) c).getType() == TypeTactique.JOKER) {
                jokers.add(i);
            }
        }

        CombinaisonBorne meilleure = null;

        // essai pour toutes les combinaisons possible
        for (Couleur c1 : Couleur.values()) {
            for (int v1 = 1; v1 <= 9; v1++) {

                for (Couleur c2 : Couleur.values()) {
                    for (int v2 = 1; v2 <= 9; v2++) {

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

        // indices où il y a un espion
        List<Integer> espions = new ArrayList<>();
        for (int i = 0; i < cartes.size(); i++) {
            Carte c = cartes.get(i);
            if (c instanceof CarteTactique && ((CarteTactique) c).getType() == TypeTactique.ESPION) {
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
        List<Integer> pbs = new ArrayList<>();

        for (int i = 0; i < cartes.size(); i++) {
            Carte c = cartes.get(i);
            if (c instanceof CarteTactique && ((CarteTactique) c).getType() == TypeTactique.PORTE_BOUCLIER) {
                pbs.add(i);
            }
        }

        int[] valeursPB = {1, 2, 3};

        for (Couleur couleur : Couleur.values()) {
            for (int valeur : valeursPB) {
                List<Carte> testList = new ArrayList<>(cartes);
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
    // on récupère les valeurs
    int v1 = cartes.get(0).getValeur();
    int v2 = cartes.get(1).getValeur();
    int v3 = cartes.get(2).getValeur();

    // trie pour que v1<v2<v3
    if (v1 > v2) { 
        int tmp = v1; v1 = v2; v2 = tmp; 
    }
    if (v2 > v3) { 
        int tmp = v2; v2 = v3; v3 = tmp;
    }
    if (v1 > v2) { 
        int tmp = v1; v1 = v2; v2 = tmp; 
    }

    // suite ou non
    return (v1 + 1 == v2) && (v2 + 1 == v3);
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