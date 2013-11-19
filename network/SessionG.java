package network;


//
// IUT de Nice / Departement informatique / Module APO-Java
// Annee 2012_2013 - Classes generiques de reseau
//
// Classe SessionG V3.4.0 - Utilise NoeudG_TCP V3.3.2
//
// + Version 1.0.0 : Premiere version presque fonctionnelle
// (seulement PC de l'IUT, non Observer/Observable)
//
// + Version 2.0.0 : version amelioree permettant la separation des messages par
// client
// + utilisation de NoeudG_TCP V2.0.0
// - suppression des methodes getMessage et getMessages
// (uniquement une liste de messages pour tous les clients en V1.0.0
// -> aucune liste, recuperation depuis les noeuds en V2.0.0)
// + remplacement par getMessageClient et getMessageClients
// + modification du constructeur (V2.0.0 de NoeudG_TCP)
// + ajout de la classe interne ThreadAttenteClient
// + ajout de la methode fermer
//
// + Version 3.0.0 : Passage en Observer/Observable, et finitions importantes
// + utilisation de NoeudG_TCP V3.0.0
// + modification du constructeur (V3.0.0 de NoeudG_TCP)
// + Passage en Observer/Observable
//
// + Version 3.1.0 : + utilisation de NoeudG_TCP V3.1.0 -> Version utilisable
// globalement
// + version sans temporisation
// + utilisation de pseudo-semaphores (AtomicBoolean)
// + factorisation avec la methode aLogger()
// + ajout de la classe interne ThreadCouperAttente
//
// + Version 3.2.0 : + ajout du getter getListePseudos()
// + ajout du getter existeClient()
// + modification de la methode fermer
//
// + Version 3.3.0 : + ajout des methodes deconnecterClient() et
// deconnecterClients()
//
// + Version 3.4.0 : + utilisation de la v 3.3.2 de NoeudG_TCP
// + demarrage de la session commande, constructeur ne la demarre pas
// + modification de la classe interne ThreadAttenteClient
// possibilite de se connecter plusieurs fois avec le meme pseudo
// + modification de l'implementation du pattern Observer/Observable
// remonte l'emmetteur du message
// + ajout de la methode getClientLike
// + nouvelles signatures de retirerMessageClients, envoyerMessageClients et
// deconnecterClients
// permettant l'envoi a une liste
//
// Auteur : Sebastien PETILLON
//

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SessionG modelise une Session permettant la communication d'un serveur avec
 * de multiples clients.
 * 
 * @author Sebastien PETILLON
 * @version 3.4.0
 */

// ------------------------------------------------------- //
// ---------------------- SESSIONG ---------------------- //
// ------------------------------------------------------- //

public class SessionG extends Observable implements Observer {

	// ------------------------------------------------------- //
	// ---------------------- ATTRIBUTS ---------------------- //
	// ------------------------------------------------------- //

	// --- Attributs de la session --- /

	// Premier port de la plage ensuite incremente
	private int					port;
	// Port de distribution
	private int					distribPort;
	// Adresse de la session
	private static String		host	= "127.0.0.1";
	// Noeud de distribution de port
	private NoeudG_TCP			distribNoeud;
	// Liste des noeuds serveurs
	private final HashMap		noeudsServeurs;
	// Nombre de clients actuel
	private int					nbClient;
	// Representation du texte du fichier de log par un liste de String
	private final LinkedList	log		= new LinkedList();

	// --- Attributs de configuration ---/

	// Date de fin de l'attente
	private Calendar			dateF;
	// Nombre maximum de clients
	private Integer				nbMaxConnexions;
	// Chemin du fichier de log
	private String				cheminLog;
	// TimeOut de fermeture d'un client
	private int					timeOutDeconnexionClient;
	// Boolean determinant la verbosite de la sessoin
	boolean						verbeuse;

	// --- Attributs de gestions de threads --- /

	// Thread d'attribution
	private ThreadAttenteClient	threadDistrib;
	// Thread d'arret d'attribution
	private ThreadCouperAttente	threadCouperAttente;
	// TimeOut d'attente
	private int					timeout;
	// boolean condition de l'attente de client
	private final AtomicBoolean	attente;

	// ------------------------------------------------------- //
	// --------------------- CONSTRUCTEUR -------------------- //
	// ------------------------------------------------------- //

