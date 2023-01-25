package e3ps.doc.column;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.doc.PRJDocument;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OldOutputColumnData {

	private String oid;
	private String name;
	private String number;
	private String description;
	private String location;
	private String state;
	private String version;
	private String creator;
	private String createDate;
	private String modifier;
	private String modifyDate;
	private String[] primary;
	private String iconPath;

	public OldOutputColumnData() throws Exception {

	}

	public OldOutputColumnData(PRJDocument document) throws Exception {
		setOid(document.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(document.getName());
		setNumber(document.getNumber());
		setDescription(document.getDescription());
		setLocation(document.getLocation().substring("/Default".length() + 1, document.getLocation().length()));
		setState(document.getLifeCycleState().getDisplay());
		setVersion(CommonUtils.getFullVersion(document));
		setCreator(document.getCreatorFullName());
		setCreateDate(CommonUtils.getPersistableTime(document.getCreateTimestamp()));
		setModifier(document.getModifierFullName());
		setModifyDate(CommonUtils.getPersistableTime(document.getModifyTimestamp()));
		setPrimary(ContentUtils.getPrimary(document));
		setIconPath(ContentUtils.getOpenIcon(document));
	}
}
