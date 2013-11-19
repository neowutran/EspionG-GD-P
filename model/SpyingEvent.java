package model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 
 * Décrit un évènement d'espionnage reçu Contient également l'horodatage de ce dernier
 * 
 */
public class SpyingEvent {
	private Date	_date;
	private String	_espionGD;
	private String	_espionG;
	private String	_sourceClass;
	private String	_infos;
	
	public SpyingEvent(Date date, String espionGD, String espionG, String sourceClass, String infos) {
		_date = date;
		_espionGD = espionGD;
		_espionG = espionG;
		_sourceClass = sourceClass;
		_infos = infos;
	}
	
	public Date getDate() {
		return _date;
	}
	
	public String getStringDate() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss.SSS");
		return dateFormat.format(_date);
	}
	
	public String getEspionGD() {
		return _espionGD;
	}
	
	public String getEspionG() {
		return _espionG;
	}
	
	public String getSourceClass() {
		return _sourceClass;
	}
	
	public String getInfos() {
		return _infos;
	}
}
