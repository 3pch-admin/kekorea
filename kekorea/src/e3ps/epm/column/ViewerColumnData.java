package e3ps.epm.column;

import java.util.Vector;

import e3ps.common.util.ContentUtils;
import e3ps.epm.ViewerData;

public class ViewerColumnData {

	public String oid;
	public String name;
	public String number;
	public String description;
	public String fileName;
	public String creator;
	public String createDate;
	public String iconPath;
	public Vector<String[]> viewer;
	public String[] primary;

	public ViewerColumnData(ViewerData viewerData) throws Exception {
		this.oid = viewerData.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = viewerData.getName();
		this.number = viewerData.getNumber();
		this.description = viewerData.getDescription();
		this.fileName = viewerData.getFileName();
		this.creator = viewerData.getOwnership().getOwner().getFullName();
		this.createDate = viewerData.getCreateTimestamp().toString().substring(0, 10);
		this.viewer = ContentUtils.getSecondary(viewerData);
		this.iconPath = ContentUtils.getStandardIcon(viewerData);
		this.primary = ContentUtils.getPrimary(viewerData);
		System.out.println(primary.length);
	}
}
