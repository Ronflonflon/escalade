# Escalado

Ce dépôt est associé au projet [Créez un site communautaire autour de l’escalade](https://openclassrooms.com/fr/projects/128/assignment) pour [OpenClassRooms](https://openclassrooms.com).

## Technologies

- Java JDK 8
- Apache Tomcat 8.5.34
- Apache Maven 4.0.0
- PostgreSQL 9.4
- Spring Boot 2.0.5
- Spring Security 2.0.5
- Spring Data JPA 2.1.6
- Spring Web MVC 5.1.4
- Thymeleaf 3.0.11
- Bulma 0.7.4

### Modules

- `escalade-business` : contient la logique métier
- `escalade-consumer` : contient la persistance et le pattern DAO
- `escalade-model` : contient les entités
- `escalade-webapp` : contient la vue

### Schémas

#### Diagramme de classes

![](http://image.noelshack.com/fichiers/2019/23/1/1559577482-capture1.png)

#### Model physique de données

![](http://image.noelshack.com/fichiers/2019/23/1/1559572493-capture.png)

### Déploiement

- Importer le projet dans votre IDE (celui utilisé par la réalisé de celui-ci a été Intellij IDEA)

Pour configurer la base de donner il faut créer deux variables d'environnement :
- `jdbc_database` contenant le nom de la base de données à laquelle le programme doit être connecté
- `jdbc_username` contenant le nom d'utilisateur pour se connecter à la base de donnée
- `jdbc_password` contenant le mot de passe pour se connecter à la base de donnée

Pour générer la base de données :
- Exécuter le programme, il créera lui-même la structure de la base de donnée avec Spring Data JPA
- Pour utiliser le jeu de données il faut utiliser le fichier dumpExample dans le dossier assets
Pour le jeu de données deux comptes sont déjà créés, les noms de compte sont Compte1 et Compte2, leur mot de passe est "mdp" à chacun.