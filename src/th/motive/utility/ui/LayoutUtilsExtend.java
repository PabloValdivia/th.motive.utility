package th.motive.utility.ui;

import org.zkoss.zk.ui.HtmlBasedComponent;

public class LayoutUtilsExtend {
	public static void removeSclass(String [] removeClass, HtmlBasedComponent target) {
		String targetSclass = target.getSclass();
		
		if (targetSclass == null)
			return;
		
		for(String cssClass : removeClass) {
			targetSclass = targetSclass.replaceAll("\\b" + cssClass + "\\b", "");
		}
		// refine placehold by remove
		targetSclass = targetSclass.trim().replace("  ", " ");
		
		if (targetSclass.length() == 0)
			targetSclass = null;
		
		target.setSclass(targetSclass);
	}
}
