package espionG;

import groovy.lang.GroovyShell;

import java.awt.AWTEvent;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DragSourceMotionListener;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.AWTEventListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.beans.beancontext.BeanContextMembershipEvent;
import java.beans.beancontext.BeanContextMembershipListener;
import java.beans.beancontext.BeanContextServiceAvailableEvent;
import java.beans.beancontext.BeanContextServiceRevokedEvent;
import java.beans.beancontext.BeanContextServiceRevokedListener;
import java.beans.beancontext.BeanContextServicesListener;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.prefs.NodeChangeEvent;
import java.util.prefs.NodeChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import javax.naming.event.NamespaceChangeListener;
import javax.naming.event.NamingEvent;
import javax.naming.event.NamingExceptionEvent;
import javax.naming.event.NamingListener;
import javax.naming.event.ObjectChangeListener;
import javax.naming.ldap.UnsolicitedNotificationEvent;
import javax.naming.ldap.UnsolicitedNotificationListener;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLSessionBindingEvent;
import javax.net.ssl.SSLSessionBindingListener;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.RowSetEvent;
import javax.sql.RowSetListener;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuDragMouseEvent;
import javax.swing.event.MenuDragMouseListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuKeyEvent;
import javax.swing.event.MenuKeyListener;
import javax.swing.event.MenuListener;
import javax.swing.event.MouseInputListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.ExpandVetoException;

import library.ObjectString;
import library.Validator;
import models.NoeudG_TCP;

import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import antlr.collections.List;

import config.GenerateXML;

/**
 * @author sephiroth
 */
