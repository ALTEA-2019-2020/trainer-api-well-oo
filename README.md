# trainer-api-welloo

## But du projet
API permettant de représenter un dresseur de pokémon avec son équipe sous format json

## Lancer l'application
Pour lancer l'application, il suffit d'exécuter la commande suivante:
```
mvn clean install && mvn spring-boot:run
```
L'application sera accessible via l'url http://localhost:8081/  
Il est possible de modifier la configuration du projet directement dans le fichier application.properties pour modifier la connexion à la base de données, les identifiants de connexion à cette API ou son port.

Routes disponibles:
- GET http://localhost:8081/trainers/ : retourne  tous les trainers
- GET http://localhost:8081/trainers/Ash: retourne le trainer avec le nom Ash
- POST /trainers/ : créer un nouveau trainer trainer
- PUT /trainers/{name}: modifier un trainer
- DELETE /trainers/{name}: supprimer un trainer

## Exécuter les tests
Pour exécuter les tests, utiliser la commande suivante:
```
mvn test
```

## Déploiement
L'application n'a pas été déployé

## Annexes
Un swagger a été mis en place et est accessible via l'url http://localhost:8081/swagger-ui.html#/  
Des tests postman sont disponibles dans le fichier `src/test/resources/trainer-api.json`

## Auteur

Christopher DUCROCQ [@well-oo](https://github.com/well-oo)  
Etudiant en Master 2 MIAGE FA