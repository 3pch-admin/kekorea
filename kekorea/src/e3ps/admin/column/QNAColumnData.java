package e3ps.admin.column;

import e3ps.admin.QNA;
import e3ps.common.util.ContentUtils;

public class QNAColumnData {
	public String oid;
	public String name;
	public String creator;
	public String description;
	public String createDate;
	public String iconPath;

	public QNAColumnData(QNA qna) throws Exception {
		this.oid = qna.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = qna.getName();
		this.description = qna.getDescription();
		this.creator = qna.getOwnership().getOwner().getFullName();
		this.createDate = qna.getCreateTimestamp().toString().substring(0, 16);
		this.iconPath = ContentUtils.getStandardIcon(qna);
	}
}