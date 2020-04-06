package th.motive.utility.db;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.compiere.util.Util;

/**
 * 
 * @author hieplq
 *
 */
public class DBUtill {
	public static void whereClauseRange(Timestamp from, Timestamp to, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder) {
		whereClauseRange (from, to, column, tableAlias, parameter, whereBuilder, true);
	}
	
	/**
	 * get time at start of day
	 * https://stackoverflow.com/questions/10308356/how-to-obtain-the-start-time-and-end-time-of-a-day
	 * @param day
	 * @return
	 */
	public static Timestamp getStartDay (Timestamp day) {
		LocalDate dateTime = day.toLocalDateTime().toLocalDate();
		return Timestamp.valueOf(dateTime.atStartOfDay());
	}
	
	/**
	 * get time at start of tomorrow of this day
	 * https://stackoverflow.com/questions/10308356/how-to-obtain-the-start-time-and-end-time-of-a-day
	 * @param day
	 * @return
	 */
	public static Timestamp getStartTomorrowDay (Timestamp day) {
		LocalDate dateTime = day.toLocalDateTime().toLocalDate();
		dateTime = dateTime.plusDays(1);
		return Timestamp.valueOf(dateTime.atStartOfDay());
	}
	
	/**
	 * use Half-Open technical
	 * https://stackoverflow.com/questions/10308356/how-to-obtain-the-start-time-and-end-time-of-a-day
	 * @param from
	 * @param to
	 * @param column
	 * @param tableAlias
	 * @param parameter
	 * @param whereBuilder
	 * @param appendAnd
	 */
	public static void whereClauseRange(Timestamp from, Timestamp to, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder, boolean appendAnd) {
		if (from == null)
			return;
		
		parameter.add(getStartDay(from));
		
		if (to == null)
			to = from;
		
		parameter.add(getStartTomorrowDay(to));
		
		appendOpen(appendAnd, whereBuilder);
		
		whereBuilder.append(" ? <= ");
		
		appendColumnName (column, tableAlias, whereBuilder);
		
		whereBuilder.append(" AND ");
		
		appendColumnName (column, tableAlias, whereBuilder);
		
		whereBuilder.append(" < ?) ");
	}

	public static void whereClauseRange(String from, String to, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder) {
		whereClauseRange (from, to, column, tableAlias, parameter, whereBuilder, true);
	}
	
	public static void whereClauseRange(String from, String to, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder, boolean appendAnd) {
		if (StringUtils.isEmpty(from))
			return;
		
		parameter.add(from);
		
		if (!StringUtils.isEmpty(to))
			parameter.add(to);
		
		appendOpen(appendAnd, whereBuilder);
		
		appendColumnName (column, tableAlias, whereBuilder);
		
		if (!Util.isEmpty(to))
			whereBuilder.append(" BETWEEN ? AND ?)");
		else
			appendEqualCondition(true, whereBuilder, Operate.EQUAL);
	}
	
	public static enum Operate{
		EQUAL,
		NON_EQUAL
	}
	
	public static void whereClause(Object value, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder) {
		whereClause(value, column, tableAlias, parameter, whereBuilder, true, Operate.EQUAL);
	}
	
	public static void whereClause(Object value, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder, Operate operate) {
		whereClause(value, column, tableAlias, parameter, whereBuilder, true, operate);
	}
	
	public static void whereClause(Object value, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder, boolean appendAnd) {
		whereClause(value, column, tableAlias, parameter, whereBuilder, appendAnd, Operate.EQUAL);
	}
	
	public static void whereClause(Object value, String column, String tableAlias, List<Object> parameter, StringBuilder whereBuilder, boolean appendAnd, Operate operate) {
		if (value == null || (value instanceof String && StringUtils.isEmpty((String)value)))
			return;
		
		parameter.add(value);
		
		appendOpen(appendAnd, whereBuilder);
		
		appendColumnName (column, tableAlias, whereBuilder);
		
		appendEqualCondition(true, whereBuilder, operate);
	}
	
	protected static void appendColumnName(String column, String tableAlias, StringBuilder whereBuilder) {
		if ("TH_FA_SubNumber".equals(column)) {
			whereBuilder.append("LPAD(");
		}
		
		if (tableAlias != null) {
			whereBuilder.append(tableAlias);
			whereBuilder.append(".");
		}
		whereBuilder.append(column);
		if ("TH_FA_SubNumber".equals(column)) {
			whereBuilder.append(", 4,'0')");
		}
	}
	
	protected static void appendOpen(boolean appendAnd, StringBuilder whereBuilder) {
		if (appendAnd)
			whereBuilder.append(" AND (");
		else
			whereBuilder.append(" (");
	}
	
	protected static void appendOpen(StringBuilder whereBuilder) {
		whereBuilder.append("(");
	}
	
	protected static void appendClose(StringBuilder whereBuilder) {
		whereBuilder.append(")");
	}
	
	protected static void appendEqualCondition(boolean withEnd, StringBuilder whereBuilder, Operate operate) {
		if (operate == Operate.EQUAL)
			whereBuilder.append(" = ");
		else
			whereBuilder.append(" <> ");
		
		whereBuilder.append("? ");
		
		if (withEnd)
			whereBuilder.append(") ");
	}
	
	protected static void appendEqualCondition(StringBuilder whereBuilder) {
		whereBuilder.append(" = ? ");
	}
}
