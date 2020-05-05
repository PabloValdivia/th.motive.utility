package th.motive.utility;

import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.compiere.util.CCache;

/**
 * Util for manipulate string. focus to regex
 * @author hieplq
 *
 */
public final class MotiveStringUtils {
	/**
	 * cache complied regular expression
	 */
	private static final CCache<String, Pattern> patternCache = new CCache<String, Pattern>(null, "MotiveStringUtilsPatternCompiled", 5, false);
	
	/**
	 * replace string by other string, it's same {@link String#replaceAll(String, String)}
	 * @param src
	 * @param regSearch
	 * @param regReplacement
	 * @return
	 */
	public final static String regReplace (String src, String regSearch, String regReplacement) {
		return regReplace (src, regSearch, null, regReplacement);
	}
	
	/**
	 * get {@link Pattern} from cache
	 * @param regSearch
	 * @return
	 */
	public final static Pattern getRegPattern (String regSearch) {
		Pattern pattern = patternCache.get(regSearch);
		if (pattern == null) {
			synchronized (patternCache) {
				pattern = Pattern.compile(regSearch);
				patternCache.put(regSearch, pattern);
			}
			
			pattern = patternCache.get(regSearch);
		}
		
		return pattern;
	}
	
	/**
	 * use regular to replace string, use function to build up replace string
	 * @param src
	 * @param regSearch
	 * @param funcReplacement
	 * @return
	 */
	public final static String regReplace (String src, String regSearch, Function<Matcher, String> funcReplacement) {
		return regReplace (src, regSearch, funcReplacement, null);
	}
	
	/**
	 * use regular to replace string
	 * @param src
	 * @param regSearch
	 * @param funcReplacement
	 * @param regReplacement
	 * @return
	 */
	private static String regReplace (String src, String regSearch, Function<Matcher, String> funcReplacement, String regReplacement) {
		Pattern pattern = getRegPattern(regSearch);
		
		Matcher regexMatcher = pattern.matcher(src);
		StringBuffer buildNewStr = new StringBuffer();
		
		while (regexMatcher.find()) {
			if (funcReplacement != null) {
				regexMatcher = regexMatcher.appendReplacement(buildNewStr, funcReplacement.apply(regexMatcher));
			}else if (regReplacement != null) {
				regexMatcher = regexMatcher.appendReplacement(buildNewStr, regReplacement);
			}else {
				// like append empty string
			}
		}
		regexMatcher.appendTail(buildNewStr);
		
		return buildNewStr.toString();
	}

	/**
	 * append a text with end line
	 * @param queryBuild
	 * @param appendText
	 */
	public static void appendLine (StringBuilder queryBuild, String appendText) {
		queryBuild.append(appendText);
		queryBuild.append(System.lineSeparator());
	}
	
	public static void appendLine (StringBuilder queryBuild, String... textParts) {
		for (String textPart : textParts) {
			queryBuild.append(textPart);
		}
		queryBuild.append(System.lineSeparator());
	}
	
	public static void appendLine (StringBuilder queryBuild, Object appendText) {
		queryBuild.append(appendText);
		queryBuild.append(System.lineSeparator());
	}
	
	public static void appendLine (StringBuilder queryBuild, Object... textParts) {
		for (Object textPart : textParts) {
			queryBuild.append(textPart);
		}
		queryBuild.append(System.lineSeparator());
	}
}
