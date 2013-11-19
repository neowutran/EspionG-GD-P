package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import network.SessionG;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class Model implements Observer {
	private SessionG		_clients;
	private ModelListener	_listener;
	private HashMap			_espions = new HashMap();
	
	public Model(short serverPort, short waitingDuration) {
		HashMap config = new HashMap();
		
		config.put("port", (int) serverPort);
		config.put("duree", (int) waitingDuration);
		config.put("chemin", "./logEspionP.txt");
		
		_clients = new SessionG(config);
		System.out.println("Session construite");
		
		_clients.addObserver(this);
		_clients.demarrerConnexions();
		_clients.demarrerEmission();
		_clients.demarrerReception();
		
		LinkedList listePseudo = _clients.getListePseudos();
		System.out.println("La liste des clients connectes est : " + listePseudo);
	}
	
	public void setListener(ModelListener listener) {
		_listener = listener;
	}
	
	public void configure(HashMap config) {
		config.put("commande", "CONFIGURER"); /* On réutilise directement la HashMap fournie pour le message à envoyer */
		
		_clients.envoyerClients(config);
	}
	
	public void getThreadInfosAsync(String espionGD, String espionG) {
		HashMap message = new HashMap();
		message.put("commande", "CAPTURER");
		message.put("id", espionG);
		 
		 List<String> pseudos = _clients.getListePseudos();
		  for(String pseudo : pseudos)
		  {
		   if(pseudo.startsWith(espionGD))
		   {
			_clients.envoyerClient(message, pseudo);
		    break;
		   }
		  }
		  
		
	}
	
	/**
	 * 
	 * @return A list of all EspionGD without their EspionG.
	 */
	public ArrayList getEspionGDs() {
		return null;
		
	}
	
	/**
	 * 
	 * @deprecated This method is no longer used.
	 */
	public ArrayList getEspionGs() {
		throw new UnsupportedOperationException();
		
	}
	
	/**
	 * 
	 * @param espionGD
	 *            The EspionGD where the EspionG are extracted.
	 * @return A list of all EspionG belonging to the specified EspionGD.
	 */
	public ArrayList getEspionGs(String espionGD) {
		ArrayList out = new ArrayList();
		Iterator it = ((HashMap)_espions.get(espionGD)).entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        out.add(pair.getKey());
	    }
	    return out;
	}
	
	/**
	 * 
	 * @return The events belonging to all EspionG of all EspionGD.
	 */
	public LinkedList getEvents() {
		return null;
		
	}
	
	/**
	 * 
	 * @param espionGD
	 *            The EspionGD where the events are extracted.
	 * @return The events belonging to all EspionG of the specified EspionGD.
	 */
	public LinkedList getEvents(String espionGD) {
		return null;
		
	}
	
	/**
	 * 
	 * @param espionGD
	 *            The EspionGD where the events are extracted.
	 * @param espionG
	 *            The EspionG where the events are extracted.
	 * @return The events belonging to the specified EspionG of the specified EspionGD.
	 */
	public LinkedList getEvents(String espionGD, String espionG) {
		return null;
		
	}
	
	public void update(Observable o, Object arg) {
		System.out.println("Message recu: " + arg);
		System.out.println("Classe: " + arg.getClass().getName());
		
		if (arg instanceof String) {
			// TODO ???
		}
		else if (arg instanceof HashMap) {
			HashMap message = (HashMap) arg;
			String command = (String) message.get("commande");
			String espionGD = (String) message.get("emetteur");
			
			System.out.println("emetteur : " + espionGD);
			System.out.println("commande : " + command);
			
			// Détection de l'ajout d'un EspionGD
			if (!_espions.containsKey(espionGD)) {
				_espions.put(espionGD, new HashMap());
				
				// Notification de l'ajout à l'écouteur
				_listener.espionGDAdded(new EspionGDAddedEvent(this, espionGD));
			}
			
			String espionG = (String) message.get("id");
			
			if (command.equals("DECLARER-ESPIONG") || ((HashMap)_espions.get(espionGD)).get(espionG) == null) {
				// Cas d'une déclaration d'espionG
				((HashMap) _espions.get(espionGD)).put(espionG, new LinkedList());
				
				// Notification à l'écouteur d'une déclaration d'espionG
				_listener.espionGAdded(new EspionGAddedEvent(this, espionGD, espionG));
			}
			else if (command.equals("NOTIFIER")) {
				String type = (String) message.get("type");
				if (type.equals("event")) {
					// Cas d'un évènement d'espionnage reçu
					Object event = message.get("content");
					SpyingEvent e = new SpyingEvent(new Date(((Long) message.get("time"))), espionGD, espionG, event.getClass().getName(), event.toString());
					((LinkedList) ((HashMap) _espions.get(espionGD)).get(espionG)).add(e);
					
					// Notification à l'écouteur d'un évènement d'espionnage reçu
					_listener.spyingEventReceived(new SpyingEventReceivedEvent(this, espionGD, espionG, e));
				}
				else if (type.equals("thread")) {
					// Cas d'une réponse à la requête de demandes d'informations sur les threads
					
					// Notification à l'écouteur de la réponse à la requête de demandes d'informations sur les threads
					_listener.threadInfosResponse(new ThreadInfosResponseEvent(this, espionGD, espionG, (ArrayList) message.get("content")));
				}
			}
		}
	}
	
}
