package gui;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.table.DefaultTableModel;

import model.SpyingEvent;
 

public class SpyingEventTableModel extends DefaultTableModel {
	private static final long		serialVersionUID	= 1L;
	
	private static final String[]	columnArray			= { "Date de l'évènement", "EspionGD source", "EspionG source", "Classe de l'évènement", "Informations complémentaires" };
	
	public void addSpyingEvent(SpyingEvent e) {
		super.addRow(new Object[] { e.getStringDate(), e.getEspionGD(), e.getEspionG(), e.getSourceClass(), e.getInfos() });
	}
	
	public SpyingEvent getSpyingEvent(int identifier) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
			return new SpyingEvent(dateFormat.parse((String) getValueAt(identifier, 0)), (String) getValueAt(identifier, 1), (String) getValueAt(identifier, 2), (String) getValueAt(identifier, 3), (String) getValueAt(identifier, 4));
		}
		catch (ParseException e) {
			return null;
		}
	}
	
	@Override
	public boolean isCellEditable(int row, int col) {
		return false;
	}
	
	@Override
	public Class<?> getColumnClass(int modelIndex) {
		return String.class;
	}
	
	@Override
	public int getColumnCount() {
		return columnArray.length;
	}
	
	@Override
	public String getColumnName(int modelIndex) {
		return columnArray[modelIndex];
	}
}