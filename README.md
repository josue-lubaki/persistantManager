# persistantManager

- Langage Utilisé : Java
- Dependance : Maven
- Base de Données : PostgreSQL

* Projet qui permet de Recupérer les éléments se trouvant dans la Base de Données (BD) pour les insérer dans les beans (Class) Utilisées : @See Retrieve()
  - Etudiant.class
  - Cours.Class
  - Inscription.class
  
* Le Projet implémente également une Methode permettant d'insérer les données provenant de notre Application (Beans) dans le Base de Données : @See BulkInsert()
* Le projet implémente un module pour la connexion dans la base de données, ce qui permet une facilité de changement de la Base de Données (Passer de PostgreSQL à Oracle par exemple)
