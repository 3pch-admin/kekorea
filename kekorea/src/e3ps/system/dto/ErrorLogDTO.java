package e3ps.system.dto;

import e3ps.system.ErrorLog;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogDTO {

	private String oid;
	private String name;
	private String errorMsg;
	private String creator;
	private String createdDate_txt;

	public ErrorLogDTO() {

	}

	public ErrorLogDTO(ErrorLog errorLog) throws Exception {

	}
}
