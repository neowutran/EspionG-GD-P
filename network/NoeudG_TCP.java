package network;


//
// IUT de Nice / Departement informatique / Module APO-Java
// Annee 2012_2013 - Composants reseaux generiques sous TCP/IP
//
// Classe NoeudG_TCP
//
//
// + Version 1.0.0 : version initial
// + Cr�ation de Noeud Client ou Serveur
// + Ajout de m�thodes pour activer / d�sactiver l'�mission / r�ception des
// messages
// + Ajout de m�thodes pour ajouter et retirer des messages.
// + Initialisation des flux d'entr�e et de sortie
//
// + Version 2.0.0 : + Modification du constructeur
// + Modification des Threads d'emission et r�ception en deux classes internes
// + Modification de la m�thode fermer (envoi d'un message possible)
// + Ajout d'une �num�ration interne : ModeNoeudG
//
// + Version 2.1.0 : + Ajout d'une m�thode defautConnecter dans la classe
// interne
// Elle a �t� impl�ment� pour ImagesMyst�rieuses, elle a pour but de se
// connecter sur le port 8080
// et d'attendre un message du serveur pour se reconnecter sur le port fourni
// par celui-ci
// + Ajout de fichier de configuration pour le port par d�faut
//
// + Version 3.0.0 : + Mise en place du design pattern observer / observable
// pour les messages recus
//
// + Version 3.1.0 : + Utilisation de BlockingQueue et AtomicBoolean pour palier
// les problemes de temporisation
// + R��criture des m�thodes debuterEmission, stopperEmission, debuterReception,
// stopperReception
// envoyerMessage et attendreMessage
// + R��criture des classes internes ThreadEmission et ThreadReception
//
// + Version 3.1.1 : + R��criture de la methode fermer
// + Ajout de la methode fermerForcer permettant de fermer le noeud meme en cas
// d'attente
// d'un message
//
// + Version 3.2.1 : + Modification de la classe interne ServeurG_TCP
// + Prise en compte d'un timeout pour l'attente de client
//
// + Version 3.3.1 : + Mise en place de fichier de configuration
//
// + Version 3.3.2 : + Modification des methodes fermer et fermerForce
//
// Auteur : T. Cazorla

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Mod�lisation d'un noeud de communication TCP/IP. <br/>
 * Il est alors possible de mod�liser un client ou un serveur.
 * 
 * @author Tony CAZORLA
 * @version 3.1.1
 */

public class NoeudG_TCP extends Observable {

	// Constantes pour le mode de configuration
	//
	public final static ModeNoeudG	SERVEUR	= ModeNoeudG.SERVEUR;
	public final static ModeNoeudG	CLIENT	= ModeNoeudG.CLIENT;

	// Description de la connexion
	//
	private String					adresse;
	private int						portConnecte;

	// Socket li� au noeud
	//
	private Socket					socketSupport;
	private Socket					socketDef;
	private ServerSocket			serveur;

	// Concerver le mode d'action du noeudG Client/Serveur
	//
	private ModeNoeudG				modeCourant;
	String							pseudo;

	// Liste des messages �chang�s
	//
	private BlockingQueue			listeEmission;					// Liste des
																	// messages
																	// a envoyer
	private BlockingQueue			listeReception;				// Liste des
																	// messages
																	// a
																	// recevoir

	// Statut de l'envoi
	//
	private final AtomicBoolean		statusEmission;				// Status
																	// autorisationemettre
	private final AtomicBoolean		statusReception;				// Status
																	// autorisationrecevoir

	// Buffers d'entr�e et de sortie
	//
	private ObjectInputStream		bIn;							// Buffer
																	// entree en
																	// mode
																	// "bytes"
	private ObjectOutputStream		bOut;							// Buffer
																	// sortie en
																	// mode
																	// "bytes"

	// Etat du noeud
	//
	private final AtomicBoolean		isConnecte;
	private final AtomicBoolean		isRunning;
	private AtomicBoolean			attenteClient;

