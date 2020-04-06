package th.motive.utility;

import java.text.MessageFormat;

import org.compiere.util.Env;
import org.compiere.util.Msg;

public class MsgUtil {
	public static String getMessage (String msg, String msgDefault, Object ...msgArgs) {
		String msgValue = Msg.getMsg(Env.getCtx(), msg, msgArgs);
		// not yet has define then return default value (message from dev)
		return !msgValue.equals(msg)?msg:MessageFormat.format(msgDefault, msgArgs);
	}
}
