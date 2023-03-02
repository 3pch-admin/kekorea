package e3ps.erp.beans;

import e3ps.erp.ErpSendHistory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpColumnData {

	private String oid;
	private String name;
	private String resultMsg;
	private boolean result;
	private String sendType;
	private String sendQuery;

	public ErpColumnData() {

	}

	public ErpColumnData(ErpSendHistory history) throws Exception {
		setOid(history.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(history.getName());
		setResultMsg(history.getResultMsg());
		setResult(history.getResult());
		setSendType(history.getSendType());
		setSendQuery(history.getSendQuery());
	}
}
