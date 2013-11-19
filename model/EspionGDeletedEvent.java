package model;

import java.util.EventObject;

public class EspionGDeletedEvent extends EventObject {
	private static final long	serialVersionUID	= 1L;
	
	private String				_parentName;
	private String				_name;
	
	public EspionGDeletedEvent(Object source, String parentName, String name) {
		super(source);
		_parentName = parentName;
		_name = name;
	}
	
	public String getParentName() {
		return _parentName;
	}
	
	public String getName() {
		return _name;
	}
}
