package th.motive.utility.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;

import org.adempiere.base.IModelFactory;
import org.compiere.model.PO;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;

public abstract class AbstractModelFactory implements IModelFactory {
	private final static CLogger s_log = CLogger.getCLogger(AbstractModelFactory.class);
	
	@Override
	public Class<?> getClass(String tableName) {
		return null;
	}
	/**
	 * abstract don't handle case create new (Record_ID = 0), extend class have care this case <br/>
	 * it ever don't care about record 0 of table on list @see MTable#isZeroIDTable(String)
	 */
	@Override
	public PO getPO(String tableName, int Record_ID, String trxName) {
		if (!isSupportTable (tableName) || Record_ID == 0){
			return null;
		}
		
		PO po = null;
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
			.append(tableName)
			.append(" WHERE ").append(tableName).append("_ID=?");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				po = getPO(tableName, rs, trxName);
			}
			else{
				String msg = "Not Found: " + tableName + "_ID=" + Record_ID + " on table " +  tableName + ":SQL:" + sql.toString();
				s_log.log (Level.SEVERE, msg);
				throw new AdempiereUserError(msg);
			}
		}
		catch (SQLException e)
		{
			s_log.log (Level.SEVERE, sql.toString(), e);
			throw new AdempiereUserError(e.getMessage(), e);
		}
		finally
		{
			DB.close(rs, pstmt);
			rs = null;
			pstmt = null;
		}
		return po;
	}

	@Override
	public PO getPO(String tableName, ResultSet rs, String trxName) {
		try {
			if (!isSupportTable (tableName)){
				return null;
			}
			return getPOLogic(tableName, rs, trxName);
		} catch (Exception e) {
			s_log.log (Level.SEVERE, e.toString(), e);
			throw new AdempiereUserError(e.getMessage(), e);
		}
	}

	public abstract PO getPOLogic(String tableName, ResultSet rs, String trxName) throws Exception;
	
	protected abstract boolean isSupportTable(String tableName);
}
