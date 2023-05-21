package e3ps.system.dto;

import e3ps.common.util.CommonUtils;
import e3ps.system.ErrorLog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogDTO {

	private String oid;
	private String errorMsg;
	private String callUrl;;
	private String logType;
	private String creator;
	private String createdDate_txt;

	public ErrorLogDTO() {

	}

	public ErrorLogDTO(ErrorLog errorLog) throws Exception {
		setOid(errorLog.getPersistInfo().getObjectIdentifier().getStringValue());
		setErrorMsg(errorLog.getErrorMsg());
		setCallUrl(errorLog.getCallUrl());
		setLogType(errorLog.getLogType());
		setCreator(errorLog.getOwnership().getOwner().getFullName());
		setCreatedDate_txt(CommonUtils.getPersistableTime(errorLog.getCreateTimestamp(), 16));
	}
}
