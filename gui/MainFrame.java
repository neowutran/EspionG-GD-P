package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;

import model.EspionGAddedEvent;
import model.EspionGDAddedEvent;
import model.EspionGDDeletedEvent;
import model.EspionGDeletedEvent;
import model.Model;
import model.ModelListener;
import model.SpyingEvent;
import model.SpyingEventReceivedEvent;
import model.ThreadInfosResponseEvent;
import controller.Controller;


/**
 * 
 * @author Sylvain Levasseur
 * 
 */
@SuppressWarnings({ "rawtypes" })
public class MainFrame extends JFrame implements ModelListener, ListSelectionListener, ActionListener {
	private static final long							serialVersionUID	= 1L;
	
	private Model										_model;
	private Controller									_controller;
	
	private JPanel										_spyListAndEventListPanel;		/* Panneau 1 */
	private JPanel										_spyListPanel;					/* Panneau 1.1 */
	private JPanel										_spyGDListPanel;				/* Panneau 1.1.1 */
	private JPanel										_spyGDInfosPanel;				/* Panneau 1.1.2 */
	private JPanel										_spyGListPanel;				/* Panneau 1.1.2.1 */
	private JPanel										_spyGDCommandsPanel;			/* Panneau 1.1.2.2 */
	private JPanel										_eventListPanel;				/* Panneau 1.2 */
	private JPanel										_configPanel;					/* Panneau 2 */
	private JLabel										_spyGDListLabel;
	private JList										_spyGDListView;
	private JLabel										_spyGListLabel;
	private JList										_spyGListView;
	private JButton										_threadInfosRequestButton;
	private JTable										_eventTableView;
	private JButton										_setConfigButton;
	
	private DefaultListModel							_spyGDListData;
	private DefaultListModel							_spyGListData;
	private SpyingEventTableModel						_eventTableData;
	private TableRowSorter<SpyingEventTableModel>		_eventTableDataSorter;
	private RowFilter<SpyingEventTableModel, Integer>	_eventTableDataFilter;
	private String										_eventTableDataEspionGDFilter;
	private String										_eventTableDataEspionGFilter;
	