	/**
	 * Constructeur normal
	 * 
	 * @param configuration
	 *            : HashMap de description de la configuration de la session :
	 *            <ul>
	 *            <li>port : port de connexion par defaut avant distribution
	 *            (int)</li>
	 *            <li>nbMaxConnexions : nombre de clients a connecter avant
	 *            demarrage (int)</li>
	 *            <li>date : date d'arr�t de l'attente de connexion de clients</li>
	 *            <li>duree : duree d'attente de connexion de clients -> prevaut
	 *            sur la date</li>
	 *            <li>chemin : chemin et nom du fichier de log</li>
	 *            <li>timeOutDeconnexionClient : temps de deconnexion apres
	 *            l'ordre de deconnexion d'un client, superieur a zero</li>
	 *            </ul>
	 * <br/>
	 *            Si aucun des trois criteres d'arret (date, duree, nombre de
	 *            connexions) n'est fournit, la session n'attendra qu'un client.
	 * @since V1.0.0
	 */
	public SessionG(HashMap configuration) {

		// Initialiser l'attente
		//
		attente = new AtomicBoolean(true);

		// Recuperer les informations de configuration
		//
		Integer p = (Integer) configuration.get("port");
		Integer nbCM = (Integer) configuration.get("nbMaxConnexions");
		Calendar cDate = (Calendar) configuration.get("date");
		Integer cDuree = (Integer) configuration.get("duree");
		String c = (String) configuration.get("chemin");
		Integer t = (Integer) configuration.get("timeOutDeconnexionClient");
		Boolean v = (Boolean) configuration.get("verbeux");

		// Affecter le caractere verbeux de la session quant au temps restant
		//
		if (v == null) {
			verbeuse = true;
		} else {
			verbeuse = v;
		}

		// Affecter le numero de port de distribution
		//
		if (p == null) {
			distribPort = 8080;
		} else {
			distribPort = p;
		}

		// Initialiser le port d'attribution
		//
		port = distribPort + 1;

		// Affecter le chemin de log
		//
		if (c == null) {
			cheminLog = "./log.txt";
		} else {
			cheminLog = c;
		}

		// Affecter la date
		//
		if (cDate != null) {
			dateF = cDate;
		}
		if (cDuree != null) {
			dateF = Calendar.getInstance();
			dateF.setTimeInMillis(dateF.getTimeInMillis() + cDuree * 1000);
		}

		// Affecter le nombre de connexions maximum
		//
		if (nbCM == null) {
			if (dateF == null) {
				nbMaxConnexions = 1;
			} else {
				nbMaxConnexions = null;
			}
		} else {
			nbMaxConnexions = nbCM;
		}

		// Affecter le timeOut de fermeture d'un client
		//
		if (t != null) {
			timeOutDeconnexionClient = t;
		} else {
			timeOutDeconnexionClient = 1;
		}

		// Creation de la liste de noeuds serveurs vide
		//
		noeudsServeurs = new HashMap();

	}

	// ------------------------------------------------------- //
	// ---------------------- METHODES ----------------------- //
	// ------------------------------------------------------- //

	/**
	 * Demarre le thread de distribution de ports aux nouveaux clients
	 * 
	 * @since V1.0.0
	 */
	public void demarrerConnexions() {

		// Ajouter au log le demarrage
		//
		aLogger("[DEMARRAGE DE LA SESSION]");

		// Creer le thread de distribution
		//
		threadDistrib = new ThreadAttenteClient();
		threadDistrib.addObserver(this);

		// Creer et lancre le thread de coupure par date
		//
		threadCouperAttente = new ThreadCouperAttente();
		threadCouperAttente.start();

		// Lancer la methode run du thread de distribution
		//
		threadDistrib.run();

		// boolean de verification du nombre de clients
		//
		boolean verifNb = false;

		// Si le TimeOut n'est pas fini et le nombre de clients incorrect,
		// attendre
		//
		while (attente.get() && verifNb) {

			if (nbMaxConnexions == null) {
				verifNb = false;
			} else {

				// Mettre a jour la comparaison du nombre de clients
				//
				verifNb = nbMaxConnexions != nbClient;
			}
		}

		// Couper le thread permettant de couper dans le cas d'un arret par
		// nombre de clients
		//
		while (threadCouperAttente.isAlive()) {
			attente.set(false);
		}

		// Ajouter au log le debut des echanges
		//
		aLogger("[DEBUT DES ECHANGES]");

	}