	// Temps d'attente pour la connexion de client au serveur
	//
	private Integer					timeout;

	// Fichier de configuration
	//
	private HashMap					config;

	// --- Premier constructeur normal
	//
	/**
	 * Cr�er un nouveau NoeudG_TCP avec un mode d'utilisation (client/serveur).
	 * 
	 * @param host
	 *            adresse IP de l'h�te, String.
	 * @param port
	 *            numero de port utilise, int.
	 * @param mode
	 *            mode de connexion : Serveur/Client, Enumeration.
	 * @param pseudo
	 *            pseudo du noeud, String.
	 * @param timeout
	 *            timeout pour l'attente d'un client, Integer.
	 * @param observateur
	 *            observateur du noeud, Observer.
	 * @since 1.0.0
	 */
	public NoeudG_TCP(String host, int port, ModeNoeudG mode, String pseudo,
					Integer timeout, Observer observateur) {

		// Memoriser le mode du noeud
		//
		modeCourant = mode;

		if (observateur != null) {
			addObserver(observateur);
		}

		// Cr�er les listes de stockage des messages
		//
		listeEmission = new LinkedBlockingQueue();
		listeReception = new LinkedBlockingQueue();

		// Initialiser les statuts de r�ception et d'�mission
		//
		statusEmission = new AtomicBoolean(false);
		statusReception = new AtomicBoolean(false);
		isRunning = new AtomicBoolean(false);
		isConnecte = new AtomicBoolean(false);
		attenteClient = new AtomicBoolean(false);

		// Lancer les threads de r�ception et �mission
		//
		new ThreadEmission().start();
		new ThreadReception().start();

		// Controler le mode
		//
		switch (mode) {

		// Traitement serveur
		//
			case SERVEUR:
				// Cr�er le noeud serveur
				//
				ServeurG_TCP noeudS = new NoeudG_TCP.ServeurG_TCP(host, port);

				// Sauvegarder le timeout pour l'attente de client
				//
				this.timeout = timeout;

				// Lancer l'�coute de ce serveur
				//
				noeudS.run();

				break;

			// Traitement client
			//
			case CLIENT:
				// Cr�er le noeud client
				//
				ClientG_TCP noeudC = new NoeudG_TCP.ClientG_TCP(host, port,
								pseudo);

				// Lancer la tentative de connexion
				//
				noeudC.run();

				break;
		}
	}

	// --- Second constructeur normal
	//
	/**
	 * Cr�er un nouveau NoeudG_TCP configurer via un fichier de configuration.
	 * 
	 * @param config
	 *            Fichier de configuration pour le nouveau noeud.
	 * @param timeout
	 *            timeout pour l'attente d'un client, Integer.
	 * @param observateur
	 *            observateur du noeud, Observer.
	 * @since 3.2.1
	 */
	public NoeudG_TCP(HashMap config, Integer timeout, Observer observeur) {

		String host = null;
		Integer port = null;

		// Sauvegarder le fichier de configuration
		//
		this.config = config;

		// Configurer le noeud
		//
		configurer(config);

		// Sauvegarder l'host
		//
		if ((String) config.get("host") != null) {
			host = (String) config.get("host");
		} else {
			host = "127.0.0.1";
		}

		// Sauvegarder le port
		//
		if ((Integer) config.get("portDefaut") != null) {
			port = (Integer) config.get("portDefaut");
		} else {
			port = 8080;
		}

		// Ajouter un observeur du noeud
		//
		if (observeur != null) {
			addObserver(observeur);
		}

		// Cr�er les listes de stockage des messages
		//
		listeEmission = new LinkedBlockingQueue();
		listeReception = new LinkedBlockingQueue();

		// Initialiser les statuts de r�ception et d'�mission
		//
		statusEmission = new AtomicBoolean(false);
		statusReception = new AtomicBoolean(false);
		isRunning = new AtomicBoolean(false);
		isConnecte = new AtomicBoolean(false);
		attenteClient = new AtomicBoolean(false);

		// Lancer les threads de r�ception et �mission
		//
		new ThreadEmission().start();
		new ThreadReception().start();

		// Controler le mode
		//
		switch (modeCourant) {

		// Traitement serveur
		//
			case SERVEUR:

				// Cr�er le noeud serveur
				//
				ServeurG_TCP noeudS = new NoeudG_TCP.ServeurG_TCP(
								(String) config.get("host"),
								(Integer) config.get("portDefaut"));

				// Sauvegarder le timeout pour l'attente de client
				//
				this.timeout = timeout;

				// Lancer l'�coute de ce serveur
				//
				noeudS.run();

				break;

			// Traitement client
			//
			case CLIENT:
				// Cr�er le noeud client
				//
				ClientG_TCP noeudC = new NoeudG_TCP.ClientG_TCP(
								(String) config.get("host"),
								(Integer) config.get("portDefaut"), pseudo);

				// Lancer la tentative de connexion
				//
				noeudC.run();

				break;
		}
	}

