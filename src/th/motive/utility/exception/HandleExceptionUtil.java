package th.motive.utility.exception;

import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;

/**
 * commont function to handle exception/log/error
 * @author hieplq
 *
 */
public class HandleExceptionUtil {
	private HandleExceptionUtil (){}; //for prevent other create instance of a util class
	
	/**
	 * raise a exception to stop process, also log message
	 * @param logger
	 * @param msg
	 * @return
	 */
	public static AdempiereUserError raiseException (CLogger logger, String msg){
		return HandleExceptionUtil.raiseException(logger, msg, null);
	}
	
	/**
	 * re throw a exception
	 * @param logger
	 * @param msg
	 * @param ex
	 * @return
	 */
	public static AdempiereUserError raiseException (CLogger logger, String msg, Exception ex){
		logger.severe(msg);
		if (ex != null)
			return new AdempiereUserError(msg, ex);
		else
			return new AdempiereUserError(msg);
	}
}