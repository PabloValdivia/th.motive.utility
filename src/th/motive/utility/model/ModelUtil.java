package th.motive.utility.model;

import java.math.RoundingMode;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.adempiere.exceptions.AdempiereException;
import org.apache.commons.lang3.StringUtils;
import org.compiere.model.MAccount;
import org.compiere.model.MAcctSchema;
import org.compiere.model.MDocType;
import org.compiere.model.MInOut;
import org.compiere.model.MInOutLine;
import org.compiere.model.MInvoice;
import org.compiere.model.MInvoiceLine;
import org.compiere.model.MInvoicePaySchedule;
import org.compiere.model.MLocator;
import org.compiere.model.MOrder;
import org.compiere.model.MOrderLine;
import org.compiere.model.MOrderPaySchedule;
import org.compiere.model.MPeriod;
import org.compiere.model.MTable;
import org.compiere.model.MWarehouse;
import org.compiere.model.PO;
import org.compiere.model.Query;
import org.compiere.model.X_AD_Reference;
import org.compiere.model.X_A_Asset_Addition;
import org.compiere.process.DocAction;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

/**
 * utility relate to model like common query, common transform, cache...
 * @author hieplq
 *
 */
public class ModelUtil {
	protected static CLogger log = CLogger.getCLogger (ModelUtil.class);
	
	private ModelUtil (){} //private constructor let other can't instance it
	
