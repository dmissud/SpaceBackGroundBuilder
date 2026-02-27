package org.dbs.sbgb.port.out;

import org.dbs.sbgb.domain.model.NormalizedNoiseGrid;
import org.dbs.sbgb.port.in.ImageRequestCmd;

import java.util.List;

/**
 * Port de sortie pour le calcul (et la mise en cache) des grilles de bruit normalisées.
 * L'implémentation décide si le résultat est recalculé ou récupéré depuis le cache.
 */
public interface NoiseGridComputationPort {

    /** Retourne la grille normalisée pour un calcul mono-couche. La clé de cache est le configHash. */
    NormalizedNoiseGrid computeSingleLayerGrid(int configHash, ImageRequestCmd.SizeCmd sizeCmd);

    /** Retourne les grilles normalisées pour chaque layer actif d'un calcul multi-couches. */
    List<NormalizedNoiseGrid> computeMultiLayerGrids(int configHash, ImageRequestCmd.SizeCmd sizeCmd);
}
