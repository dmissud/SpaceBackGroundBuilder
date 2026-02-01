Business Need: Pouvoir créer un ensemble d'image pour construire un fond d'écran d'un jeux représentant l'espace

  Scenario: Creer une image de fond d'écran pour un jeux de type space opera
    Given Il n y a pas d image de fond d ecran
    When je cree une image de fond d'ecran avec la taille '2500' x '2500' et un seed '1925'
    And les couleurs sont en back '#000000' et en front '#FFFFFF' et pour middle '#00FFFF'
    And les valeurs de changement sont de '0.7' pour le back et '0.75'  pour le middle
    Then je veux que l'image soit creee avec le nom "space" et le type "space opera"