	/**
	 * Ajoute un message a la liste d'envoi
	 * 
	 * @param msg
	 *            Message a transmettre
	 * @param pseudo
	 *            Pseudo du client auquel envoyer
	 * @return
	 *         True si l'envoi a ete realise, False si le client n'existe pas
	 * @since V1.0.0
	 */
	public boolean envoyerClient(HashMap msg, String pseudo) {

		// Recuperer le noeud serveur correspondant au client demande
		//
		NoeudG_TCP noeud = (NoeudG_TCP) noeudsServeurs.get(pseudo);

		// S'il n'existe pas, cesser l'envoi et rendre faux
		//
		if (noeud == null) {
			return false;
		}

		// Envoyer le message en l'ajoutant au noeud serveur
		//
		noeud.envoyerMessage(msg);

		// Mettre a jour le fichier de log avec l'envoi du message
		//
		aLogger("\t\t|  Envoi  |<Pseudo=" + pseudo + ">\t<Message=" + msg + ">");

		return true;
	}

	/**
	 * Ajoute un message a la liste d'envoi de tous les clients listes
	 * 
	 * @param msg
	 *            Message a transmettre
	 * @param clients
	 *            Liste des clients a qui transmettre
	 * @since V3.4.0
	 */
	public void envoyerClients(HashMap msg, LinkedList clients) {

		// Parcourir la liste des pseudos en parametre
		//
		Iterator i = clients.iterator();
		while (i.hasNext()) {

			// Envoyer le message au client
			//
			envoyerClient(msg, ((String) i.next()));
		}

	}