	// --- M�thode configurer
	//
	/**
	 * /**
	 * Permettre la configuration du nouveau noeud.
	 * 
	 * @param config
	 *            Fichier de configuration pour le nouveau noeud.
	 * @since 3.2.1
	 */
	private void configurer(HashMap config) {

		// Sauvegarder le pseudo du client
		//
		if ((String) config.get("pseudo") != null) {
			this.pseudo = (String) config.get("pseudo");
		} else {
			this.pseudo = "Client";
		}

		// COnfigurer le modeCourant d'utilisation du noeud
		//
		if ((String) config.get("mode") != null) {
			String mode = (String) config.get("mode");

			if (mode.equals("Client")) {
				this.modeCourant = ModeNoeudG.CLIENT;
			}

			if (mode.equals("Serveur")) {
				this.modeCourant = ModeNoeudG.SERVEUR;
			}
		} else {
			this.modeCourant = ModeNoeudG.CLIENT;
		}
	}

	// ### ACCESSEURS ###

	// --- M�thode debuterEmission - d�buter l'�mission de donn�es
	//
	/**
	 * Permettre de d�buter l'emission.
	 * 
	 * @return void
	 * @since 1.0.0
	 */
	public void debuterEmission() {

		statusEmission.set(true);

		synchronized (statusEmission) {
			statusEmission.notifyAll();
		}
	}

	// --- M�thode debuterReception - d�buter la r�ception de donn�es
	//
	/**
	 * Permettre de d�buter la reception.
	 * 
	 * @return void
	 * @since 1.0.0
	 */
	public void debuterReception() {

		this.statusReception.set(true);

		synchronized (statusReception) {
			statusReception.notifyAll();
		}
	}

	// --- M�thode stopperEmission - stopper l'�mission de donn�es
	//
	/**
	 * Permettre de stopper l'emission.
	 * 
	 * @return void
	 * @since 1.0.0
	 */
	public void stopperEmission() {

		this.statusEmission.set(false);
	}

	// --- M�thode stopperReception - stopper la r�ception de donn�es
	//
	/**
	 * Permettre de stopper la r�ception.
	 * 
	 * @return void
	 * @since 1.0.0
	 */
	public void stopperReception() {

		statusReception.set(false);
	}

	// --- M�thode obtenirSocket - r�cup�rer le socket associ� au noeud
	//
	/**
	 * Permettre de conna�tre la socketSupport utilis�e, utile pour les tests et
	 * les debug notamment.
	 * 
	 * @return socket
	 *         Retourne la socket utilis�e pour les �changes.
	 * @since 1.0.0
	 */
	public Socket obtenirSocket() {

		return socketSupport;
	}

	// --- M�thode obtenirMessages - r�cup�rer la liste de messages
	//
	/**
	 * Obtenir la liste des messages envoy�s.
	 * 
	 * @return BlockingQueue
	 *         Retourne la liste des messages � envoyer.
	 * @since 3.1.0
	 */
	public BlockingQueue obtenirMessages() {

		return listeEmission;
	}

