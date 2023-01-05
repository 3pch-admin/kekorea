package e3ps.doc.column;

import e3ps.common.util.ContentUtils;
import e3ps.doc.PRJDocument;
import wt.session.SessionHelper;

public class OldOutputColumnData {

	// OID 1번
	public String oid;
	// 리스트 컬럼
	public String name;
	public String number; // 문서 번호
	public String description;
	public String location;
	public String ke_number;
	public String kek_number; // 작번
	public String kek_description; // 작업내용
	public String mak;
	public String state;
	public String version;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;
	public String[] primary;
	// 아이콘
	public String iconPath;

//	public OldOutputColumnData(WTDocument output) throws Exception {
//		this(output, null);
//	}

	public OldOutputColumnData(PRJDocument output) throws Exception {
		this.oid = output.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = output.getName();
		this.number = output.getNumber();
		// this.work = "";
		this.description = output.getDescription();
		this.location = output.getLocation().substring(14, output.getLocation().length());
		this.ke_number = "";
		this.kek_number = "";
		this.kek_description = "";
		this.mak = "";
		this.state = output.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.version = output.getVersionIdentifier().getSeries().getValue() + "."
				+ output.getIterationIdentifier().getSeries().getValue();
		this.creator = output.getCreatorFullName();
		this.createDate = output.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = output.getModifierFullName();
		this.modifyDate = output.getModifyTimestamp().toString().substring(0, 16);
		this.primary = ContentUtils.getPrimary(this.oid);

		this.iconPath = ContentUtils.getStandardIcon(this.oid);
	}
}