	public MainFrame(Controller controller, Model model) {
		_controller = controller;
		_model = model;
		
		// D�finition du comportement de la fen�tre lors de sa fermeture
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// D�finition de la taille de la fen�tre
		this.setSize(900, 500);
		
		// D�finition de la position de la fen�tre
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
		
		// D�finition du titre de la fen�tre
		this.setTitle("Espion");
		
		// D�finition des panneaux de la fen�tre
		_spyListAndEventListPanel = new JPanel(new BorderLayout());
		_spyListAndEventListPanel.setBorder(new EmptyBorder(8, 8, 8, 8));
		_configPanel = new JPanel(new BorderLayout());
		
		// D�finition des widgets du panneau de listes d'espions et �v�nements (panneau 1)
		_spyListPanel = new JPanel(new BorderLayout());
		_spyListPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "Espionneurs"));
		_eventListPanel = new JPanel(new BorderLayout());
		_eventListPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "�v�nements"));
		
		// D�finition des widgets du panneau de listes d'espions (panneau 1.1)
		_spyGDListPanel = new JPanel(new BorderLayout());
		_spyGDListPanel.setBorder(new EmptyBorder(5, 5, 5, 10));
		_spyGDInfosPanel = new JPanel(new BorderLayout());
		_spyGDInfosPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLACK), "EspionGD s�lectionn�"));
		
		// // D�finition des widgets du panneau de la liste d'espions GD (panneau 1.1.1)
		_spyGDListLabel = new JLabel("EspionGD :");
		_spyGDListData = new DefaultListModel();
		_spyGDListData.addElement("Tous");
		_spyGDListView = new JList(_spyGDListData);
		_spyGDListView.setFixedCellWidth(110);
		_spyGDListView.setVisibleRowCount(5);
		_spyGDListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_spyGDListView.addListSelectionListener(this);
		
		// D�finition des widgets du panneau d'informations d'un espionGD (panneau 1.1.2)
		_spyGListPanel = new JPanel(new BorderLayout());
		_spyGListPanel.setBorder(new EmptyBorder(5, 5, 5, 10));
		_spyGDCommandsPanel = new JPanel();
		_spyGDCommandsPanel.setLayout(new BoxLayout(_spyGDCommandsPanel, BoxLayout.Y_AXIS));
		_spyGDCommandsPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		
		// D�finition des widgets du panneau de la liste d'espionG (panneau 1.1.2.1)
		_spyGListLabel = new JLabel("EspionG :");
		_spyGListData = new DefaultListModel();
		_spyGListData.addElement("Tous");
		_spyGListView = new JList(_spyGListData);
		_spyGListView.setFixedCellWidth(80);
		_spyGListView.setVisibleRowCount(5);
		_spyGListView.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		_spyGListView.addListSelectionListener(this);
		
		// D�finition des widgets du panneau de commandes d'un espionG (panneau 1.1.2.2)
		_threadInfosRequestButton = new JButton("Liste des threads");
		_threadInfosRequestButton.setAlignmentX(Component.RIGHT_ALIGNMENT);
		_threadInfosRequestButton.addActionListener(this);
		
		// D�finition des widgets du panneau de la liste d'�v�nements (panneau 1.2)
		// _eventTableData = new DefaultTableModel(new Object[][] {}, new Object[] { "Date de l'�v�nement", "EspionGD source", "EspionG source", "Classe de l'�v�nement", "Informations compl�mentaires" });
		_eventTableData = new SpyingEventTableModel();
		_eventTableDataEspionGDFilter = "";
		_eventTableDataEspionGFilter = "";
		_eventTableDataSorter = new TableRowSorter<SpyingEventTableModel>(_eventTableData);
		_eventTableDataSorter.toggleSortOrder(0);
		_eventTableDataFilter = new RowFilter<SpyingEventTableModel, Integer>() {
			@Override
			public boolean include(javax.swing.RowFilter.Entry<? extends SpyingEventTableModel, ? extends Integer> entry) {
				SpyingEvent event = entry.getModel().getSpyingEvent(entry.getIdentifier());
				if (!_eventTableDataEspionGDFilter.equals("") && !event.getEspionGD().equals(_eventTableDataEspionGDFilter)) {
					return false;
				}
				if (!_eventTableDataEspionGFilter.equals("") && !event.getEspionG().equals(_eventTableDataEspionGFilter)) {
					return false;
				}
				return true;
			}
		};
		_eventTableDataSorter.setRowFilter(_eventTableDataFilter);
		_eventTableView = new JTable(_eventTableData);
		_eventTableView.setRowSorter(_eventTableDataSorter);
		
		// D�finition des widgets du panneau de configuration (panneau 2)
		_setConfigButton = new JButton("Charger la configuration");
		_setConfigButton.addActionListener(this);
		
		// Accrochage des widgets au panneau de listes d'espions et �v�nements (panneau 1)
		_spyListAndEventListPanel.add(_spyListPanel, BorderLayout.NORTH);
		_spyListAndEventListPanel.add(_eventListPanel, BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau de listes d'espions (panneau 1.1)
		_spyListPanel.add(_spyGDListPanel, BorderLayout.WEST);
		_spyListPanel.add(_spyGDInfosPanel, BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau de la liste d'espions GD (panneau 1.1.1)
		_spyGDListPanel.add(_spyGDListLabel, BorderLayout.NORTH);
		_spyGDListPanel.add(new JScrollPane(_spyGDListView), BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau d'informations d'un espionGD (panneau 1.1.2)
		_spyGDInfosPanel.add(_spyGListPanel, BorderLayout.WEST);
		_spyGDInfosPanel.add(_spyGDCommandsPanel, BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau de la liste d'espionG (panneau 1.1.2.1)
		_spyGListPanel.add(_spyGListLabel, BorderLayout.NORTH);
		_spyGListPanel.add(new JScrollPane(_spyGListView), BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau de commandes d'un espionG (panneau 1.1.2.2)
		_spyGDCommandsPanel.add(_threadInfosRequestButton);
		
		// Accrochage des widgets au panneau de la liste d'�v�nements (panneau 1.2)
		_eventListPanel.add(new JScrollPane(_eventTableView), BorderLayout.CENTER);
		
		// Accrochage des widgets au panneau de configuration (panneau 2)
		_configPanel.add(_setConfigButton);
		
		// Accrochage des panneaux � la fen�tre
		this.getContentPane().add(_spyListAndEventListPanel, BorderLayout.CENTER);
		this.getContentPane().add(_configPanel, BorderLayout.SOUTH);
		
		// TODO Chargement des espionGD
		// TODO Chargement des �v�nements
		
		// S�lection de tous les espionGD
		_spyGDListView.setSelectedIndex(0);
	}
	
	public void valueChanged(ListSelectionEvent event) {
		if (!event.getValueIsAdjusting()) {
			
			// Cas o� l'�lement s�lectionn� de la liste d'espionGD a chang�
			if (event.getSource().equals(_spyGDListView)) {
				String selectedValue = (String) _spyGDListView.getSelectedValue();
				if(selectedValue == null) return;
				System.out.println("Nouveau EspionGD : " + selectedValue);
				
				// Masquage de la liste d'espionG si tous les espionGD sont s�lectionn�s
				if (_spyGDListView.getSelectedIndex() == 0) {
					_spyGListPanel.setVisible(false);
					_eventTableDataEspionGDFilter = "";
				}
				else {
					_spyGListPanel.setVisible(true);
					_eventTableDataEspionGDFilter = selectedValue;
					
					// Rafra�chissement de la liste d'espionsG
					_spyGListData.clear();
					_spyGListData.addElement("Tous");
					Iterator it = _model.getEspionGs(selectedValue).iterator();
					while (it.hasNext()) {
						String espionG = (String) it.next();
						_spyGListData.addElement(espionG);
					}
				}
				
				// Application du nouveau filtre d'�v�nements
				_eventTableDataSorter.setRowFilter(_eventTableDataFilter);
				
				// S�lection de tous les espionG
				_spyGListView.setSelectedIndex(0);
			}
			
			// Cas o� l'�lement s�lectionn� de la liste d'espionG a chang�
			else if (event.getSource().equals(_spyGListView)) {
				String selectedValue = (String) _spyGListView.getSelectedValue();
				if(selectedValue == null) return;
				System.out.println("Nouveau EspionG : " + selectedValue);
				
				// Masquage du bouton "Liste des threads" si tous les espionG sont s�lectionn�s
				if (_spyGListView.getSelectedIndex() == 0) {
					_threadInfosRequestButton.setVisible(false);
					_eventTableDataEspionGFilter = "";
				}
				else {
					_threadInfosRequestButton.setVisible(true);
					_eventTableDataEspionGFilter = selectedValue;
				}
				
				// Application du nouveau filtre d'�v�nements
				_eventTableDataSorter.setRowFilter(_eventTableDataFilter);
			}
		}
	}
	
	public void actionPerformed(ActionEvent event) {
		// Cas o� le bouton de demande d'informations sur les threads a �t� cliqu�
		if (event.getSource().equals(_threadInfosRequestButton)) {
			System.out.println("Informations sur les threads demand�es");
			
			// Notification d'une demande d'informations sur les threads au controleur
			_controller.notifyThreadInfosRequest((String) _spyGDListView.getSelectedValue(), (String) _spyGListView.getSelectedValue());
		}
		
		// Cas o� le bouton de chargement de configuration a �t� cliqu�
		else if (event.getSource().equals(_setConfigButton)) {
			System.out.println("Chargement de configuration demand�");
			try {
				_controller.notifyConfigure(selectFile());
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	public void espionGDAdded(EspionGDAddedEvent e) {
		// Ajout � la JList
		_spyGDListData.addElement(e.getName());
	}
	
	/**
	 * 
	 * @deprecated This method is no longer used since no EspionGD sends any message related to this event.
	 */
	public void espionGDDeleted(EspionGDDeletedEvent e) {
		// TODO Changement d'espionGD s�lectionn� si besoin puis suppression de la JList
		// TODO Rafra�chissement de la liste des �v�nements si tous les espionGD sont s�lectionn�s
		throw new UnsupportedOperationException();
	}
	
	public void espionGAdded(EspionGAddedEvent e) {
		// Ajout � la JList si son espionGD est actuellement s�lectionn�
		if (((String) _spyGDListView.getSelectedValue()).equals(e.getParentName())) {
			_spyGListData.addElement(e.getName());
		}
	}
	
	/**
	 * 
	 * @deprecated This method is no longer used since no EspionGD sends any message related to this event.
	 */
	public void espionGDeleted(EspionGDeletedEvent e) {
		// TODO Changement d'espionG s�lectionn� si besoin et suppression de la JList si son espionGD est s�lectionn�
		// TODO Rafra�chissement de la liste des �v�nements si tous les espionGD sont s�lectionn�s ou son espionGD est s�lectionn�
		throw new UnsupportedOperationException();
	}
	
	public void spyingEventReceived(SpyingEventReceivedEvent e) {
		// Ajout de l'�v�nement � la Table
		_eventTableData.addSpyingEvent(e.getEvent());
	}
	
	public void threadInfosResponse(ThreadInfosResponseEvent e) {
		// TODO Finir en affichant des informations plus pertinantes
		ArrayList infos = (ArrayList)e.getResponse();
		
		for(String i : (ArrayList<String>)infos ){
			
			System.out.println(i);
			JOptionPane.showMessageDialog(this,i);
		}
	}
	
	private String selectFile() throws InterruptedException {
		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Choisir un fichier de configuration");
		
		String fileLocation = null;
		while (fileLocation == null) {
			switch (chooser.showOpenDialog(this)) {
			case JFileChooser.APPROVE_OPTION:
				fileLocation = chooser.getSelectedFile().getAbsolutePath();
				break;
			case JFileChooser.CANCEL_OPTION:
				throw new InterruptedException();
			default:
				/* Reste le cas d'erreur */
			}
			
		}
		return fileLocation;
	}
}
