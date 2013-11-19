package espionGD;

import groovy.lang.GroovyShell;

import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import library.ValidatorComposer;

public class EspionGD implements Observer {

	private NoeudG_TCP								_noeudEspionP;
	private final SessionG							_sessionHamecons;

	private HashMap									_regles;
	private Integer 								gap = 0;

	private List<String>							identifiants;

	private final Map<String, ValidatorComposer>	validateurs	= new HashMap<String, ValidatorComposer>();
	private final Map<String, String>				operation	= new HashMap<String, String>();

	/**
	 * @param adresseEspionP
	 * @param portEspionP
	 * @param portEspionGD
	 */
	public EspionGD(final String adresseEspionP, final Integer portEspionP,
					final Integer portEspionGD) {

		// --- Connexion � l'espion principal

		HashMap<String, Object> configNoeud = new HashMap<String, Object>();
		configNoeud.put("host", adresseEspionP);
		configNoeud.put("portDefaut", portEspionP);
		configNoeud.put("mode", "Client");
		configNoeud.put("utiliseParSession", true);

		do {
			try {
				configNoeud.put("pseudo", generate(64));
				_noeudEspionP = new NoeudG_TCP(configNoeud, null, this);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while (_noeudEspionP == null);

		_noeudEspionP.debuterEmission();
		_noeudEspionP.debuterReception();

		// Attendre la mise en service du serveur / Obtenir la socket support
		//
		Socket socketSupport = _noeudEspionP.obtenirSocket();
		while (socketSupport == null) {
			NoeudG_TCP.Chrono.attendre(200);
			socketSupport = _noeudEspionP.obtenirSocket();
		}
		System.out.println("Connection a l'espion principal");

		// ---
		// ENVOI DE LA DECLARATION
		// ---

		HashMap<String, Object> configSession = new HashMap();

		configSession.put("nbMaxConnexions", 100);
		configSession.put("date", null);
		configSession.put("port", portEspionGD);
		configSession.put("duree", 10);
		configSession.put("chemin", "./logEspionGD.txt");
		configSession.put("timeOutDeconnexionClient", 1);
		configSession.put("verbeux", true);

		_sessionHamecons = new SessionG(configSession);
		System.out.println("Session construite");
		_sessionHamecons.addObserver(this);

		_sessionHamecons.demarrerConnexions();

		System.out.println("\nConnexion des clients terminee.\n");

		// Afficher la liste des clients connectes
		//
		LinkedList listePseudo = _sessionHamecons.getListePseudos();
		System.out.println("La liste des clients connectes est : "
						+ listePseudo);
		_sessionHamecons.demarrerEmission();
		_sessionHamecons.demarrerReception();
		System.out.println("Sortie du constructeur");
	}

	/**
	 * Cr�er un identifiant al�atoire
	 * 
	 * @param length
	 * @return chaine al�atoire
	 */
	public String generate(int length) {

		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		String pass = "";
		for (int x = 0; x < length; x++) {
			int i = (int) Math.floor(Math.random() * 62);
			pass += chars.charAt(i);
		}
		System.out.println(pass);
		return pass;
	}

	private boolean validate(final Object event, final Long time,
					final String idEspion) throws Exception {

		String operationBoolean = null;
		Set<String> keys = validateurs.keySet();
		Iterator<String> iterator = keys.iterator();
		operationBoolean = this.operation.get(idEspion);
		if (operationBoolean == null) {

			operationBoolean = "true";

		}
		while (iterator.hasNext()) {

			Object key = iterator.next();
			Object value = validateurs.get(key);

			try {
				operationBoolean.replace(
								(String) key,
								((ValidatorComposer) value).validate(event,
												time, idEspion).toString());
			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		if (operationBoolean.matches("#[^(&&)(||)(true)(false)\\(\\)]#")) {

			throw new Exception("Etat incoherant, operation boolean = "
							+ operationBoolean);

		}

		// call groovy expressions from Java code
		GroovyShell shell = new GroovyShell();
		Boolean autoriser = (Boolean) shell.evaluate("return "
						+ operationBoolean);
		return autoriser;

	}

	// /**
	// * Un espion secondaire qui se d�clare
	// *
	// * @param espion
	// */
	// public void addEspion(EspionG espion)
	// {
	//
	// _espions.add(espion);
	//
	// // ajout aux listes de correspondance id - espion
	// _espionToId.put(espion, compteur);
	// _idToEspion.put(compteur, espion);
	// compteur++;
	//
	// // On transmet � cet espion les r�gles actuelles pour un espion qui n'a
	// // pas re�u de r�gles particuli�re
	// // /espion.update(_reglesNormales);
	// }

	/**
	 * Appel�e � la r�ception d'un message par un NoeudG ou SessionG
	 */
	public void update(Observable o, Object arg) {

		System.out.println("Message recu: " + arg);
		// TODO V�rifier les non-nullitude ou utiliser containsKey()

		if (o == _noeudEspionP)// Si c'est un nouveau message de EspionP
		{
			System.out.println("Provenance: EspionP");
			Map recu = (Map) arg;
			String commande = (String) recu.get("commande");

			if (commande.equals("CONFIGURER")) {
				// R�cup�ration de l'objet re�u

				Map config = (Map) recu.get("config");
				if (config.containsKey("espionGD")) {
					System.out.println("Partie EspionGD");
					Map partieEspionGD = (Map) config.get("espionGD");

					Map<String, Object> rules = (Map<String, Object>) partieEspionGD
									.get("rules");
					System.out.println("Mise a jour des regles");
					// On parcours le HashMap des r�gles pour:
					for (String ruleName : rules.keySet()) {

						Map rule = (Map) rules.get(ruleName);
						String action = (String) rule.get("action");
						if (action.equals("create"))// Ajouter la r�gle dans le
													// cas de create
						{
							_regles.put("ruleName", rule.get("validateur"));
						} else if (action.equals("delete"))// l'enlever dans le
															// cas de delete
						{
							_regles.remove("ruleName");
						}
					}

					System.out.println("Mise a jour du pre-requis");
					// Mise � jour du pr�-requis de niveau espionGD selon
					// l'idespion hamecon du Map
					String requis = (String) partieEspionGD.get("requis");
					if (requis != null) {
						List<String> idsEspionsHamecons = (List<String>) partieEspionGD
										.get("ids_espions_hamecons");
						if (idsEspionsHamecons.size() == 0)
							for (String idEspionHamecon : idsEspionsHamecons) {
								operation.put(idEspionHamecon, requis);
							}
					}
				}
				if (config.containsKey("hamecon")) {
					System.out.println("Partie hamecon:");
					HashMap partieHamecon = (HashMap) config.get("hamecon");
					partieHamecon.put("commande", "CONFIGURER");
					// R�partition des r�gles selon l'id espion hamecon du Map
					List<String> idsEspionsHamecons = (List<String>) partieHamecon
									.get("id_espion_hamecon");
					if (idsEspionsHamecons == null
									|| idsEspionsHamecons.size() == 0)// Si la
																		// liste
																		// est
																		// vide,
																		// on
																		// envoie
																		// �
																		// tous
																		// les
																		// espions
																		// hamecons
					{
						System.out.println(idsEspionsHamecons);
						System.out.println("Envoi de la nouvelle config a tous les hamecons");
						_sessionHamecons.envoyerClients(partieHamecon);
					} else {
						for (String idEspionHamecon : idsEspionsHamecons) {
							System.out.println("Envoi de la nouvelle config a "
											+ idEspionHamecon);
							_sessionHamecons.envoyerClient(partieHamecon,
											idEspionHamecon);
						}

					}

				}
			} else if (commande.equals("CAPTURER")) {
				
				 List<String> pseudos = _sessionHamecons.getListePseudos();
				  for(String pseudo : pseudos)
				  {
				   if(pseudo.startsWith((String) recu.get("id")))
				   {
					   _sessionHamecons.envoyerClient((HashMap) recu,
								pseudo);
				    break;
				   }
				  }
				  
				
			}

		} else
		// Si c'est un nouveau message d'un Espion hame�on
		{
			System.out.println("Provenance: un espion hamecon");

			if (arg instanceof String) // Si c'est un String qui remonte
			{
				System.out.println("String: " + (String) arg);
			} else if (arg instanceof HashMap) // Si c'est un HashMaps
			{
				HashMap message = (HashMap) arg;
				System.out.println(message);
				if (message.containsKey("commande")) {
					String emetteur = (String) message.get("emetteur");
					System.out.println("emetteur : " + emetteur);

					String commande = (String) message.get("commande");
					System.out.println("commande : " + commande);

					// ==== D�claration d'un espion hamecon
					if (commande.equals("DECLARER")) {
						// TODO Notifier l'espionP de l'abonnement de l'espion
						// hamecon
						HashMap declarationDEspionHamecon = new HashMap();
						declarationDEspionHamecon.put("commande",
										"DECLARER-ESPIONG");
						declarationDEspionHamecon.put("id", emetteur);

					}

					// ==== Remont�e d'information
					else if (commande.equals("NOTIFIER")) // Appell�e dans le
															// cas d'un
															// �venement ou
															// d'une r�ponse �
															// une commande
					{
						notify(message, emetteur);
					}

				}

			}

		}

		System.out.println("Fin de traitement de reception du message. operation: "
						+ operation);
	}

	//
	/**
	 * Appel�e par espionG lorsqu'un �v�nement passe le filtre
	 * 
	 * @param message
	 * @param identifiant
	 */
	public void notify(Map<String, Object> message, String identifiant) {

		Boolean autoriser = true;
		if (message.get("evenement") != null) {
			autoriser = false;

			try {
				autoriser = this.validate(message.get("evenement"),
								(Long) message.get("date"), identifiant);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (autoriser) {
			message.put("id", identifiant);

			_noeudEspionP.envoyerMessage(message);
		}
	}

}
