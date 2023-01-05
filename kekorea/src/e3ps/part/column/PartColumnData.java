package e3ps.part.column;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.ThumnailUtils;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class PartColumnData {

	// 목록에서 보여 줄것만해서
	public String oid;
	public String number;
	public String[] thumnail;
	public String name;
	public String state;
	public String version;
	public String creator;
	public String createDate;

	// 기타
	public String iconPath;

	public PartColumnData(WTPart part) throws Exception {
		this.oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
		this.number = part.getNumber();
		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.name = part.getName();
		this.state = part.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.version = part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue();
		this.creator = part.getCreatorFullName();
		this.createDate = part.getCreateTimestamp().toString().substring(0, 16);

		// 기타
		this.iconPath = ContentUtils.getOpenIcon(this.oid);
	}

	public String getValue(String key) {
		String value = "";
		if (key.equals("name")) {
			value = this.name;
		} else if (key.equals("number")) {
			value = this.number;
		} else if (key.equals("thumnail")) {
			value = this.thumnail[0] + "†" + this.thumnail[1] + "†" + this.thumnail[2];
		} else if (key.equals("version")) {
			value = this.version;
		} else if (key.equals("state")) {
			value = this.state;
		} else if (key.equals("createDate")) {
			value = this.createDate;
		} else if (key.equals("creator")) {
			value = this.creator;
		}
		return value;
	}
}
