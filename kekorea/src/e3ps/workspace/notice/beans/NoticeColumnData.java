package e3ps.workspace.notice.beans;

import java.sql.Timestamp;

import e3ps.common.util.AUIGridUtils;
import e3ps.workspace.notice.Notice;
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
	private String primary;

	public NoticeColumnData() {

	}

	public NoticeColumnData(Notice notice) throws Exception {
		setOid(notice.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(notice.getName());
		setDescription(notice.getDescription());
		setCreator(notice.getOwnership().getOwner().getFullName());
		setCreatedDate(notice.getCreateTimestamp());
		setPrimary(AUIGridUtils.primaryTemplate(notice));
	}
}