	/**
	 * it's common to get list of PO by parent key, link column,... so this function was born
	 * @param poTableName
	 * @param lookupColumnName
	 * @param lookupColumnValue
	 * @param trx
	 * @return
	 */
	public static <T extends PO> List<T> getListPOByColumnValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx){
		Query formQuery = getQueryForLookupPOByColumn (poTableName, trx, lookupColumnName, lookupColumnValue, true);
		return formQuery.list();
	}
	
	public static <T extends PO> T getPOByColumnValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx){
		return getPOByColumnValue (poTableName, lookupColumnName, lookupColumnValue, trx, false, true);
	}
	
	public static <T extends PO> T getPOByColumnLikeValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx){
		return getPOByColumnLikeValue (poTableName, lookupColumnName, lookupColumnValue, trx, false, true);
	}
	
	public static <T extends PO> T getPOByColumnValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx, boolean clientRestrict){
		return getPOByColumnValue (poTableName, lookupColumnName, lookupColumnValue, trx, false, clientRestrict);
	}
	
	/**
	 * it's common to get first record of child table by link column, so this function was born
	 * need to order it?
	 * @param poTableName
	 * @param lookupColumnName
	 * @param lookupColumnValue
	 * @param trx
	 * @return
	 */
	public static <T extends PO> T getPOByColumnValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx, boolean isOnlyOne, boolean clientRestrict){
		Query formQuery = getQueryForLookupPOByColumn (poTableName, trx, lookupColumnName, lookupColumnValue, clientRestrict);
		// maybe cache it
		if (isOnlyOne) {
			return formQuery.firstOnly();
		}else {
			return formQuery.first();
		}
	}
	
	public static <T extends PO> T getPOByColumnLikeValue (String poTableName, String lookupColumnName, Object lookupColumnValue, String trx, boolean isOnlyOne, boolean clientRestrict){
		Query formQuery = getQueryLikeForLookupPOByColumn (poTableName, trx, lookupColumnName, lookupColumnValue, clientRestrict);
		// maybe cache it
		if (isOnlyOne) {
			return formQuery.firstOnly();
		}else {
			return formQuery.first();
		}
	}
	
	private static Query getQueryForLookupPOByColumn (String poTableName, String trx, String lookupColumnName, Object lookupColumnValue, boolean clientRestrict) {
		Query formQuery = getQueryForLookupPO (poTableName, trx, clientRestrict, lookupColumnName + " = ?", lookupColumnValue);
		return formQuery;
	}
	
	private static Query getQueryLikeForLookupPOByColumn (String poTableName, String trx, String lookupColumnName, Object lookupColumnValue, boolean clientRestrict) {
		Query formQuery = getQueryForLookupPO (poTableName, trx, clientRestrict, lookupColumnName + " LIKE ?", lookupColumnValue);
		return formQuery;
	}
	
	public static <T extends PO> T getPOByCondition (String poTableName, boolean isOnlyOne, String trx, String whereCondition, Object ...parameters) {
		return getPOByCondition (poTableName, isOnlyOne, true, trx, whereCondition, parameters);
	}
	
	/**
	 * use {@link Query} to lookup PO by condition
	 * @param poTableName
	 * @param trx
	 * @param isOnlyOne
	 * @param whereCondition
	 * @param parameters
	 * @return
	 */
	public static <T extends PO> T getPOByCondition (String poTableName, boolean isOnlyOne, boolean clientRestrict, String trx, String whereCondition, Object ...parameters){
		Query formQuery = getQueryForLookupPOIncMarkDelete (poTableName, trx, clientRestrict, whereCondition, parameters);
		// maybe cache it
		if (isOnlyOne) {
			return formQuery.firstOnly();
		}else {
			return formQuery.first();
		}
	}
	
	public static <T extends PO> List<T> getListPOByCondition (String poTableName, String trx, String whereCondition, Object ...parameters){
		Query formQuery = getQueryForLookupPO (poTableName, trx, true, whereCondition, parameters);
		return formQuery.list();
	}
	
	private static Query getQueryForLookupPO (String poTableName, String trx, boolean clientRestrict, String whereCondition, Object ...parameters) {
		Query formQuery = new Query(Env.getCtx(), poTableName, whereCondition, trx);
		formQuery.setParameters(parameters);
		formQuery.setOnlyActiveRecords(true);
		if (clientRestrict)
			formQuery.setClient_ID();
		return formQuery;
	}
	
	private static Query getQueryForLookupPOIncMarkDelete (String poTableName, String trx, boolean clientRestrict, String whereCondition, Object ...parameters) {
		Query formQuery = new Query(Env.getCtx(), poTableName, whereCondition, trx);
		formQuery.setParameters(parameters);
//		formQuery.setOnlyActiveRecords(true);
		if (clientRestrict)
			formQuery.setClient_ID();
		return formQuery;
	}
	
	public static <T extends PO> List<T> getListPOByRawQuery (String poTableName, String rawQuery, Properties ctx, String trxName, Object ...parameters) throws SQLException {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<T> listPo = new ArrayList<T>();
		try
		{
			pstmt = DB.prepareStatement(rawQuery, trxName);
			DB.setParameters(pstmt, parameters);
			rs = pstmt.executeQuery();
			MTable table = MTable.get(ctx, poTableName);
			while (rs.next()) {
				@SuppressWarnings("unchecked")
				T po = (T)table.getPO(rs, trxName);
				listPo.add(po);
			}
		}finally{
			DB.close(rs, pstmt);				
		}
		return listPo;
	}
	
	/**
	 * it's common to get check on a parent record have child record, so this function was born <br/>
	 * WORK-AROUND:I like to count (*) it's light, but Query.count() have other meaning, it count per all table <br/> 
	 * so work-around by select first record 
	 * @param tableName
	 * @param columnKey
	 * @param parentKeyValue
	 * @param trx
	 * @return
	 */
	public static boolean hasRecord (String tableName, String columnKey, Object parentKeyValue, String trx){
		return  getPOByColumnValue (tableName, columnKey, parentKeyValue, trx) != null;
	}
	
	/**
	 * display type use record id to figure out.<br/>
	 * for default type it's ok, because record id is fixed <br/>
	 * but if plug-in define new display type. it's hard to use record id <br/>
	 * so this method are born, it use display type name
	 * @param key record id get from configuration "Reference" of column
	 * @param name handle by plug-in to compare
	 * @return
	 */
	public static boolean isDisplayType (int key, String name){
		X_AD_Reference displayType = new X_AD_Reference(Env.getCtx(), key, null);
		return "DataType".equals(displayType.getValidationType()) && displayType.getName().equals(name);
	}

	public static void setterColumnValueStrForPO (PO poObj, String columnName, String value, boolean isCreate) {
		if (StringUtils.isEmpty(value)) {
			setterColumnValueObjForPO (poObj, columnName, null, isCreate);
		}else {
			setterColumnValueObjForPO (poObj, columnName, value, isCreate);
		}
	}
	
	public static void setterColumnValueForPO (PO poObj, String columnName, Object value, boolean isCreate) {
		if (value instanceof String)
			setterColumnValueStrForPO (poObj, columnName, (String)value, isCreate);
		else
			setterColumnValueObjForPO (poObj, columnName, value, isCreate);
	}
	/*public static void setterColumnValueForPO (PO poObj, String columnName, Object value) {
		setterColumnValueForPO(poObj, columnName, value, true);
	}/*
	/**
	 * when call {@link PO#set_ValueOfColumn(String, Object)} ever value same old one, {@link PO} still do normal
	 * in case it's a readOnly column raise exception, so repeat code to check 
	 * @param poObj
	 * @param columnName
	 * @param value
	 */
	public static void setterColumnValueObjForPO (PO poObj, String columnName, Object value, boolean isCreate) {
		// to don't override default value or value set by before save,....
		if (isCreate && value == null)
			return;
		
		Object oldValue = poObj.get_Value(columnName);
		if (oldValue == null && value == null) {
			// do nothing
		}else if (oldValue != null && oldValue.equals(value)) {
			// do nothing
		}else {
			poObj.set_ValueOfColumn(columnName, value);
		}
		
	}
	
	public static MPeriod lookupPeriodByDateAcct (Properties ctx, int orgId, Timestamp dateAcct) {
		MPeriod period = null;
		if (dateAcct != null) {
			period = MPeriod.get(ctx, dateAcct, orgId, null);
		}
		return period;
	}
	
	public static void setDocReserver (IDocPO mainDoc, IDocPO reverseDoc) {
		reverseDoc.setProcessing(true);
		reverseDoc.setDocStatus(X_A_Asset_Addition.DOCSTATUS_Reversed);
		reverseDoc.setDocAction(X_A_Asset_Addition.DOCACTION_None);
		reverseDoc.set_ValueOfColumn(MInOut.COLUMNNAME_Reversal_ID, mainDoc.get_ID());
		
		StringBuilder msgadd = new StringBuilder("{->").append(mainDoc.getDocumentNo()).append(")");
		reverseDoc.addDescription(msgadd.toString());
		
		mainDoc.setProcessing (true);
		mainDoc.setDocStatus (X_A_Asset_Addition.DOCSTATUS_Reversed);
		mainDoc.setDocAction (X_A_Asset_Addition.DOCACTION_None);
		mainDoc.set_ValueOfColumn(MInOut.COLUMNNAME_Reversal_ID, reverseDoc.get_ID());
		msgadd = new StringBuilder("(").append(reverseDoc.getDocumentNo()).append("<-)");
		mainDoc.addDescription(msgadd.toString());
		
		//mainDoc.saveEx();
		
		//reverseDoc.saveEx();
	}
	
	public static void resetDocStatus (IDocPO reverseDoc) {
		reverseDoc.setDocumentNo(null);
		reverseDoc.setProcessing(false);
		reverseDoc.setDocStatus(X_A_Asset_Addition.DOCSTATUS_Drafted);
		reverseDoc.setDocAction(X_A_Asset_Addition.DOCACTION_Complete);
	}
	
	public static MAccount getSimpleMaccount (Properties ctx, MAcctSchema  acctSchema, int elementValueId, String trxName) {
		MAccount defaultAccount = MAccount.getDefault(acctSchema, true);
		MAccount account = MAccount.get(ctx,
	            Env.getAD_Client_ID(ctx),
	            Env.getAD_Org_ID(ctx),
	            acctSchema.getC_AcctSchema_ID(),
	            elementValueId,
	            defaultAccount.getC_SubAcct_ID(),
	            defaultAccount.getM_Product_ID(),
	            defaultAccount.getC_BPartner_ID(),
	            defaultAccount.getAD_OrgTrx_ID(),
	            defaultAccount.getC_LocFrom_ID(),
	            defaultAccount.getC_LocTo_ID(),
	            defaultAccount.getC_SalesRegion_ID(),
	            defaultAccount.getC_Project_ID(),
	            defaultAccount.getC_Campaign_ID(),
	            defaultAccount.getC_Activity_ID(),
	            defaultAccount.getUser1_ID(),
	            defaultAccount.getUser2_ID(),
	            defaultAccount.getUserElement1_ID(),
	            defaultAccount.getUserElement2_ID(),
	            trxName);
		return account;
	}
	
	public static MInvoice createInvoiceFromPurchase (MDocType dt, MOrder purchase)
	{
		if (log.isLoggable(Level.INFO)) 
			log.info(dt.toString());
		MInvoice invoice = new MInvoice (purchase, dt.getC_DocTypeInvoice_ID(), purchase.getDateOrdered());
		if (!invoice.save(purchase.get_TrxName()))
			throw new AdempiereException("Could not create Invoice");
		
		MOrderLine[] oLines = purchase.getLines();
		for (int i = 0; i < oLines.length; i++)
		{
			MOrderLine oLine = oLines[i];
			//
			MInvoiceLine iLine = new MInvoiceLine(invoice);
			iLine.setOrderLine(oLine);
			//	Qty = Ordered - Invoiced	
			iLine.setQtyInvoiced(oLine.getQtyOrdered().subtract(oLine.getQtyInvoiced()));
			if (oLine.getQtyOrdered().compareTo(oLine.getQtyEntered()) == 0)
				iLine.setQtyEntered(iLine.getQtyInvoiced());
			else
				iLine.setQtyEntered(iLine.getQtyInvoiced().multiply(oLine.getQtyEntered())
					.divide(oLine.getQtyOrdered(), 12, RoundingMode.HALF_UP));
			if (!iLine.save(purchase.get_TrxName()))
				throw new AdempiereException("Could not create Invoice Line from Order Line");
		}
		
		// Copy payment schedule from order to invoice if any
		for (MOrderPaySchedule ops : MOrderPaySchedule.getOrderPaySchedule(purchase.getCtx(), purchase.getC_Order_ID(), 0, purchase.get_TrxName())) {
			MInvoicePaySchedule ips = new MInvoicePaySchedule(purchase.getCtx(), 0, purchase.get_TrxName());
			PO.copyValues(ops, ips);
			ips.setC_Invoice_ID(invoice.getC_Invoice_ID());
			ips.setAD_Org_ID(ops.getAD_Org_ID());
			ips.setProcessing(ops.isProcessing());
			ips.setIsActive(ops.isActive());
			if (!ips.save())
				throw new AdempiereException ("ERROR: creating pay schedule for invoice from : "+ ops.toString());
		}
		return invoice;
	}	//	createInvoice

	public static MInOut createReceiptFromPurchase (MOrder purchase) {
		MInOut inout = new MInOut (purchase, 0, purchase.getDateOrdered());
		inout.setDocAction(MInOut.DOCACTION_Complete);
		inout.saveEx(purchase.get_TrxName());
		
		MOrderLine [] purchaseLines = purchase.getLines();
		MLocator defaultLocator = MLocator.getDefault((MWarehouse)inout.getM_Warehouse());
		if (defaultLocator == null)
			throw new AdempiereException("need a default locator on warehouse:" + inout.getM_Warehouse().getName());
		
		for (MOrderLine ol : purchaseLines) {
			MInOutLine iol = new MInOutLine (inout);
			iol.setM_Product_ID(ol.getM_Product_ID(), ol.getC_UOM_ID());	//	Line UOM
			iol.setQtyEntered(ol.getQtyOrdered());							//	Movement/Entered
			iol.setMovementQty (ol.getQtyOrdered());
			iol.setC_OrderLine_ID(ol.get_ID());
			iol.setM_AttributeSetInstance_ID(ol.getM_AttributeSetInstance_ID());
			iol.setDescription(ol.getDescription());
			//
			iol.setC_Project_ID(ol.getC_Project_ID());
			iol.setC_ProjectPhase_ID(ol.getC_ProjectPhase_ID());
			iol.setC_ProjectTask_ID(ol.getC_ProjectTask_ID());
			iol.setC_Activity_ID(ol.getC_Activity_ID());
			iol.setC_Campaign_ID(ol.getC_Campaign_ID());
			iol.setAD_OrgTrx_ID(ol.getAD_OrgTrx_ID());
			iol.setUser1_ID(ol.getUser1_ID());
			iol.setUser2_ID(ol.getUser2_ID());
			iol.setC_OrderLine_Ref_OrderLine_ID(ol.getOrderLineRefID());
			iol.setC_Charge_ID(ol.getC_Charge_ID());
			// Set locator
			iol.setM_Locator_ID(defaultLocator.get_ID());
			iol.saveEx(inout.get_TrxName());
		}
		
		if (!inout.processIt(DocAction.ACTION_Complete))
			throw new AdempiereException("Failed when processing document - " + inout.getProcessMsg());
		
		return inout;
	}
}
