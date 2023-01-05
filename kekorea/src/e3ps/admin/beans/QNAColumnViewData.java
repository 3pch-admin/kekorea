package e3ps.admin.beans;

import e3ps.admin.QNA;
import e3ps.common.util.ContentUtils;

public class QNAColumnViewData {

	public QNA qna;
	public String name;
	public String creator;
	public String description;
	public String createDate;
	public String iconPath;

	public QNAColumnViewData(QNA qna) throws Exception {
		this.qna = qna;
		this.name = qna.getName();
		this.description = qna.getDescription();
		this.creator = qna.getOwnership().getOwner().getFullName();
		this.createDate = qna.getCreateTimestamp().toString().substring(0, 16);
		this.iconPath = ContentUtils.getStandardIcon(this.qna);
	}
}
