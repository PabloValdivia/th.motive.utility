package th.motive.utility.jasperreport;

/**
 * contain constant use for jasperreport stuff
 * @author hieplq
 *
 */
public final class JRConstants {
	private JRConstants (){}//private constructor let other can't instance it
	public static final String PARAMETER_NAME_IS_REDUCE_EXCEL = "IS_REDUCE_EXCEL";
	public static final String PROPERTIES_NAME_IS_REDUCE_EXCEL = "net.sf.jasperreports.export.xls." + PARAMETER_NAME_IS_REDUCE_EXCEL;
	public static final String PROPERTIES_NAME_INCLUDE_ELEMENT = "net.sf.jasperreports.export.xls.include";
	public static final String PROPERTIES_NAME_EXCLUDE_ELEMENT = "net.sf.jasperreports.export.xls.exclude";
	
}
