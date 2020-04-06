package th.motive.utility.db;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.adempiere.exceptions.DBException;
import org.compiere.util.DB;

/**
 * add more function missing from {@link DB}
 * @author hieplq
 *
 */
public final class DBExtend {
	
	/**
	 * excecute query and put each record to {@link Map}, {@link List} of record convert to {@link List} of {@link Map}
	 * @param trxName
	 * @param sql
	 * @param params
	 * @return
	 * @throws DBException
	 */
	public static List<Map<String, Object>> excecuteQuery (String trxName, String sql, Object... params) throws DBException
    {
    	PreparedStatement pstmt = null;
    	ResultSet rs = null;
    	List<Map<String, Object>> listResult = new ArrayList<>();
    	try
    	{
    		pstmt = DB.prepareStatement(sql, trxName);
    		DB.setParameters(pstmt, params);
    		rs = pstmt.executeQuery();
    		while (rs.next()) {
    			Map<String, Object> record = new HashMap<>();
    			for (int columnIndex = 1; columnIndex <= rs.getMetaData().getColumnCount(); columnIndex++) {
    				record.put(rs.getMetaData().getColumnLabel(columnIndex), rs.getObject(columnIndex));
    			}
    			listResult.add(record);
    		}
    	}
    	catch (SQLException e)
    	{
    		throw new DBException(e, sql);
    	}
    	finally
    	{
    		DB.close(rs, pstmt);
    		rs = null; pstmt = null;
    	}
    	return listResult;
    }
	
	/**
	 * same {@link #excecuteQuery(String, String, Object...)} but get first record only, in case query return more than one, thru exception
	 * @param trxName
	 * @param sql
	 * @param params
	 * @return
	 * @throws DBException
	 */
	public static Map<String, Object> excecuteQueryFirstOnly (String trxName, String sql, Object... params) throws DBException{
		List<Map<String, Object>> result = excecuteQuery (trxName, sql, params);
		if (result.size() == 0) {
			return null;
		}else if (result.size() == 1) {
			return result.get(0);
		}else {
			throw new DBException("QueryMoreThanOneRecordsFound");
		}
	}
	
	/**
	 * same {@link #excecuteQuery(String, String, Object...)} but get only first record, ignore others
	 * @param trxName
	 * @param sql
	 * @param params
	 * @return
	 * @throws DBException
	 */
	public static Map<String, Object> excecuteQueryFirst (String trxName, String sql, Object... params) throws DBException{
		List<Map<String, Object>> result = excecuteQuery (trxName, sql, params);
		if (result.size() == 0) {
			return null;
		}else {
			return result.get(0);
		}
	}
}
