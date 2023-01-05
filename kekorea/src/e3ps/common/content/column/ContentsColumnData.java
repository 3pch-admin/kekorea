package e3ps.common.content.column;

import e3ps.common.content.Contents;
import e3ps.common.util.ContentUtils;
import wt.doc.WTDocument;
import wt.enterprise.RevisionControlled;
import wt.session.SessionHelper;

public class ContentsColumnData {

	// 목록에서 보여 줄것만해서
	public String oid;
	public String number;
	public String name;
	public String filename;
	public String version;
	public String state;
	public String modifier;
	public String modifyDate;
	public String[] primary;
	public String description;
//	public String modelName;
	// 기타
	public String iconPath;
	
	public ContentsColumnData(Contents contents) throws Exception {
		this(contents, null);
	}

	public ContentsColumnData(Contents contents , WTDocument doc) throws Exception {
		this.oid = contents.getPersistInfo().getObjectIdentifier().getStringValue();
		this.number = contents.getNumber();
		this.name = contents.getName();
		this.filename = contents.getFileName();
		this.version = contents.getVersion();

		if (contents.getPersistables() instanceof RevisionControlled) {
			RevisionControlled rc = (RevisionControlled) contents.getPersistables();
			this.modifier = rc.getModifierFullName();
			this.modifyDate = rc.getModifyTimestamp().toString().substring(0, 16);
			this.state = rc.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		}
		// 기타
		this.iconPath = ContentUtils.getFileIcon(this.filename);
		this.primary = ContentUtils.getPrimary(contents);
		this.description = contents.getDescription();
//		this.modelName = IBAUtils.getStringValue((IBAHolder) contents.getPersistables(), "MODEL_NAME");
	}
}
