package e3ps.part.beans;

import java.util.ArrayList;

import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import wt.part.WTPart;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;

public class BomTreeData {

	public static String expand = "expand";
	public static String collapse = "collapse";
	public static String empty = "empty";

	public String treeIcon;
	public int level;
	public String findNumber = "";
	public String name;
	public String number;
	public String oid;
	public String version;
	public String createDate;
	public String creator;
	public String state;
	public String stateKey;
	public String iconPath;

	public double qty;
	public WTPart part;
	public WTPartUsageLink link;

	public String[] thumnail;

	public BomTreeData parent = null;
	public boolean open = false;
	public boolean view = false;
	// treeIcon = action
	public ArrayList<BomTreeData> children = new ArrayList<BomTreeData>();

	public boolean isMinus = false;

	public BomTreeData(WTPart part, WTPartUsageLink link, int level) throws Exception {
		init(part, link, level, expand, true);
	}

	public BomTreeData(WTPart part, WTPartUsageLink link, int level, String action, boolean view) throws Exception {
		init(part, link, level, action, view);
	}

	private void init(WTPart part, WTPartUsageLink link, int level, String action, boolean view) throws Exception {
		this.part = part;
		this.link = link;
		this.level = level;
		this.oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = part.getName();
		this.number = part.getNumber();

		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.treeIcon = action;

		if (link != null) {
			this.findNumber = link.getFindNumber() != null ? link.getFindNumber() : "";
			this.qty = link.getQuantity().getAmount();
		}
		this.iconPath = ContentUtils.getStandardIcon(part);
		this.state = part.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.stateKey = part.getLifeCycleState().toString();
		this.version = part.getVersionIdentifier().getSeries().getValue() + "."
				+ part.getIterationIdentifier().getSeries().getValue();
		this.creator = part.getCreatorFullName();
		this.createDate = part.getCreateTimestamp().toString().substring(0, 16);
		this.view = view;
		this.isMinus = IBAUtils.getBooleanValue(this.part, "MINUS");
	}
}
