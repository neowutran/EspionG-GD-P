package model;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Map;

@SuppressWarnings("rawtypes")
public class ThreadInfosResponseEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	private String				_espionGD;
	private String				_espionG;
	private ArrayList					_response;
	
	public ThreadInfosResponseEvent(Object source, String espionGD, String espionG, ArrayList arrayList) {
		super(source);
		_espionGD = espionGD;
		_espionG = espionG;
		_response = arrayList;
	}
	
	public String getEspionGD() {
		return _espionGD;
	}
	
	public String getEspionG() {
		return _espionG;
	}
	
	public ArrayList getResponse() {
		return _response;
	}
	
}