	/**
	 * Ajoute a la liste d'envoi de tous les clients
	 * 
	 * @param msg
	 *            Message a transmettre
	 * @since V1.0.0
	 */
	public void envoyerClients(HashMap msg) {

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Envoyer le message au client
			//
			envoyerClient(msg, (String) i.next());
		}
	}

	/**
	 * Recupere un message du client (blocant)
	 * 
	 * @param pseudo
	 *            Pseudo du client dont on veut le message
	 * @return Message du client
	 * @since V2.0.0
	 */
	public HashMap retirerMessageClient(String pseudo) {

		// Creer le HashMap de reception
		//
		HashMap msg = new HashMap();

		// Recuperer le noeud concerne
		//
		NoeudG_TCP noeud = (NoeudG_TCP) noeudsServeurs.get(pseudo);

		// Recuperer le message depuis le noeud
		//
		msg = (HashMap) noeud.attendreMessage();

		// Logger l'action
		//
		aLogger("\t\t|Reception|<Pseudo=" + pseudo + ">\t<Message=" + msg + ">");

		return msg;
	}

	/**
	 * Recup�re un message de chaque client (blocant)
	 * 
	 * @return En cles les pseudo, en associes les messages respectifs (HashMap)
	 * @since V2.0.0
	 */
	public HashMap retirerMessageClients() {

		// Creer le HashMap a renvoyer
		//
		HashMap msgs = new HashMap();

		// Recuperer la liste des noeuds
		//
		LinkedList noeuds = getListePseudos();

		// Parcourir la liste des pseudos
		//
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Recuperer le message de ce client
			//
			msgs.put(pseudo, retirerMessageClient(pseudo));
		}

		return msgs;
	}

	/**
	 * Recupere un message de chaque client liste (blocant)
	 * 
	 * @param clients
	 *            :
	 *            Liste des clients desquels on veut recuperer un message
	 * @return En cles les pseudo, en associes les messages respectifs (HashMap)
	 * @since V3.4.0
	 */
	public HashMap retirerMessageClients(LinkedList clients) {

		// Creer le HashMap a renvoyer
		//
		HashMap msgs = new HashMap();

		// Parcourir la liste en parametre
		//
		Iterator i = clients.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Recuperer le message de ce client
			//
			msgs.put(pseudo, retirerMessageClient(pseudo));
		}

		return msgs;
	}

	/**
	 * Liste les clients contenant la chaine donnee
	 * 
	 * @param pseudoCherche
	 *            Chaine de recherche
	 * @return
	 *         Liste des clients
	 * @since V3.4.0
	 */
	public LinkedList getClientLike(String pseudoCherche) {

		// Creer la liste de retour
		//
		LinkedList like = new LinkedList();

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// L'ajouter a la liste a renvoyer s'il contient la chaine
			//
			if (pseudo.contains(pseudoCherche)) {
				like.add(pseudo);
			}
		}

		return like;
	}

	/**
	 * Attend le message du client
	 * 
	 * @param pseudo
	 *            Pseudo du client attendu
	 * @since V1.0.0
	 */
	private void attendreClient(String pseudo) {

		// Recuperer le noeud correspondant
		//
		NoeudG_TCP noeud = (NoeudG_TCP) noeudsServeurs.get(pseudo);

		// Attendre un message
		//
		noeud.attendreMessage();
	}

	/**
	 * Entamer l'emission de messages
	 * 
	 * @since V1.0.0
	 */
	public void demarrerEmission() {

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo correspondant
			//
			pseudo = (String) i.next();

			// Debuter l'emission du noeud
			//
			((NoeudG_TCP) noeudsServeurs.get(pseudo)).debuterEmission();
		}
	}

	/**
	 * Entamer la reception de messages
	 * 
	 * @since V1.0.0
	 */
	public void demarrerReception() {

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Debuter la reception du noeud
			//
			((NoeudG_TCP) noeudsServeurs.get(pseudo)).debuterReception();
		}
	}

	/**
	 * Cesser l'emission de messages
	 * 
	 * @since V1.0.0
	 */
	public void stopperEmission() {

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {
			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Cesser l'emission du noeud
			//
			((NoeudG_TCP) noeudsServeurs.get(pseudo)).stopperEmission();
		}
	}

	/**
	 * Cesser la reception de messages
	 * 
	 * @since V1.0.0
	 */
	public void stopperReception() {

		// Parcourir la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Cesser la reception du noeud
			//
			((NoeudG_TCP) noeudsServeurs.get(pseudo)).stopperReception();
		}
	}

	/**
	 * Permet d'obtenir la liste des clients connectes
	 * 
	 * @return LinkedList : liste des pseudos (String)
	 * @since V3.2.0
	 */
	public LinkedList getListePseudos() {

		// Creer la liste de retour
		//
		LinkedList res = new LinkedList();

		// Parcourir le HashMap des noeuds
		//
		Set cles = noeudsServeurs.keySet();
		Iterator i = cles.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// L'ajouter a la liste
			//
			res.add(pseudo);
		}

		return res;
	}

	/**
	 * Permet de savoir si un client est connecte
	 * 
	 * @param nom
	 *            Pseudo du client dont on verifie l'existance
	 * @return
	 * @since V3.2.0
	 */
	public boolean existeClient(String nom) {

		// Recuper la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();

		// Parcourir la liste
		//
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Comparer le pseudo au parametre
			//
			if (pseudo.equals(nom)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Deconnecter le client specifie
	 * 
	 * @param pseudo
	 *            Pseudo du client a deconnecter
	 * @param messageFermeture
	 *            Message de prevention de fermeture
	 * @return boolean : True si le client est deconnecte, False si le client
	 *         n'existe pas
	 * @since V3.3.0
	 */
	public boolean deconnecterClient(String pseudo, HashMap messageFermeture) {

		// Verifier l'existance du client
		//
		if (!existeClient(pseudo)) {
			return false;
		}

		// Fermer le noeud de communication avec un message
		//
		((NoeudG_TCP) noeudsServeurs.get(pseudo)).fermerForce(messageFermeture,
						timeOutDeconnexionClient);

		// Retirer le noeud de la liste
		//
		noeudsServeurs.remove(pseudo);

		// Enregistrer dans le fichier de log la deconnexion
		//
		aLogger("<Deconnexion-Client><Pseudo=" + pseudo + ">\t<Raison="
						+ messageFermeture + ">");

		return true;
	}

	/**
	 * Deconnecter tous les clients
	 * 
	 * @param messageFermeture
	 *            Message de fermeture
	 * @since V3.3.0
	 */
	public void deconnecterClients(HashMap messageFermeture) {

		// Recuper la liste des pseudos
		//
		LinkedList noeuds = getListePseudos();

		// Parcourir la liste
		//
		Iterator i = noeuds.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Deconnecter le client associe au pseudo
			//
			deconnecterClient(pseudo, messageFermeture);
		}

	}

	/**
	 * Deconnecter tous les clients specifies
	 * 
	 * @param clients
	 *            Liste des clients a deconnecter
	 * @param messageFermeture
	 *            Message de fermeture
	 * @since V3.4.0
	 */
	public void deconnecterClients(LinkedList clients, HashMap messageFermeture) {

		// Parourir la liste des clients en parametre
		//
		Iterator i = clients.iterator();
		String pseudo;
		while (i.hasNext()) {

			// Recuperer le pseudo en cours de parcours
			//
			pseudo = (String) i.next();

			// Deconnecter le client associe au pseudo
			//
			deconnecterClient(pseudo, messageFermeture);
		}
	}

	/**
	 * Permet d'enregistrer dans le fichier de log une nouvelle ligne datee
	 * 
	 * @param toLog
	 *            Chaine a ajouter au log
	 * @since V3.2.0
	 */
	private void aLogger(String toLog) {

		// Calculer la date
		//
		Date actuelle = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(
						DateFormat.MEDIUM, DateFormat.MEDIUM);

		// La mettre au format adequat
		//
		String dat = dateFormat.format(actuelle);

		// Ajouter la ligne datee au log
		//
		log.add("DATE:'" + dat + "' |" + toLog);

		// Enregistrer le fichier de log
		//
		String[] toStore = {};
		toStore = (String[]) log.toArray(toStore);
		// Texte.store(toStore, cheminLog);
	}

	/**
	 * Ferme la session
	 * 
	 * @param messageFermeture
	 *            Message de prevention de fermeture
	 * @since V2.0.0
	 */
	public void fermer(HashMap messageFermeture) {

		// Deconnecter tous les clients
		//
		deconnecterClients(messageFermeture);

		// Cesser emission et reception
		//
		stopperEmission();
		stopperReception();

		// Enregistrer dans le log la fermeture de la session
		//
		aLogger("[ARRET DE LA SESSION]");
	}

	// ------------------------------------------------------- //
	// --------- CLASSE INTERNE ThreadAttenteClient ---------- //
	// ------------------------------------------------------- //

	/**
	 * Thread permettant d'attendre les clients et de distribuer les ports
	 * respectifs dynamiquement
	 * 
	 * @author Sebastien PETILLON
	 * @version 3.4.0
	 * @since V2.0.0
	 */
	private class ThreadAttenteClient extends Observable implements Observer,
					Runnable {

		public void run() {

			// Tant que les conditions d'attente de clients sont verifiees
			//
			while (enAttente() && attente.get()) {

				// Creer le HashMap de configuration des noeuds
				// (distribution et noeud serveur)
				//
				HashMap config = new HashMap();

				// Creer le HashMap d'echanges
				//
				HashMap msg = new HashMap();

				// Remplir le HashMap de configuration pour le noeud de
				// distribution
				//
				config.put("host", host);
				config.put("portDefaut", distribPort);
				config.put("mode", "Serveur");
				config.put("pseudo", "noeudDistrib");

				// Verifier que l'attente n'ait pas change
				//
				if (attente.get() == false) {
					return;
				}

				// Redefinir le noeud de distribution
				//
				distribNoeud = new NoeudG_TCP(config, timeout, this);

				// Verifier que l'attente n'ait pas change en cas de timeout
				//
				if (attente.get() == false) {
					return;
				}

				// Commencer l'emission et la reception pour permettre la
				// communication du pseudo et du port a utiliser
				//

				distribNoeud.debuterEmission();
				distribNoeud.debuterReception();

				// Recuperer la reponse
				//
				msg = (HashMap) distribNoeud.attendreMessage();
				String pseudo = (String) msg.get("pseudo");

				// Reinitialiser le HashMap d'echanges
				//
				msg = new HashMap();

				// Ajouter le port a transmettre
				//
				msg.put("portDefaut", port);

				// Ajouter le message pour transmettre le port attribue
				//
				distribNoeud.envoyerMessage(msg);

				// Reinitialiser le HashMap d'echanges
				//
				msg = new HashMap();

				// Attendre la confirmation de reception
				//
				msg = (HashMap) distribNoeud.attendreMessage();

				// Fermer le noeud de distribution
				//
				distribNoeud.stopperEmission();
				distribNoeud.stopperReception();
				distribNoeud.fermerForce();

				// Le r�initialiser a null
				//
				distribNoeud = null;

				// Creer le nom du noeud
				//
				String pseudoS = pseudo + ":noeudSession:" + distribPort;

				// Reinitialiser le HashMap de configuration
				//
				config = new HashMap();

				// Remplir le HashMap de configuration
				//
				config.put("host", pseudo);
				config.put("portDefaut", port);
				config.put("mode", "Serveur");
				config.put("pseudo", pseudoS);
				config.put("attenteAvantFermeture", timeOutDeconnexionClient);

				// Verifier que l'attente n'ait pas change
				//
				if (attente.get() == false) {
					return;
				}

				// Creer le noeud attribue au client
				//
				NoeudG_TCP noeud = null;
				noeud = new NoeudG_TCP(config, null, this);

				// Modifier le pseudo pour assurer l'unicite des pseudos
				//
				pseudo = pseudo
								+ noeud.obtenirSocket()
												.getRemoteSocketAddress();

				// Ajouter ce noeud a la liste avec comme cle le pseudo
				//
				noeudsServeurs.put(pseudo, noeud);

				// Enregistrer dans le log l'arrivee du nouveau client
				//
				aLogger("<Connexion-client>\t<Port=" + port + ">\t<Pseudo="
								+ pseudo + ">");

				// Incrementer le numero de port pour la prochaine attribution
				//
				port++;

				// Incrementer le nombre de clients
				//
				nbClient++;
				System.out.println(nbClient);

			}
		}

		/**
		 * Verification des conditions d'attente
		 * 
		 * @return boolean : True s'il faut continuer l'attente, False s'il faut
		 *         arr�ter
		 * @since V3.0.0
		 */
		private boolean enAttente() {

			// Verifier si une eventuelle limite numerique de client est
			// depassee
			//
			if (nbMaxConnexions != null) {
				if (nbClient >= nbMaxConnexions) {
					return false;
				}
			}

			// Sinon continuer
			//
			return true;
		}

		/**
		 * Implementation du pattern Observer/Observable dans le thread
		 * 
		 * @since V3.0.0
		 */
		public void update(Observable o, Object arg) {

			// Si l'objet est un Noeud
			//
			if (o instanceof NoeudG_TCP) {

				// Recuperer son nom
				//
				String nom = ((NoeudG_TCP) o).pseudo;

				// Recuperer la partie precedant les :
				//
				String[] w = nom.split(":");

				// Verifier qu'il ne s'agit pas du noeud de distribution
				//
				if (arg instanceof HashMap && !nom.equals("distribNoeud")) {

					// Ajouter au dictionnaire l'emetteur du message pour
					// l'observeur
					//
					((HashMap) arg).put("emetteur", w[0]);
				}

			}

			// Notifier les observeurs
			//
			notifyObservers(arg);
			setChanged();
		}
	}

	/**
	 * Implementation du pattern Observer/Observable dans la session
	 * 
	 * @since V3.0.0
	 */
	public void update(Observable o, Object arg) {

		// Si l'objet est un String
		//
		if (arg instanceof String) {

			// S'il est egal a STOP
			//
			if (((String) arg).equals("STOP")) {

				// Un observateur previent qu'il faut arreter l'attente
				// Affecter faux a l'attente
				//
				attente.set(false);
			}
		}

		// Notifier les observeurs
		notifyObservers(arg);
		setChanged();
	}

	// ------------------------------------------------------- //
	// --------- CLASSE INTERNE ThreadCouperAttente ---------- //
	// ------------------------------------------------------- //

	/**
	 * Thread de coupure de l'attente
	 * 
	 * @author Sebastien PETILLON
	 * @version 3.4.0
	 * @since V3.1.0
	 */
	private class ThreadCouperAttente extends Thread {

		@Override
		public void run() {

			while (true) {

				// Dans le cas d'arret a cause du nombre de clients
				//
				if (attente.get() == false) {
					break;
				}

				// Pour eviter le spam du temps restant, patienter
				//
				try {
					this.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (dateF != null) {

					// Mettre a jour le timeout
					//
					timeout = (int) (dateF.getTimeInMillis() - Calendar
									.getInstance().getTimeInMillis());

					// Informer du temps restant
					//
					if (verbeuse) {
						if (timeout > 0) {
							System.out.println("Temps restant (ms): " + timeout);
						} else {
							System.out.println("Temps �coul�.");
						}
					}
				}

				// Verifier si une eventuelle limite temporelle est depassee
				//
				if (dateF != null) {
					if (dateF.before(Calendar.getInstance())) {

						// Mettre l'attente a faux
						//
						attente.set(false);
						break;
					}
				}
			}
		}
	}
}