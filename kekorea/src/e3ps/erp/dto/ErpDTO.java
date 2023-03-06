package e3ps.erp.dto;

import e3ps.erp.ErpSendHistory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErpDTO {

	private String oid;
	private String name;
	private String resultMsg;
	private boolean result;
	private String sendType;
	private String sendQuery;

	public ErpDTO() {

	}

	public ErpDTO(ErpSendHistory history) throws Exception {
		setOid(history.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(history.getName());
		setResultMsg(history.getResultMsg());
		setResult(history.getResult());
		setSendType(history.getSendType());
		setSendQuery(history.getSendQuery());
	}
}
