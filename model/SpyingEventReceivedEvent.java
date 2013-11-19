package model;

import java.util.EventObject;

public class SpyingEventReceivedEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	private String				_espionGD;
	private String				_espionG;
	private SpyingEvent			_event;
	
	public SpyingEventReceivedEvent(Object source, String espionGD, String espionG, SpyingEvent event) {
		super(source);
		_espionGD = espionGD;
		_espionG = espionG;
		_event = event;
	}
	
	public String getEspionGD() {
		return _espionGD;
	}
	
	public String getEspionG() {
		return _espionG;
	}
	
	public SpyingEvent getEvent() {
		return _event;
	}
	
}
