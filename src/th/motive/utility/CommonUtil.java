package th.motive.utility;

import java.util.Map;
import java.util.Properties;

import org.compiere.model.MCurrency;

public final class CommonUtil {
	private CommonUtil() {}// prevent other create a instance of this class 
	
	/**
	 * script engine prefix
	 */
	public static final String SCRIPT_ENGINE_PREFIX_BEANSHELL = "beanshell:";
	public static final String SCRIPT_ENGINE_PREFIX_GROOVY = "groovy:";
	public static final String SCRIPT_ENGINE_PREFIX_JYTHON = "jython:";
	
	public static final String PROCESS_ERROR_STR_EXISTS = "@Error@";
	
	/**
	 * common message id
	 */
	public static final String MSG_NOT_HANDLE_PO_EVENT = "MSG_NOT_HANDLE_PO_EVENT {0} {1}";
	public static final String MSG_PROCESS_NOT_SUPPORT_ON_THE_TAB = "PROCESS_NOT_SUPPORT_ON_THE_TAB";
	public static final String MSG_NOT_SUPPORT_TAX_TYPE = "NOT_SUPPORT_TAX_TYPE {0}";
	
	/**
	 * supported script engine on idempiere
	 * @author hieplq
	 *
	 */
	public enum ScriptEngineType {
		UNDEFINE, JYTHON, GROOVE, BEANSHELL;
	}
	
	/**
	 * engine name of MRule detect by prefix of value field
	 * @param value
	 * @return
	 */
	public static ScriptEngineType getEngineType (String value) {
		if (value == null)
			return ScriptEngineType.UNDEFINE;
		else if (value.startsWith(SCRIPT_ENGINE_PREFIX_BEANSHELL))
			return ScriptEngineType.BEANSHELL;
		else if (value.startsWith(SCRIPT_ENGINE_PREFIX_GROOVY))
			return ScriptEngineType.GROOVE;
		else if (value.startsWith(SCRIPT_ENGINE_PREFIX_JYTHON))
			return ScriptEngineType.JYTHON;
		else
			return ScriptEngineType.UNDEFINE;
	}

	public static <T, K> T getFromMap (Class<T> tClass, String key, Map<String, K> record) {
		return getFromMap (tClass, key, record, false);
	}
	
	public static <T, K> T getFromMapSql (Class<T> tClass, String key, Map<String, K> record) {
		return getFromMap (tClass, key, record, true);
	}
	/**
	 * get object from Map without cast
	 * @param <T>
	 * @param <K>
	 * @param tClass
	 * @param key
	 * @param record
	 * @return
	 */
	public static <T, K> T getFromMap (Class<T> tClass, String key, Map<String, K> record, boolean doUpperKey) {
		if (doUpperKey)
			key = key.toUpperCase();
		
		K objValue = record.get(key);
		if (objValue == null)
			return (T)null;
		else
			return tClass.cast(objValue);
	}
	
	private static int precisionForTH = 0;
	public static int getPrecisionForTH (Properties ctx) {
		if (precisionForTH == 0) {
			MCurrency thCurrency = MCurrency.get(ctx, "THB");
			precisionForTH = MCurrency.getStdPrecision(ctx, thCurrency.get_ID());
		}
		return precisionForTH;
	}
}
