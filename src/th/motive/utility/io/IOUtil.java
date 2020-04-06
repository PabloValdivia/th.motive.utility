package th.motive.utility.io;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * contain util function relate to IO like file, path,...
 * @author hieplq
 *
 */
public class IOUtil {
	private IOUtil (){}//private constructor let other can't instance it
	
	public static final Pattern regexNormanlizeFileName = Pattern.compile("[^\\w]", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE | Pattern.MULTILINE);
	/**
	 * Create file name from input string. it will replace all none ascii letter or number by underscore
	 * give a correct file name
	 * @return
	 */
	public static String normanlizeFileName (String inputString){
		Matcher regexMatcher = regexNormanlizeFileName.matcher(inputString);
		return regexMatcher.replaceAll("_");
	}
}
