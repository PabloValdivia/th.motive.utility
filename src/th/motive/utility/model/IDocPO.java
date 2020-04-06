package th.motive.utility.model;

public interface IDocPO {
	public void setProcessing (boolean isProcess);
	public void setDocStatus(String docStatus);
	public void setDocAction(String docAction);
	public void setReversal_ID (int docID);
	public void setDocumentNo(String docStatus);
	public void set_ValueOfColumn (String columnName, Object value);
	public Object get_Value (String columnName);
	public String getDocumentNo();
	public String getDescription();
	public void setDescription(String desc);
	default public void addDescription(String description) {
		String desc = getDescription();
		if (desc == null)
			setDescription(description);
		else{
			StringBuilder msgd = new StringBuilder(desc).append(" | ").append(description);
			setDescription(msgd.toString());
		}
	}
	public int get_ID();
	
	public void saveEx();
}
