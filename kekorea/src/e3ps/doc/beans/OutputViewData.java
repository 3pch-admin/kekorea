package e3ps.doc.beans;

import java.util.Vector;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.session.SessionHelper;

public class OutputViewData {

	public WTDocumentMaster master;
	public WTDocument document;
	public String oid;
	public String name;
	public String number;
	public String description;
	public String state;
	public String stateKey;
	public String version;
	public String iteration;
	public String fullVersion;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;

	public String location;
	public String iconPath;

	public String[] primary;
	public Vector<String[]> secondarys;

	public boolean isLatest = true;
	public WTDocument latestObj;
	public String latestOid;

	// public boolean isModify = false;
	// public boolean isDelete = false;
	// public boolean isRevise = false;

	public boolean isModify = true;
	public boolean isDelete = true;
	public boolean isRevise = true;

	public boolean isCreator;
	public boolean isModifier;

	// public ArrayList<WTPart> refPart = new ArrayList<WTPart>();

	public OutputViewData(WTDocument document) throws Exception {
		this.master = (WTDocumentMaster) document.getMaster();
		this.document = document;
		this.oid = document.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = document.getName();
		this.number = document.getNumber();
		this.description = document.getDescription() != null ? document.getDescription() : "";
		this.state = document.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.stateKey = document.getLifeCycleState().toString();
		this.version = document.getVersionIdentifier().getSeries().getValue();
		this.iteration = document.getIterationIdentifier().getSeries().getValue();
		this.fullVersion = this.version + "." + this.iteration;
		this.creator = document.getCreatorFullName();
		this.createDate = document.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = document.getModifierFullName();
		this.modifyDate = document.getModifyTimestamp().toString().substring(0, 16);
		this.location = document.getLocation();
		this.iconPath = ContentUtils.getStandardIcon(this.document);
		this.primary = ContentUtils.getPrimary(this.document);
		this.secondarys = ContentUtils.getSecondary(this.document);

		this.isCreator = CommonUtils.isCreator(document);
		this.isModifier = CommonUtils.isModifier(document);

		// this.refPart = DocumentHelper.manager.getWTPart(this.document);

		// setButtonAuth();
	}

//	private void setButtonAuth() throws Exception {
//		// 수정 권한 ... 최신 버전
//		boolean isAdmin = CommonUtils.isAdmin();
//		if (this.isLatest) {
//			boolean checkState = checkState("INWORK") || checkState("RETURN");
//			// 사용자 권한 추가 예정..
//
//			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
//				this.isModify = true;
//			}
//		}
//
//		if (this.isLatest) {
//			boolean checkState = checkState("RELEASED");
//			// 최신이면서 승인안된것으로?
//			if (!checkState && isAdmin) {
//				this.isDelete = true;
//			}
//		}
//
//		// 문서 개정..
//		// 최신 버전이고 상태가 승인댐
//		if (this.isLatest) {
//			// boolean checkState = checkState("RELEASED") || checkState("RETURN");
//			boolean checkState = checkState("RELEASED");
//			// 작성자가 하게 할지???
//			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
//				this.isRevise = true;
//			}
//		}
//	}

//	private boolean checkState(String key) throws Exception {
//		return this.stateKey.equals(key);
//	}
}
