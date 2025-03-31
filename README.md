# CyberGames22

CyberGames22 est une application de gestion pour un cybercafé, permettant aux utilisateurs d'acheter, activer et gérer des forfaits, ainsi qu'aux administrateurs de gérer les machines et les utilisateurs.

---

## Fonctionnalités principales

### Utilisateurs
- **Connexion** : Les utilisateurs peuvent se connecter via une interface sécurisée.
- **Gestion des forfaits** :
  - Acheter des forfaits disponibles.
  - Activer un forfait à l'aide d'un code unique.
  - Suivre le temps restant sur un forfait actif.
- **Interface utilisateur intuitive** : Les utilisateurs peuvent naviguer facilement entre les différentes fonctionnalités.

### Administrateurs
- **Gestion des machines** :
  - Ajouter, modifier ou supprimer des machines.
  - Voir les détails des machines (nom, processeur, mémoire, stockage, statut, etc.).
- **Gestion des utilisateurs** (fonctionnalité à compléter).

---

## Structure du projet

### Répertoires principaux
- **`src/UI`** : Contient les interfaces utilisateur (Swing) pour les différentes fonctionnalités.
- **`src/DAO`** : Contient les classes d'accès aux données pour interagir avec la base de données.
- **`src/Conn`** : Contient la classe de connexion à la base de données.
- **`src/Model`** : Contient les modèles de données (ex. `Machine`).
- **`src/Session`** : Contient la classe `UserSession` pour gérer la session utilisateur.

### Fichiers importants
- **`Main.java`** : Point d'entrée de l'application.
- **`DatabaseConnection.java`** : Gère la connexion à la base de données MySQL.
- **`META-INF/MANIFEST.MF`** : Définit la classe principale pour exécuter l'application.

---

## Prérequis

- **Java** : Version 21 ou supérieure.
- **MySQL** : Une base de données MySQL configurée avec les tables nécessaires.
- **Bibliothèques externes** :
  - `mysql-connector-java-8.0.25.jar` : Pour la connexion à MySQL.
  - `jbcrypt-0.4.jar` : Pour le hachage des mots de passe.

---

## Installation

### 1. Télécharger les fichiers nécessaires
- Téléchargez le fichier `.jar` de l'application (par exemple : `cyberGames22.jar`).
- Téléchargez le fichier SQL pour la base de données (par exemple : `cyberGames22.sql`).

### 2. Configurer la base de données
1. Assurez-vous que MySQL est installé et en cours d'exécution sur votre machine.
2. Connectez-vous à MySQL via le terminal ou un outil comme phpMyAdmin.
3. Créez une base de données nommée `cybergamesArras2` :
   ```sql
   CREATE DATABASE cybergamesArras2;


