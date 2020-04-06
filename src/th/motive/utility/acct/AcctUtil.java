package th.motive.utility.acct;

import org.compiere.model.MBankAccount;
import org.compiere.util.Env;

public class AcctUtil {
	private AcctUtil(){}// for prevent other create instance of a util class
	
	public static int getBankOrg (int bankId){
		if (bankId == 0)
			return 0;
		//
		MBankAccount ba = MBankAccount.get(Env.getCtx(), bankId);
		return ba.getAD_Org_ID();
	}
}
