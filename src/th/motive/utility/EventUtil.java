package th.motive.utility;

import org.adempiere.base.event.IEventManager;
import org.osgi.service.event.Event;

public class EventUtil {
	public static final String EVENT_KEY_TABLE = "table";
	
	@SuppressWarnings("unchecked")
	public static <T> T getProperties (String key, Event event) {
		return (T) event.getProperty(key);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getPO (Event event) {
		return (T) event.getProperty(IEventManager.EVENT_DATA);
	}
}
