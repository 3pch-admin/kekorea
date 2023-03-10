package e3ps.doc.dto;

import java.util.ArrayList;
import java.util.Vector;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.doc.service.DocumentHelper;
import wt.doc.WTDocument;
import wt.doc.WTDocumentMaster;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class DocumentViewData {

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

	public boolean isModify = false;
	public boolean isDelete = false;
	public boolean isRevise = false;

	public boolean isCreator;
	public boolean isModifier;

	public ArrayList<WTPart> refPart = new ArrayList<WTPart>();

	public DocumentViewData(WTDocument document) throws Exception {
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

		this.isLatest = CommonUtils.isLatestVersion(this.oid);
		this.latestObj = (WTDocument) CommonUtils.getLatestVersion(this.document);
		this.latestOid = latestObj.getPersistInfo().getObjectIdentifier().getStringValue();

		this.isCreator = CommonUtils.isCreator(document);
		this.isModifier = CommonUtils.isModifier(document);

		this.refPart = DocumentHelper.manager.getWTPart(this.document);

		setButtonAuth();
	}

	private void setButtonAuth() throws Exception {
		// ?????? ?????? ... ?????? ??????
		boolean isAdmin = CommonUtils.isAdmin();
		if (this.isLatest) {
			boolean checkState = checkState("INWORK") || checkState("RETURN");
			// ????????? ?????? ?????? ??????..

			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
				this.isModify = true;
			}
		}

		if (this.isLatest) {
			boolean checkState = checkState("APPROVED");
			// ??????????????? ??????????????????????
			if (!checkState && isAdmin) {
				this.isDelete = true;
			}
		}

		// ?????? ??????..
		// ?????? ???????????? ????????? ?????????
		if (this.isLatest) {
			boolean checkState = checkState("APPROVED") || checkState("RETURN");
			// boolean checkState = checkState("RELEASED");
			// ???????????? ?????? ?????????
			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
				this.isRevise = true;
			}
		}
	}

	private boolean checkState(String key) throws Exception {
		return this.stateKey.equals(key);
	}
}