	// --- M�thode obtenirReponses - r�cup�rer la liste de messages
	//
	/**
	 * Obtenir la liste des messages recus.
	 * 
	 * @return BlockingQueue
	 *         Retourne la liste des messages recus.
	 * @since 3.1.0
	 */
	public BlockingQueue obtenirReponses() {

		return listeReception;
	}

	// --- M�thode obtenirStatusEmission - r�cup�rer le statut d'envoi
	//
	/**
	 * Conna�tre l'�tat concernant l'�mission.
	 * 
	 * @return boolean
	 *         Retourne le status � propos de l'�mission.
	 * @since 1.0.0
	 */
	public boolean obtenirStatusEmission() {

		return statusEmission.get();
	}

	// --- M�thode obtenirStatusReception - r�cup�rer le statut de r�ception
	//
	/**
	 * Conna�tre l'�tat concernant la r�ception.
	 * 
	 * @return boolean
	 *         Retourne le status � propos de la r�ception.
	 * @since 1.0.0
	 */
	public boolean obtenirStatusReception() {

		return statusReception.get();
	}

	// Methode retirerReponse
	//
	/**
	 * Obtenir le premier message courant recu et le retire.
	 * 
	 * @return HashMap
	 *         Le premier message recu sous forme de HashMap.
	 * @since 1.0.0
	 */
	private HashMap retirerReponse() {

		HashMap msg = null;

		if (listeReception.size() == 0) {
			return null;
		}

		// Executer une operation atomique pour obtenir le premier
		// message courant recu et le retirer de la liste
		//
		synchronized (listeReception) {
			msg = (HashMap) listeReception.poll();
		}

		// Restituer le resultat
		//
		return msg;
	}

	// --- Methode initFlux
	//
	/**
	 * Initialiser les flux de communication.
	 * 
	 * @param Socket
	 *            La socket sur laquelle l'initialisation des flux entrant et
	 *            sortant aura lieu.
	 * @return boolean
	 *         La r�ussite ou l'�chec de l'op�ration
	 * @since 1.0.0
	 */
	private boolean initFlux(Socket s) {

		// Creer le flux de sortie
		//
		OutputStream streamOut = null;
		try {
			streamOut = s.getOutputStream();
		} catch (Exception e) {
			return false;
		}
		if (streamOut == null) {
			return false;
		}

		// Creer le buffer de sortie
		//
		try {
			bOut = new ObjectOutputStream(streamOut);
		} catch (Exception e) {
			return false;
		}
		if (bOut == null) {
			return false;
		}

		// Creer le flux d'entree
		//
		InputStream streamIn = null;
		try {
			streamIn = s.getInputStream();
		} catch (Exception e) {
			return false;
		}
		if (streamIn == null) {
			return false;
		}

		// Creer le buffer d'entree
		//
		try {
			bIn = new ObjectInputStream(streamIn);
		} catch (Exception e) {
			return false;
		}
		if (bIn == null) {
			return false;
		}

		return true;
	}

	// --- Methode envoyerMessage
	//
	/**
	 * Ajouter un message � la liste d'�mission.
	 * 
	 * @param Object
	 *            Message � envoyer
	 * @return boolean
	 * @since 1.0.0
	 */
	public boolean envoyerMessage(Object msg) {

		// Controler la validit� du message
		//
		if (msg == null) {
			return false;
		}

		listeEmission.offer(msg);
		return true;
	}

