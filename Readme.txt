 ================================================================================
IUT de Nice / Département informatique / Module APO-Java / 2012-2013
================================================================================

Projet optionnel EspionGD

Auteur : Martini Didier, Gauthier Cibert—Volpe, Sylvain Levasseur
Responsable : 
EspionP : Sylvain Levasseur
Reseau espionGD / espionG : Gauthier Cibert—Volpe
Système de filtrage espionGD / espionG : Martini Didier
Groupe : S3T G1
Readme : Version : 1.0.0 (Dernière révision 15/01/2013 02:15)


================================================================================

1. Instructions pour l'installation
================================================================================

Deux launcher sont présents pour l’exécution du .jar qui contient le serveur EspionP, un .bat (Windows) et un .sh (Linux)

Deux launcher sont présents pour l’exécution du .jar qui contient le serveur EspionGD, un .bat (Windows) et un .sh (Linux)

Deux launcher sont présents pour l’exécution du .jar qui contient trafalgarPlayer observé par EspionG, un .bat (Windows) et un .sh (Linux)

Deux launcher sont présents pour l’exécution du .jar qui contient ImageMysterieuse observé par EspionG, un .bat (Windows) et un .sh (Linux)

Deux launcher sont présents pour l’exécution du .jar qui contient TrafalgarMap observé par EspionG, un .bat (Windows) et un .sh (Linux)

Deux launcher sont présents pour l’exécution du .jar qui contient PanneauG observé par EspionG, un .bat (Windows) et un .sh (Linux)


================================================================================

2. Fonctionnalités et spécification technique
================================================================================

EspionG : 
Récupéré de manière non intrusif un maximum d’événements, applique le système de filtrage et envoie les événement qui ont réussi a passer le filtrage vers son espionGD
Retourne le résultat de commandes que EspionP peut lui demander via EspionGD

EspionGD :
Serveur pour les espionG de la machine
Connecté a l'espionP
Récupéré les événements renvoyé par les espionG, applique le système de filtrage et envoie les événements qui ont réussi a passer le filtrage vers espionP
Transféré les données de espionP vers les espionG concerné si nécessaire

EspionGP :
Serveur qui va centraliser tous les événements.
- Une IHM qui permet la visualisation des événements et résultats de commandes, l'envoie de commandes, visualisation des clients connecté, envoyer des fichier de config en vue du filtrage vers espionGD



Systeme de filtrage :
Nous allons expliquer le système de filtrage choisi. 
Les filtres appliqués dans EspionG implémentes toute l'interface Validator qui oblige l’implémentation de la méthode « public Boolean validate(final Object évent, final Long time)	throws Exception; »
Le Boolean renvoyé correspond au passage avec succes du filtre ou pas : 
- True : L’événement répond aux critères du filtre
- False : L’événement ne répond pas aux critères du filtre

Chaque filtre (Validator) a un nom associé dans un HashMap

L'assemblage de filtre est faite par une expression Booleene :
exemple : « ( Validateur1 AND Validateur2 ) OR Validateur3 »

- Si l’événement passe (TRUE) on remplace le nom du validateur par 'true'
- Si l’événement ne passe pas (FALSE) on remplace le nom du validateur par 'false'

L’événement passe le filtre 'Validateur1' et le filtre 'Validateur3' mais pas 'Validateur2' on obtiendra : « ( true AND false ) OR true »
   
On utilisera le Groovy Shell pour analyser l'expression booleene, ce qui nous permettra d'obtenir le boolean final qui nous indiquera si oui ou non on doit envoyer l’événement a espionGD

(L'ensemble de ce que j’essaye d'expliquer correspond a la méthode Validate de EspionG) 

================================================================================

3. Problèmes connus
================================================================================

Reseau limité par sessionG et noeudG_TCP


================================================================================

4. Idées d’améliorations possibles
================================================================================



================================================================================

5. Notes et informations
================================================================================

- Utilisation de XML et donc de libraire pour le faire, nous avons choisi : jdom : http://www.jdom.org/

- Utilisation de Groovy : http://groovy.codehaus.org/
