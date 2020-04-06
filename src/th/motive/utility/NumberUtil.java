package th.motive.utility;

import java.math.BigDecimal;
import java.util.Map;

import org.adempiere.exceptions.AdempiereException;

public final class NumberUtil {

	/**
	 * when do math with {@link BigDecimal}, we normal need to convert number (get from database) as {@link BigDecimal} 
	 * @param columnName
	 * @param record
	 * @param isZeroWhenNull
	 * @return
	 */
	public static BigDecimal getBigDecimal(String columnName, Map<String, Object> record, boolean isZeroWhenNull) {
		Object obj = record.get(columnName);
		if (obj == null) {
			if (isZeroWhenNull)
				return BigDecimal.ZERO;
			else
				return null;	
		} else if (obj instanceof BigDecimal) {
			return (BigDecimal)obj;
		}else if (obj instanceof Integer || obj instanceof Long){
			return BigDecimal.valueOf((long)obj);
		}else if (obj instanceof Float || obj instanceof Double) {
			return BigDecimal.valueOf((double)obj);
		}else {
			throw new AdempiereException (String.format("column $1%s can't cast to BigDecimal. current value $2%s", columnName, obj.toString()));
		}
	}

	/**
	 * refer: {@link #getBigDecimal(String, Map, boolean)}
	 * @param columnName
	 * @param record
	 * @return
	 */
	public static BigDecimal getBigDecimal(String columnName, Map<String, Object> record) {
		return getBigDecimal (columnName, record, false);
	}

}
