package th.motive.utility.component;

import org.adempiere.webui.Extensions;
import org.adempiere.webui.factory.IFormFactory;
import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.compiere.model.MForm;

import th.motive.utility.model.ModelUtil;

/**
 * support form factory
 * @author hieplq
 *
 */
public abstract class AbstractFormFactory implements IFormFactory{

	/**
	 * pass {@link MForm} to {@link ADForm} to get more information when init form <br/>
	 * it's ok to re-get {@link MForm} on {@link ADForm} and {@link IFormController} but this help to void duplicate code <br/>
	 * personal i like pass it from {@link Extensions#getForm(String)}, or ever better to create a new interface for it, help old code can work without any modify
	 *  
	 */
	@Override
	public ADForm newFormInstance(String formClass) {
		MForm form = ModelUtil.getPOByColumnValue(MForm.Table_Name, MForm.COLUMNNAME_Classname, formClass, null);
		return lookupFormLogic(form);
	}

	public abstract ADForm lookupFormLogic (MForm form);
}
