Business Need: Pouvoir créer un ensemble d'image pour construire un fond d'écran d'un jeux représentant l'espace

  Scenario: Création réussie d'une image de fond d'écran avec ses caractéristiques
    When je crée une image de fond d'écran avec les caractéristiques suivantes :
      | nom    | space        |
      | type   | space opera  |
      | taille | 2500 x 2500  |
      | seed   | 1925         |
      | back   | #000000 0.7  |
      | middle | #00FFFF 0.75 |
      | front  | #FFFFFF      |
    Then l'image est générée avec succès
    And elle est sauvegardée avec la description "space (space opera)"