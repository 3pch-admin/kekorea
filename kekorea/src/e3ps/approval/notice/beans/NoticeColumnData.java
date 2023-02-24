package e3ps.approval.notice.beans;

import java.sql.Timestamp;

import e3ps.approval.notice.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeColumnData {

	private String oid;
	private String name;
	private String description;
	private String creator;
	private Timestamp createdDate;

	public NoticeColumnData() {

	}

	public NoticeColumnData(Notice notice) throws Exception {
		setOid(oid);
	}
}
