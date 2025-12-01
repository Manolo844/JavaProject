package com.schottenTotten.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pioche {

    private final List<Carte> cartes;

    public Pioche(List<Carte> cartes) {
        this.cartes = new ArrayList<>(cartes);
    }

    public boolean estVide() {
        return cartes.isEmpty();
    }

    public Carte piocher() {
        if (cartes.isEmpty()) {
            return null;
        }
        return cartes.remove(cartes.size() - 1);
    }

    public int taille() {
        return cartes.size();
    }
}
