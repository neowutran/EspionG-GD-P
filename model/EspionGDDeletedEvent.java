package model;

import java.util.EventObject;

public class EspionGDDeletedEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	private String				_name;
	
	public EspionGDDeletedEvent(Object source, String name) {
		super(source);
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
}