public class EspionG implements Observer, EventListener, DragGestureListener,
				DragSourceListener, DragSourceMotionListener,
				DropTargetListener, FocusListener, HandshakeCompletedListener,
				HierarchyBoundsListener, HierarchyListener, HyperlinkListener,
				InputMethodListener, InternalFrameListener, ItemListener,
				KeyListener, LineListener, ListDataListener,
				ListSelectionListener, MenuDragMouseListener, MenuKeyListener,
				MenuListener, MetaEventListener, MouseInputListener,
				MouseListener, MouseMotionListener, MouseWheelListener,
				NamespaceChangeListener, NamingListener, NodeChangeListener,
				ObjectChangeListener, PopupMenuListener,
				PreferenceChangeListener, PropertyChangeListener,
				RowSetListener, SSLSessionBindingListener,
				TableColumnModelListener, TableModelListener, TextListener,
				TreeExpansionListener, TreeModelListener,
				TreeSelectionListener, TreeWillExpandListener,
				UndoableEditListener, UnsolicitedNotificationListener,
				VetoableChangeListener, WindowFocusListener, WindowListener,
				WindowStateListener, DocumentListener, ControllerEventListener,
				ContainerListener, ConnectionEventListener, ComponentListener,
				ChangeListener, CellEditorListener, CaretListener,
				BeanContextServicesListener, ActionListener,
				AdjustmentListener, AncestorListener, AWTEventListener,
				BeanContextMembershipListener,
				BeanContextServiceRevokedListener {

	private final Map<String, Validator>	validateurs	= new HashMap<String, Validator>();
	private String							operation	= "";
	private String							operationBoolean;
	private Long							decalage	= 0L;

	private NoeudG_TCP						noeud;

	static Element							config;

	/**
	 * 
	 */
	public EspionG() {

		// ============================== Ctrl + c - Ctrl + v

		// --- Connexion ï¿½ l'espion principal

		// Rï¿½cup de l'adresse/port dans un fichier de config
		// On crï¿½e une instance de SAXBuilder
		SAXBuilder sxb = new SAXBuilder();
		try {
			// On crï¿½e un nouveau document JDOM avec en argument le fichier XML
			// Le parsing est terminï¿½ ;)
			config = sxb.build(new File("ConfigEspionG.xml")).getRootElement();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		if (config == null) {

			config = (new GenerateXML()).genererXML();

		}

		// On initialise un nouvel ï¿½lï¿½ment racine avec l'ï¿½lï¿½ment racine du
		// document.
		// Rï¿½cupï¿½ration de l'adresse et du port de l'espionP
		String adresse = config.getChild("espionG").getChildText("adresse");
		Integer port = Integer.valueOf(config.getChild("espionG").getChildText(
						"port"));

		System.out.println("Connexion a l'hote: " + adresse);
		System.out.println("Sur le port: " + port);

		// connexion bourinos ï¿½ coup de gï¿½nï¿½ration alï¿½atoire de String
		HashMap<String, Object> configNoeud = new HashMap<String, Object>();
		configNoeud.put("host", adresse);
		configNoeud.put("portDefaut", port);

		configNoeud.put("mode", "Client");
		configNoeud.put("utiliseParSession", true);

		do {
			try {
				configNoeud.put("pseudo", generate(64));
				noeud = new NoeudG_TCP(configNoeud, null, null);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		} while (noeud == null);

		noeud.addObserver(this);
		noeud.debuterEmission();
		noeud.debuterReception();

		// Attendre la mise en service du serveur / Obtenir la socket support
		//
		Socket socketSupport = noeud.obtenirSocket();
		while (socketSupport == null) {
			NoeudG_TCP.Chrono.attendre(200);
			socketSupport = noeud.obtenirSocket();
		}
		System.out.println("Connexion a l'espion delegue reussie");

		// ---
		// ENVOI DE LA DECLARATION
		// ---

		// Construire la declaration de l'espionG
		//
		HashMap<String, String> declaration = new HashMap<String, String>();

		declaration.put("commande", "DECLARER");
		declaration.put("adresseIP", socketSupport.getLocalAddress()
						.getHostAddress());

		// Dï¿½claration de l'espion hamecon aupres de l'espion dï¿½lï¿½guï¿½
		//
		noeud.debuterEmission();
		noeud.debuterReception();

		noeud.envoyerMessage(declaration);
		System.out.println("Déclaration envoyee");

		// =======================================

	}

	/**
	 * Sert ï¿½ la gï¿½nï¿½ration alï¿½atoire d'un identifiant de connexion
	 * 
	 * @param length
	 * @return String
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

	private boolean validate(final Object event, final Long time)
					throws Exception {

		Set<String> keys = validateurs.keySet();
		Iterator<String> iterator = keys.iterator();
		this.operationBoolean = this.operation;
		
		if(this.operationBoolean.equals("")){
			
			System.out.println("aucune expression booleene defini, blocage de l'evenement");
			return false;
			
		}
		
		System.out.println("operationBool"+ this.operationBoolean);
		while (iterator.hasNext()) {

			Object key = iterator.next();
			Object value = validateurs.get(key);

			try {
						
				this.operationBoolean = this.operationBoolean.replace(
						(String) key,
						((Validator) value).validate(event, time).toString());
				
				
			} catch (Exception e) {

				e.printStackTrace();

			}

		}

		if (this.operationBoolean.matches("#[^(&&)(||)(true)(false)\\(\\)]#")) {

			throw new Exception("Etat incoherant, operation boolean = "
							+ this.operationBoolean);

		}

		// call groovy expressions from Java code
		GroovyShell shell = new GroovyShell();
		Boolean autoriser = (Boolean) shell.evaluate("return "
						+ this.operationBoolean);
		System.out.println("resultat de la methode validate:"+autoriser);
		return autoriser;

	}

	private void send(final Object event, final Long time) {

		Map<String, Object> message = new HashMap<String, Object>();
		message.put("commande", "NOTIFIER");
		message.put("type", "event");
		message.put("content", event);
		message.put("time", this.getTime());

		noeud.envoyerMessage(message);

	}

	/**
	 * 
	 */
	public void sendThread() {

		Map<String, Object> message = new HashMap<String, Object>();
		 Thread[] threads = new Thread[Thread.activeCount()];
		this.getThreads(threads);
		ArrayList messageThread =  new ArrayList();
		for(Thread t : threads){
			
			messageThread.add(t.toString());
			
		}
		message.put("commande", "NOTIFIER");
		message.put("type", "thread");
		message.put("content", messageThread);
		message.put("time", this.getTime());
		noeud.envoyerMessage(message);
	}

	/**
	 * @param regleFiltrage
	 */
	private void update(final Map regleFiltrage) {

		if (regleFiltrage.get("requis") != null) {

			this.operation = (String) regleFiltrage.get("requis");

		}

		Set<String> keys = ((Map) regleFiltrage.get("rules")).keySet();
		Iterator<String> iterator = keys.iterator();
		while (iterator.hasNext()) {

			Object key = iterator.next();
			Map value = (Map) ((Map) regleFiltrage.get("rules")).get(key);

			if (value.get("action").equals("delete")) {

				this.validateurs.remove(key);

			}

			if (value.get("action").equals("create")) {

				this.validateurs.put((String) key,
								(Validator) value.get("validateur"));

			}

		}

	}

	/**
	 * @param operation
	 */
	public void setOperations(final String operation) {

		this.operation = operation;

	}

	/**
	 * @param nom
	 * @param validateur
	 */
	public void addValidateur(final String nom, final Validator validateur) {

		if (validateurs.get(nom) != null) {
			this.removeValidateur(nom);
		}
		this.validateurs.put(nom, validateur);

	}

	/**
	 * @param nom
	 */
	public void removeValidateur(final String nom) {

		this.validateurs.remove(nom);

	}

	/**
	 * @param event
	 */
	public void reception(final Object event) {

		Long time = this.getTime() - this.decalage;
		Boolean autoriser = false;

		try {
			autoriser = this.validate(event, time);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (autoriser) {

			System.out.println("event:" + event);
			System.out.println("date:" + time);
			System.out.println("\n");
			this.send(event, time);

		}
	}

	/**
	 * @return Long
	 */
	public Long getTime() {

		return Calendar.getInstance().getTimeInMillis();

	}

	public void update(Observable o, Object arg) {

		System.out.println("Message recu");
		System.out.println(arg);
		Map recu = (Map) arg;

		// Rï¿½cupï¿½ration de l'objet reï¿½u

		String commande = (String) recu.get("commande");

		if (commande.equals("CONFIGURER")) {
			System.out.println("Mise a jour des regles");
			// ---- Mise ï¿½ jour des rï¿½gles

			Map<String, Object> rules = (Map<String, Object>) recu.get("rules");

			// On parcours le HashMap des rï¿½gles pour:
			for (String ruleName : rules.keySet()) {

				Map rule = (Map) rules.get(ruleName);
				String action = (String) rule.get("action");
				if (action.equals("create"))// Ajouter la rï¿½gle dans le cas
											// de
											// create
				{
					System.out.println();
					validateurs.put(ruleName,
									(Validator) rule.get("validateur"));
				} else if (action.equals("delete"))// l'enlever dans le cas de
													// delete
				{
					validateurs.remove(ruleName);
				}
			}

			System.out.println("Mise a jour du pre-requis");

			// -----Mise ï¿½ jour du prï¿½-requis

			String test = (String) recu.get("requis");
			System.out.println(test);
			if (test != null) {
				operation = test;
			}

		} else if (commande.equals("CAPTURER")) {
			System.out.println("bon");
			sendThread();

		}

		System.out.println("Fin de traitement du message");
	}

	/**
	 * @param time
	 */
	public void updateTime(final Long time) {

		this.decalage = Calendar.getInstance().getTimeInMillis() - time;

	}

	public void serviceRevoked(BeanContextServiceRevokedEvent bcsre) {

		this.reception(bcsre);
	}

	public void childrenAdded(BeanContextMembershipEvent bcme) {

		this.reception(bcme);

	}

	public void childrenRemoved(BeanContextMembershipEvent bcme) {

		this.reception(bcme);

	}

	public void eventDispatched(AWTEvent event) {

		this.reception(event);

	}

	public void ancestorAdded(AncestorEvent event) {

		this.reception(event);

	}

	public void ancestorRemoved(AncestorEvent event) {

		this.reception(event);

	}

	public void ancestorMoved(AncestorEvent event) {

		this.reception(event);

	}

	public void adjustmentValueChanged(AdjustmentEvent e) {

		this.reception(e);

	}

	public void actionPerformed(ActionEvent e) {

		this.reception(e);

	}

	public void serviceAvailable(BeanContextServiceAvailableEvent bcsae) {

		this.reception(bcsae);

	}

	public void caretUpdate(CaretEvent e) {

		this.reception(e);

	}

	public void editingStopped(ChangeEvent e) {

		this.reception(e);

	}

	public void editingCanceled(ChangeEvent e) {

		this.reception(e);

	}

	public void stateChanged(ChangeEvent e) {

		this.reception(e);

	}

	public void componentResized(ComponentEvent e) {

		this.reception(e);

	}

	public void componentMoved(ComponentEvent e) {

		this.reception(e);

	}

	public void componentShown(ComponentEvent e) {

		this.reception(e);

	}

	public void componentHidden(ComponentEvent e) {

		this.reception(e);

	}

	public void connectionClosed(ConnectionEvent event) {

		this.reception(event);

	}

	public void connectionErrorOccurred(ConnectionEvent event) {

		this.reception(event);

	}

	public void componentAdded(ContainerEvent e) {

		this.reception(e);

	}

	public void componentRemoved(ContainerEvent e) {

		this.reception(e);

	}

	public void controlChange(ShortMessage event) {

		this.reception(event);

	}

	public void insertUpdate(DocumentEvent e) {

		this.reception(e);

	}

	public void removeUpdate(DocumentEvent e) {

		this.reception(e);

	}

	public void changedUpdate(DocumentEvent e) {

		this.reception(e);

	}

	public void windowStateChanged(WindowEvent e) {

		this.reception(e);

	}

	public void windowOpened(WindowEvent e) {

		this.reception(e);

	}

	public void windowClosing(WindowEvent e) {

		this.reception(e);

	}

	public void windowClosed(WindowEvent e) {

		this.reception(e);

	}

	public void windowIconified(WindowEvent e) {

		this.reception(e);

	}

	public void windowDeiconified(WindowEvent e) {

		this.reception(e);

	}

	public void windowActivated(WindowEvent e) {

		this.reception(e);

	}

	public void windowDeactivated(WindowEvent e) {

		this.reception(e);

	}

	public void windowGainedFocus(WindowEvent e) {

		this.reception(e);

	}

	public void windowLostFocus(WindowEvent e) {

		this.reception(e);

	}

	public void vetoableChange(PropertyChangeEvent evt)
					throws PropertyVetoException {

		this.reception(evt);

	}

	public void notificationReceived(UnsolicitedNotificationEvent evt) {

		this.reception(evt);

	}

	public void undoableEditHappened(UndoableEditEvent e) {

		this.reception(e);

	}

	public void treeWillExpand(TreeExpansionEvent event)
					throws ExpandVetoException {

		this.reception(event);

	}

	public void treeWillCollapse(TreeExpansionEvent event)
					throws ExpandVetoException {

		this.reception(event);

	}

	public void valueChanged(TreeSelectionEvent e) {

		this.reception(e);

	}

	public void treeNodesChanged(TreeModelEvent e) {

		this.reception(e);

	}

	public void treeNodesInserted(TreeModelEvent e) {

		this.reception(e);

	}

	public void treeNodesRemoved(TreeModelEvent e) {

		this.reception(e);

	}

	public void treeStructureChanged(TreeModelEvent e) {

		this.reception(e);

	}

	public void treeExpanded(TreeExpansionEvent event) {

		this.reception(event);

	}

	public void treeCollapsed(TreeExpansionEvent event) {

		this.reception(event);

	}

	public void textValueChanged(TextEvent e) {

		this.reception(e);

	}

	public void tableChanged(TableModelEvent e) {

		this.reception(e);

	}

	public void columnAdded(TableColumnModelEvent e) {

		this.reception(e);

	}

	public void columnRemoved(TableColumnModelEvent e) {

		this.reception(e);

	}

	public void columnMoved(TableColumnModelEvent e) {

		this.reception(e);

	}

	public void columnMarginChanged(ChangeEvent e) {

		this.reception(e);

	}

	public void columnSelectionChanged(ListSelectionEvent e) {

		this.reception(e);

	}

	public void valueBound(SSLSessionBindingEvent event) {

		this.reception(event);

	}

	public void valueUnbound(SSLSessionBindingEvent event) {

		this.reception(event);

	}

	public void rowSetChanged(RowSetEvent event) {

		this.reception(event);

	}

	public void rowChanged(RowSetEvent event) {

		this.reception(event);

	}

	public void cursorMoved(RowSetEvent event) {

		this.reception(event);

	}

	public void propertyChange(PropertyChangeEvent evt) {

		this.reception(evt);

	}

	public void preferenceChange(PreferenceChangeEvent evt) {

		this.reception(evt);

	}

	public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

		this.reception(e);

	}

	public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

		this.reception(e);

	}

	public void popupMenuCanceled(PopupMenuEvent e) {

		this.reception(e);

	}

	public void objectChanged(NamingEvent evt) {

		this.reception(evt);

	}

	public void childAdded(NodeChangeEvent evt) {

		this.reception(evt);

	}

	public void childRemoved(NodeChangeEvent evt) {

		this.reception(evt);

	}

	public void namingExceptionThrown(NamingExceptionEvent evt) {

		this.reception(evt);

	}

	public void objectAdded(NamingEvent evt) {

		this.reception(evt);

	}

	public void objectRemoved(NamingEvent evt) {

		this.reception(evt);

	}

	public void objectRenamed(NamingEvent evt) {

		this.reception(evt);

	}

	public void mouseWheelMoved(MouseWheelEvent e) {

		this.reception(e);

	}

	public void mouseDragged(MouseEvent e) {

		this.reception(e);

	}

	public void mouseMoved(MouseEvent e) {

		this.reception(e);

	}

	public void mouseClicked(MouseEvent e) {

		this.reception(e);

	}

	public void mousePressed(MouseEvent e) {

		this.reception(e);

	}

	public void mouseReleased(MouseEvent e) {

		this.reception(e);

	}

	public void mouseEntered(MouseEvent e) {

		this.reception(e);

	}

	public void mouseExited(MouseEvent e) {

		this.reception(e);

	}

	public void meta(MetaMessage meta) {

		this.reception(meta);

	}

	public void menuSelected(MenuEvent e) {

		this.reception(e);

	}

	public void menuDeselected(MenuEvent e) {

		this.reception(e);

	}

	public void menuCanceled(MenuEvent e) {

		this.reception(e);

	}

	public void menuKeyTyped(MenuKeyEvent e) {

		this.reception(e);

	}

	public void menuKeyPressed(MenuKeyEvent e) {

		this.reception(e);

	}

	public void menuKeyReleased(MenuKeyEvent e) {

		this.reception(e);

	}

	public void menuDragMouseEntered(MenuDragMouseEvent e) {

		this.reception(e);

	}

	public void menuDragMouseExited(MenuDragMouseEvent e) {

		this.reception(e);

	}

	public void menuDragMouseDragged(MenuDragMouseEvent e) {

		this.reception(e);

	}

	public void menuDragMouseReleased(MenuDragMouseEvent e) {

		this.reception(e);

	}

	public void valueChanged(ListSelectionEvent e) {

		this.reception(e);

	}

	public void intervalAdded(ListDataEvent e) {

		this.reception(e);

	}

	public void intervalRemoved(ListDataEvent e) {

		this.reception(e);

	}

	public void contentsChanged(ListDataEvent e) {

		this.reception(e);

	}

	public void update(LineEvent event) {

		this.reception(event);

	}

	public void keyTyped(KeyEvent e) {

		this.reception(e);

	}

	public void keyPressed(KeyEvent e) {

		this.reception(e);

	}

	public void keyReleased(KeyEvent e) {

		this.reception(e);

	}

	public void itemStateChanged(ItemEvent e) {

		this.reception(e);

	}

	public void internalFrameOpened(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameClosing(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameClosed(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameIconified(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameDeiconified(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameActivated(InternalFrameEvent e) {

		this.reception(e);

	}

	public void internalFrameDeactivated(InternalFrameEvent e) {

		this.reception(e);

	}

	public void inputMethodTextChanged(InputMethodEvent event) {

		this.reception(event);

	}

	public void caretPositionChanged(InputMethodEvent event) {

		this.reception(event);

	}

	public void hyperlinkUpdate(HyperlinkEvent e) {

		this.reception(e);

	}

	public void hierarchyChanged(HierarchyEvent e) {

		this.reception(e);

	}

	public void ancestorMoved(HierarchyEvent e) {

		this.reception(e);

	}

	public void ancestorResized(HierarchyEvent e) {

		this.reception(e);

	}

	public void handshakeCompleted(HandshakeCompletedEvent event) {

		this.reception(event);

	}

	public void focusGained(FocusEvent e) {

		this.reception(e);

	}

	public void focusLost(FocusEvent e) {

		this.reception(e);

	}

	public void dragEnter(DropTargetDragEvent dtde) {

		this.reception(dtde);

	}

	public void dragOver(DropTargetDragEvent dtde) {

		this.reception(dtde);

	}

	public void dropActionChanged(DropTargetDragEvent dtde) {

		this.reception(dtde);

	}

	public void dragExit(DropTargetEvent dte) {

		this.reception(dte);

	}

	public void drop(DropTargetDropEvent dtde) {

		this.reception(dtde);

	}

	public void dragMouseMoved(DragSourceDragEvent dsde) {

		this.reception(dsde);

	}

	public void dragEnter(DragSourceDragEvent dsde) {

		this.reception(dsde);

	}

	public void dragOver(DragSourceDragEvent dsde) {

		this.reception(dsde);

	}

	public void dropActionChanged(DragSourceDragEvent dsde) {

		this.reception(dsde);

	}

	public void dragExit(DragSourceEvent dse) {

		this.reception(dse);

	}

	public void dragDropEnd(DragSourceDropEvent dsde) {

		this.reception(dsde);

	}

	public void dragGestureRecognized(DragGestureEvent dge) {

		this.reception(dge);

	}

	public int getThreads(Thread[] tab) {

		return Thread.enumerate(tab);
	}

	public void test() {

		HashMap message = new HashMap();
		message.put("commande", "NOTIFIER");
		noeud.envoyerMessage(message);

	}

}
