Business Need: Pouvoir créer un ensemble d'image pour construire un fond d'écran d'un jeux représentant l'espace

  Scenario: Construction réussie d'une image de bruit
    When je construis une image de bruit avec les caractéristiques suivantes :
      | taille | 500 x 500   |
      | seed   | 1925        |
      | back   | #000000 0.4 |
      | middle | #00FFFF 0.7 |
      | front  | #FFFFFF      |
    Then l'image est générée avec succès

  Scenario: Notation d'un rendu cosmétique crée une base et un rendu
    When je note un rendu cosmétique avec les caractéristiques suivantes :
      | taille | 500 x 500   |
      | seed   | 1925        |
      | back   | #000000 0.4 |
      | middle | #00FFFF 0.7 |
      | front  | #FFFFFF      |
      | note   | 4            |
    Then la base de structure est sauvegardée
    And le rendu cosmétique est sauvegardé avec la note 4
