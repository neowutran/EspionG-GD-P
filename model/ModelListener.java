package model;

import model.EspionGAddedEvent;
import model.EspionGDAddedEvent;
import model.EspionGDDeletedEvent;
import model.EspionGDeletedEvent;
import model.SpyingEventReceivedEvent;
import model.ThreadInfosResponseEvent;

public interface ModelListener {
	public void espionGDAdded(EspionGDAddedEvent e);
	
	/**
	 * 
	 * @deprecated This method is no longer used since no EspionGD sends any message related to this event.
	 */
	public void espionGDDeleted(EspionGDDeletedEvent e);
	
	public void espionGAdded(EspionGAddedEvent e);
	
	/**
	 * 
	 * @deprecated This method is no longer used since no EspionGD sends any message related to this event.
	 */
	public void espionGDeleted(EspionGDeletedEvent e);
	
	public void spyingEventReceived(SpyingEventReceivedEvent e);
	
	public void threadInfosResponse(ThreadInfosResponseEvent e);
}