	// --- Methode attendreMessage
	//
	/**
	 * Attendre un message (blocant pour le thread l'utilisant)
	 * 
	 * @return HashMap
	 *         Le message recu apr�s attente de celui-ci
	 * @since 1.0.0
	 */
	public Object attendreMessage() {

		attenteClient = new AtomicBoolean(true);
		Object msg = null;

		try {
			msg = listeReception.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		attenteClient = new AtomicBoolean(false);

		return msg;
	}

	// --- M�thode fermerForce - ferme le noeud sans verification
	//
	/**
	 * Permettre de forcer la fermeture avec un �ventuel message.
	 * 
	 * @param messageFermeture
	 *            Message de fermeture (null autoris�).
	 * @return boolean
	 *         Echec ou r�ussite de la fermeture
	 * @since 3.1.0
	 */
	public boolean fermerForce() {

		try {
			isConnecte.set(false);
			isRunning.set(false);

			switch (modeCourant) {
			// Traitement serveur
			//
				case SERVEUR:
					if (serveur != null) {
						serveur.close();
					}

					break;

				// Traitement client
				//
				case CLIENT:
					if (socketSupport != null) {
						socketSupport.close();
					}

					if (socketDef != null) {
						socketDef.close();
					}
					break;
			}

			// Fermer les buffers
			//
			if (bIn != null) {
				bIn.close();
			}

			if (bOut != null) {
				bOut.close();
			}

		} catch (Exception e) {
			return false;
		}

		return true;
	}

	// --- M�thode fermerForce - ferme le noeud sans verification
	//
	/**
	 * Permettre de forcer la fermeture avec un �ventuel message, et un timeout
	 * .
	 * 
	 * @param messageFermeture
	 *            Message de fermeture (peut �tre null).
	 * @param timingSec
	 *            Timeout en secondes.
	 * @return boolean
	 *         Echec ou r�ussite de la fermeture.
	 * @since 3.1.0
	 */
	public boolean fermerForce(HashMap messageFermeture, int timingSec) {

		envoyerMessage(messageFermeture);

		Calendar fin = Calendar.getInstance();
		fin.add(Calendar.SECOND, timingSec);

		while (Calendar.getInstance().before(fin)) {
			fermer();
		}

		return fermerForce();
	}

	// --- M�thode fermer - ferme le noeud avec verification
	//
	/**
	 * Permettre de fermer le noeud. Echoue si un message est en attente.
	 * 
	 * @param messageFermeture
	 *            Message de fermeture (peut �tre null).
	 * @return HashMap
	 *         Echec ou r�ussite de la fermeture
	 * @since 1.0.0
	 */
	public boolean fermer() {

		if (attenteClient.equals(new AtomicBoolean(false))) {
			return fermerForce();
		} else {
			return false;
		}

	}

	// ***********************************************************************//
	// Classes internes //
	// ***********************************************************************//

	// *******************//
	// ClientG_TCP //
	// *******************//

	/**
	 * Mod�lisation du ClientG_TCP
	 * 
	 * @author Alain THUAIRE (v1.0.0)
	 * @author Tony CAZORLA
	 * @version 2.0.0
	 */
	private class ClientG_TCP extends Thread {

		// Adresse du serveur � contacter
		//
		private final String	adresseServeur;

		// Port du serveur � contacter
		//
		private int				portServeur;

		// --- Constructeur par d�faut
		//
		//
		/**
		 * Cr�er un ClientG_TCP qui se connecte � l'host : 127.0.0.1 au port
		 * 8080.
		 * 
		 * @since 1.0.0
		 */
		private ClientG_TCP() {

			// Appeler le constructeur de la super class
			//
			super();

			// Stocker les informations de connexion
			//
			adresseServeur = "127.0.0.1";
			portServeur = 8080;

			// Initialiser les listes qui serviront aux echanges
			//
			listeEmission = new LinkedBlockingQueue();
			listeReception = new LinkedBlockingQueue();

			// D�sactiver l'�mission
			//
			statusEmission.set(false);
		}

		// --- Premier constructeur normal
		//
		/**
		 * Cr�er un ClientG_TCP avec un host et port donn�s.
		 * 
		 * @param host
		 *            Adresse IP du serveur
		 * @param nom
		 *            Pseudo du noeud
		 * @since 2.0.0
		 */
		private ClientG_TCP(String host, int port, String nom) {

			// Appeler le constructeur de la super class
			//
			super(nom);

			// Stocker les informations de connexion
			//
			adresseServeur = host;
			portServeur = port;
			pseudo = nom;

			// Initialiser les listes qui serviront aux echanges
			//
			listeEmission = new LinkedBlockingQueue();
			listeReception = new LinkedBlockingQueue();

			// D�sactiver l'�mission
			//
			statusEmission.set(false);
		}

		// --- Methode connecter - initialiser la connexion avec le serveur
		//
		/**
		 * Connecter le noeud Client au Serveur.
		 * Attention pour une utilisation avec la session, n�cessite une
		 * configuration pour �x�cuter la m�thode : defautConnecte.
		 * 
		 * @return boolean
		 *         Echec ou r�ussite de l'op�ration
		 * @since 1.0.0
		 */
		private boolean connecter() {

			// Creer une connexion avec le serveur cible
			//
			while (true) {

				// Se connecter sur le port par d�faut (8080) pour recuperer le
				// port de connection definitive au serveur
				//
				if (config != null) {
					if ((Boolean) config.get("utiliseParSession") != null) {
						if ((Boolean) config.get("utiliseParSession") == true) {
							defautConnecte();
						}
					}
				}

				// Afficher un message
				//
				System.out.print("Tentative de connexion...");

				// Creer la socket support
				//
				try {
					socketSupport = new Socket(adresseServeur, portServeur);
					isConnecte.set(true);
					adresse = socketSupport.getLocalAddress().getHostAddress();
					portConnecte = portServeur;
				} catch (BindException e) {
					e.printStackTrace();
					System.out.println("Tentative de connection ...");
				} catch (UnknownHostException e) {
					e.printStackTrace();
					System.out.println("Echec : adresse IP de l'hote inconnue !");
					break;
				} catch (IOException e) {
					e.printStackTrace();
					System.out.println("Echec : probl�me d'entree/sortie !");
				}

				// Controler la validite de cette socket
				//
				if (socketSupport != null) {
					break;
				}
			}

			// Indiquer le succes
			//
			System.out.println("\tConnexion au serveur r�ussie !");

			// Initialiser les flux entrant et sortant de la connexion
			//
			return initFlux(socketSupport);
		}

		// --- Methode defautConecter - utiliser pour image myst�rieuse
		//
		/**
		 * Connecter le client sur le port par d�faut pour un premier �change
		 * avec le serveur.
		 * En retour, le client recevra un port sur lequel il doit se connecter
		 * d�finitivement.
		 * 
		 * @return void
		 * @since 2.0.0
		 */
		private void defautConnecte() {

			// Creer la socket support
			//
			Socket socketDef = null;

			try {
				if ((Integer) config.get("portDefaut") != null) {
					socketDef = new Socket(adresseServeur,
									(Integer) config.get("portDefaut"));
				} else {
					socketDef = new Socket(adresseServeur, 9090);
				}
			} catch (BindException e) {
				e.printStackTrace();
				System.out.println("Tentative de connection ...");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("Echec : adresse IP de l'h�te inconnue !");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("Echec : probl�me d'entr�e/sortie !");
			}

			// Initialiser les flux entrant et sortant de la connexion
			//
			initFlux(socketDef);

			// D�buter les �missions et r�ceptions
			//
			debuterEmission();
			debuterReception();

			// Cr�er le message pour envoyer le pseudo au serveur
			//
			HashMap msg = new HashMap();
			msg.put("pseudo", pseudo);

			// Envoyer le message
			//
			envoyerMessage(msg);

			// Attendre la r�ponse du serveur
			//
			HashMap rec = (HashMap) attendreMessage();

			// R�cup�rer le nouveau port de connexion
			//
			portServeur = (Integer) rec.get("portDefaut");

			// Cr�er le HashMap d'�change
			//
			msg = new HashMap();
			msg.put("reponse", "ok");

			// R�pondre ok pour confirmer la reception du port
			//
			envoyerMessage(msg);

			// On ferme ensuite la socket
			//
			fermer();
		}

		// --- Methode run
		/**
		 * Etablir la connexion avec une serveur cible.
		 * 
		 * @return void
		 * @since 1.0.0
		 */
		@Override
		public void run() {

			// Etablir la connexion avec le serveur cible
			//
			connecter();
		}

	}

	// ******************//
	// ServeurG_TCP //
	// ******************//
	/**
	 * Mod�lisation du ServeurG_TCP.
	 * 
	 * @author Alain THUAIRE (v1.0.0)
	 * @author Tony CAZORLA
	 * @version 2.0.0
	 */
	private class ServeurG_TCP extends Thread {

		private final int	portReception;

		// --- Premier constructeur normal
		/**
		 * Cr�er un serveurG_TCP recevant des connexions seulement sur le port
		 * 8080.
		 * 
		 * @since 1.0.0
		 */
		public ServeurG_TCP() {

			super();

			portReception = 8080;
			listeEmission = new LinkedBlockingQueue();
			listeReception = new LinkedBlockingQueue();
			statusReception.set(false);
			isRunning.set(true);

		}

		// --- Second constructeur normal
		/**
		 * Cr�er un serveurG_TCP recevant des connexions sur le port indiqu�.
		 * 
		 * @param nomThread
		 *            Parametre inutile pour conserver la meme signature que le
		 *            Client.
		 * @param port
		 *            Port de connexion.
		 * @since 2.0.0
		 */
		public ServeurG_TCP(String nomThread, int port) {

			super(nomThread);

			portReception = port;
			listeEmission = new LinkedBlockingQueue();
			listeReception = new LinkedBlockingQueue();
			statusReception.set(false);
			isRunning.set(true);
		}

		// --- Methode accepter
		/**
		 * Accepter un ClientG_TCP.
		 * 
		 * @return boolean
		 *         Echec ou r�ussite de l'op�ration.
		 * @since 1.0.0
		 */
		private boolean accepter() {

			// Creer la socket serveur
			//
			try {
				serveur = new ServerSocket(portReception);
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("Erreur lors de la creation de la socket serveur");
				return false;
			}

			// Modifier le timeout permit pour l'attente d'un client
			//
			try {
				if (timeout != null) {
					if (timeout > 0) {
						serveur.setSoTimeout(timeout);
					}
				}

				// En attente d'acceptation d'un nouveau client
				//
				socketSupport = serveur.accept();
				isRunning.set(true);
				adresse = socketSupport.getLocalAddress().getHostAddress();
				portConnecte = portReception;

				// Afficher un message de r�ussite
				//
				System.out.println("Client connect� ! Adresse : "
								+ socketSupport.getLocalAddress()
												.getHostAddress() + ", port : "
								+ socketSupport.getLocalPort());
			} catch (SocketTimeoutException s) {

				setChanged();

				if ((String) config.get("messageArretTimeout") != null) {
					notifyObservers(config.get("messageArretTimeout"));
				} else {
					notifyObservers("STOP");
				}

				return false;
			}

			catch (Exception e) {
				return false;
			}

			// Intialiser les flux
			//
			return initFlux(socketSupport);
		}

		// --- Methode run
		//
		/**
		 * Etablir la connexion avec un client cible.
		 * 
		 * @return void
		 * @since 1.0.0
		 */
		@Override
		public void run() {

			// Etablir la connexion avec le client cible.
			//
			accepter();
		}
	}

	// --- Methode isRunning
	//
	/**
	 * Savoir si le serveur est en execution.
	 * 
	 * @return boolean
	 *         True si le serveur tourne, False s'il est arr�t�.
	 * @since 2.0.0
	 */
	public boolean isRunning() {

		return isRunning.get();
	}

	// --- Methode estConnecte
	//
	/**
	 * Savoir si le noeud est connect�.
	 * 
	 * @return boolean
	 *         True si le noeud est connect�, False s'il ne l'est pas.
	 * @since 2.0.0
	 */
	public boolean estConnecte() {

		return isConnecte.get();
	}

	// --- Methode toString
	//
	/**
	 * Repr�senter la valeur courante de l'object.
	 * 
	 * @override @see java.lang.String de la classe @see java.lang.Object.
	 * @return Le string repr�sentant la valeur courante.
	 * @since 2.0.0
	 */
	@Override
	public String toString() {

		return "| NoeudG - Adresse : " + adresse + " | Port : " + portConnecte
						+ " -" + " | Pseudo : " + pseudo + " |";
	}

	// ******************//
	// ThreadEmission //
	// ******************//
	/**
	 * Effectuer les �missions de messages.
	 * 
	 * @author Tony CAZORLA
	 * @version 1.0.0
	 */
	private class ThreadEmission extends Thread {

		// --- M�thode run
		//
		/**
		 * Permettre l'�mission de message.
		 * 
		 * @override M�thode run de la classe Thread.
		 * @return void
		 * @since 1.0.0
		 */
		@Override
		public void run() {

			Object msg = null;

			while (true) {

				// Controler le status d'�mission
				//
				if (!statusEmission.get())
					synchronized (statusEmission) {
						try {
							statusEmission.wait();
						} catch (InterruptedException e) {
						}
					}

				// Envoyer le message
				//
				try {
					// R�cup�rer le message de la liste des messages � envoyer
					//
					msg = listeEmission.poll();

					// Envoyer le message via le flux de sortie
					//
					if (bOut != null) {
						bOut.writeObject(msg);

						// Vider le flux de sortie
						//
						bOut.flush();
					}
				} catch (IOException e) {
				}
			}
		}
	}

	// ******************//
	// ThreadReception //
	// ******************//
	/**
	 * Permettre la r�ception de message.
	 * 
	 * @author Tony CAZORLA
	 * @version 1.0.0
	 */
	private class ThreadReception extends Thread {

		// --- M�thode run
		//
		/**
		 * Permettre une r�ception �ventuelle de message en continue.
		 * 
		 * @override M�thode run de la classe Thread.
		 * @return void
		 * @since 1.0.0
		 */
		@Override
		public void run() {

			Object msg = null;

			while (true) {

				// Controler le status de reception
				//
				if (!statusReception.get()) {
					synchronized (statusReception) {
						try {
							statusReception.wait();
						} catch (InterruptedException e) {
						}
					}
				}

				// Receptionner le message
				//
				try {
					msg = bIn.readObject();
					bIn.reset();
				}

				// Traiter le cas ou l'autre extremite de la socket disparait
				// sans coordination prealable au niveau applicatif (OSI - 7).
				//
				// Ce cas se produit quand l'objet "socket" distant est detruit
				// (mort du thread distant par exemple)
				//
				catch (SocketException e) {
				}

				// Traiter le cas ou l'autre extremite ferme la socket sans
				// coordination prealable au niveau applicatif (OSI - 7)
				//
				catch (EOFException e) {
				}

				// Traiter le cas d'autres exceptions relatives aux IO
				//
				catch (IOException e) {
				}

				catch (ClassNotFoundException e) {
				}

				// Traiter les autres cas d'exceptions
				//
				catch (Exception e) {
				}

				// Copier le message dans la liste et notifier les observateurs
				//
				if (msg != null) {
					setChanged();
					notifyObservers(msg);
					listeReception.offer(msg);
				}
			}
		}
	}

	// ******************//
	// Chrono //
	// ******************//
	/**
	 * Mod�lisation d'un Chrono
	 * 
	 * @author Tony CAZORLA
	 * @version 1.0.0
	 */
	public static class Chrono {

		// --- M�thode attendre
		//
		public static void attendre(int tms) {

			// Attendre tms millisecondes, en bloquant le thread courant
			//
			/**
			 * Attendre un temps donn� en bloquand le thread courant.
			 * 
			 * @param tms
			 *            Temps � attendre.
			 * @return void
			 * @since 1.0.0
			 */
			try {
				Thread.currentThread().sleep(tms);
			} catch (InterruptedException e) {
			}
		}
	}

	// ***********************************************************************//
	// Enum�ration des modes //
	// ***********************************************************************//
	private enum ModeNoeudG {
		SERVEUR, CLIENT
	}
}