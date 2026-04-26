# medical chain app

application microservices pour gerer le stock medical et la chaine du froid

- suivre les produits, lots et emplacements
- creer des demandes de produits
- approuver et livrer les demandes
- surveiller les temperatures
- generer des alertes
- garder une trace des actions importantes

## roles

- `ADMIN` : vue globale, users, historique
- `PHARMACIST` : stock, demandes, alertes
- `NURSE` : creer et suivre ses demandes
- `LOGISTICS` : lots, emplacements, livraisons
- `QUALITY_MANAGER` : temperature, incidents, alertes, audit


## services

| Service | Port | Role |
| --- | --- | --- |
| discovery-service | 8761 | Eureka, annuaire des services |
| api-gateway | 8080 | point d'entree unique |
| inventory-service | 8081 | produits, lots, stock |
| requisition-service | 8082 | demandes de produits |
| temperature-service | 8083 | temperatures et incidents |
| alert-service | 8084 | alertes metier |
| user-service | 8085 | utilisateurs internes |
| auth-service | 8086 | login, register, JWT |
| audit-trail-service | 8087 | journal de tracabilite |
| frontend | 5173 | dashboard Vue |

## Lancer avec Docker

creer le fichier `.env` local :

```bash
cp .env.example .env
```

modifier les valeurs et build le projet :

```bash
docker compose up --build
```
