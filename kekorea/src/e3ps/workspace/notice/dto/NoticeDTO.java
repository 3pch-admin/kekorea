package e3ps.workspace.notice.dto;

import java.sql.Timestamp;
import java.util.ArrayList;

import e3ps.common.util.AUIGridUtils;
import e3ps.workspace.notice.Notice;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoticeDTO {

	private String oid;
	private String name;
	private String description;
	private String creator;
	private String creatorId;
	private Timestamp createdDate;
	private String primary;

	// 변수용
	private ArrayList<String> primarys = new ArrayList<>();

	public NoticeDTO() {

	}

	public NoticeDTO(Notice notice) throws Exception {
		setOid(notice.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(notice.getName());
		setDescription(notice.getDescription());
		setCreator(notice.getOwnership().getOwner().getFullName());
		setCreatorId(notice.getOwnership().getOwner().getName());
		setCreatedDate(notice.getCreateTimestamp());
		setPrimary(AUIGridUtils.primaryTemplate(notice));
	}
}
