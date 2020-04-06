package th.motive.utility.component;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;

import org.adempiere.base.IDocFactory;
import org.compiere.acct.Doc;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MTable;
import org.compiere.util.AdempiereUserError;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * Common of implement IDocFactory will need data record to get more information <br/>
 * So make a abstract to do this common job <br/>
 * it also handle exception by log it in database <br/>
 * <b>It isn't common but if exists any factory have logic create Doc don't requirement record on table, this factory will disturb it</b>
 * @author hieplq
 *
 */
public abstract class AbstractDocFactory implements IDocFactory {
	private final static CLogger s_log = CLogger.getCLogger(AbstractDocFactory.class);
	public static final String INOUT_TABLE_UUID = "d3f7821b-1d9f-4031-b8e7-5ca1ebd120d2";

	/**
	 * get record data and pass to {@link #getDocument(MAcctSchema, int, ResultSet, String)} for investigate and get Doc <br/>
	 * sometime just depend on Record_ID or AD_Table_ID can decide make customer Doc, in that case, override this method <br/>
	 * 
	 * @exception throw AdempiereUserError when can't lookup record on database
	 */
	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, int Record_ID,
			String trxName) {
		
		if (!isSupportTable(AD_Table_ID)){
			return null;
		}
		
		MTable tableDefine = MTable.get(Env.getCtx(), AD_Table_ID);
		
		String tableName = tableDefine.getTableName();
		
		//
		Doc doc = null;
		StringBuilder sql = new StringBuilder("SELECT * FROM ")
			.append(tableName)
			.append(" WHERE ").append(tableName).append("_ID=? AND Processed='Y'");
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			pstmt = DB.prepareStatement (sql.toString(), trxName);
			pstmt.setInt (1, Record_ID);
			rs = pstmt.executeQuery ();
			if (rs.next ())
			{
				doc = getDocument(as, AD_Table_ID, rs, trxName);
			}
			else{
				String msg = "Not Found: " + tableName + "_ID=" + Record_ID + " on table " + tableName + ":SQL:" + sql.toString();
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
		return doc;
	}
	
	/**
	 * handle exception, extend class should override {@link #getDocumentLogic(MAcctSchema, int, ResultSet, String)}
	 */
	@Override
	public Doc getDocument(MAcctSchema as, int AD_Table_ID, ResultSet rs, String trxName) {
		try {
			if (!isSupportTable (AD_Table_ID)) {
				return null;
			}
			
			return getDocumentLogic (as, AD_Table_ID, rs, trxName);
		} catch (Exception e) {
			s_log.log (Level.SEVERE, e.toString(), e);
			throw new AdempiereUserError(e.getMessage(), e);
		}
	}
	
	/**
	 * extend class should implement logic to get Doc on this class
	 * @param as
	 * @param AD_Table_ID
	 * @param rs
	 * @param trxName
	 * @return
	 * @throws Exception
	 */
	protected abstract Doc getDocumentLogic (MAcctSchema as, int AD_Table_ID, ResultSet rs, String trxName) throws Exception;
	
	/**
	 * return true for table this factory support to create model
	 * @param tableID
	 * @return
	 */
	protected boolean isSupportTable (int AD_Table_ID) {
		MTable tableDefine = MTable.get(Env.getCtx(), AD_Table_ID);
		
		List<String> tableUUIDs = getSupportTableUUIDs();
		
		return tableUUIDs.contains(tableDefine.getAD_Table_UU()) || tableUUIDs.contains(String.valueOf(AD_Table_ID)) || tableUUIDs.contains(tableDefine.getTableName());
	}
	
	protected abstract List<String> getSupportTableUUIDs ();
}
