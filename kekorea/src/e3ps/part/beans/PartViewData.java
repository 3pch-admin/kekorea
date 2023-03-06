package e3ps.part.beans;

import java.util.ArrayList;
import java.util.HashMap;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import e3ps.epm.dto.CADAttr;
import e3ps.epm.dto.PRODUCTAttr;
import e3ps.part.service.PartHelper;
import wt.doc.WTDocument;
import wt.epm.EPMDocument;
import wt.part.WTPart;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.session.SessionHelper;

public class PartViewData {

	public WTPart part;
	public WTPartMaster master;
	public WTPartUsageLink link;

	public String oid;
	public int level;
	public String number;
	public String name;
	public String state;
	public String stateKey;
	public String version;
	public String fullVersion;
	public String iteration;
	public String creator;
	public String createDate;
	public String modifier;
	public String modifyDate;

	public String[] thumnail;
	public String[] representationData;

	public String iconPath;
	public String creoView;
	public String location;
	public boolean isLatest = true;
	public WTPart latestObj;
	public String latestOid;

	// product attr
	public String machine_type;
	public String parallel;
	public int min_temp;
	public int max_temp;

	// product attr
	public String color_finish;
	public String main_assy;
	public String maker;
	public String material;
	public String modeled_by;
	public String product_name;
	public String spec;
	public String treatment;
	public String drawing_by;
	public String dimension;
	public String bom;
	public String weight;
	public String master_type;
	public String erp_code;
	public String name_of_parts;
	public String dwg_no;

	public EPMDocument epm;

	public boolean isLibrary = false;
	public boolean isProduct = false;
	public boolean isEplan = false;

	public ArrayList<PartViewData> children = new ArrayList<PartViewData>();
	public PartViewData parent = null;

	public HashMap<String, Object> cadAttr = new HashMap<String, Object>();
	public HashMap<String, Object> productAttr = new HashMap<String, Object>();

	public String context;
	
	public String cadType = "";

	public boolean isModify = false;
	public boolean isDelete = false;
	public boolean isRevise = false;

	public boolean isCreator;
	public boolean isModifier;
	
	public ArrayList<WTDocument> refDocument = new ArrayList<WTDocument>();

	public PartViewData(WTPart part) throws Exception {
		this(part, null, 0);
	}

	public PartViewData(WTPart part, WTPartUsageLink link, int level) throws Exception {
		EPMDocument epm = e3ps.part.service.PartHelper.manager.getEPMDocument(part);
		
		if (epm != null) {
			cadType = epm.getAuthoringApplication().toString();
		}
		this.part = part;
		this.oid = part.getPersistInfo().getObjectIdentifier().getStringValue();
		this.master = (WTPartMaster) part.getMaster();
		this.level = level;
		// this.number = part.getNumber();

		if (link != null) {
			this.link = link;
		}

		if ("PROE".equals(cadType)) {
			this.name_of_parts = IBAUtils.getStringValue(part, "NAME_OF_PARTS");
			this.number = IBAUtils.getStringValue(part, "DWG_NO");
		} else if ("ACAD".equals(cadType)) {
			this.number = IBAUtils.getStringValue(part, "DWG_No");
			this.name_of_parts = IBAUtils.getStringValue(part, "TITLE1") + " "
					+ IBAUtils.getStringValue(part, "TITLE2");
		} else {
			this.number = part.getNumber();
			this.name_of_parts = part.getName();
		}

		this.dwg_no = IBAUtils.getStringValue(part, "DWG_NO");
		this.name = part.getName();
		// this.name = IBAUtils.getStringValue(part, "PRODUCT_NAME");
		this.state = part.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.stateKey = part.getLifeCycleState().toString();
		this.version = part.getVersionIdentifier().getSeries().getValue();
		this.iteration = part.getIterationIdentifier().getSeries().getValue();
		this.fullVersion = this.version + "." + this.iteration;
		this.creator = part.getCreatorFullName();
		this.createDate = part.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = part.getModifierFullName();
		this.modifyDate = part.getModifyTimestamp().toString().substring(0, 16);
		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.representationData = ContentUtils.getRepresentationData(this.part);

		this.iconPath = ContentUtils.getStandardIcon(this.part);
		this.creoView = ThumnailUtils.creoViewURL(this.part);

		this.location = part.getLocation();
		this.isLatest = CommonUtils.isLatestVersion(this.oid);

		this.latestObj = (WTPart) CommonUtils.getLatestVersion(this.part);
		this.latestOid = latestObj.getPersistInfo().getObjectIdentifier().getStringValue();

		this.epm = PartHelper.manager.getEPMDocument(this.part);

		if (this.part.getContainer().getName().equalsIgnoreCase("Commonspace")) {
			this.isProduct = true;
			this.context = "product";
		} else if (this.part.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
			this.isLibrary = true;
			this.context = "library";
		} else if (this.part.getContainer().getName().equalsIgnoreCase("EPLAN")) {
			this.isLibrary = true;
			this.context = "eplan";
		}

		this.isCreator = CommonUtils.isCreator(this.part);
		this.isModifier = CommonUtils.isModifier(this.part);

		setCADAttr();
		setPRODUCTAttr();

		setButtonAuth();

		this.refDocument = PartHelper.manager.getWTDocument(part);
	}

	private boolean checkState(String key) throws Exception {
		return this.stateKey.equals(key);
	}

	private void setButtonAuth() throws Exception {
		// 수정 권한 ... 최신 버전
		boolean isAdmin = CommonUtils.isAdmin();
		if (this.isLatest) {
			boolean checkState = checkState("INWORK");
			// 사용자 권한 추가 예정..

			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
				this.isModify = true;
			}
		}

		if (this.isLatest) {
			boolean checkState = checkState("RELEASED");
			// 최신이면서 승인안된것으로?
			if (!checkState && isAdmin) {
				this.isDelete = true;
			}
		}

		// 문서 개정..
		// 최신 버전이고 상태가 승인댐
		if (this.isLatest) {
			boolean checkState = checkState("RELEASED") || checkState("RETURN");
			// 작성자가 하게 할지???
			if ((checkState && (this.isCreator || this.isModifier)) || isAdmin) {
				this.isRevise = true;
			}
		}
	}

	private void setPRODUCTAttr() throws Exception {
		this.machine_type = IBAUtils.getStringValue(this.part, PRODUCTAttr.MACHINE_TYPE.name());
		this.parallel = IBAUtils.getStringValue(this.part, PRODUCTAttr.PARALLEL.name());
		this.min_temp = IBAUtils.getIntegerValue(this.part, PRODUCTAttr.MIN_TEMP.name());
		this.max_temp = IBAUtils.getIntegerValue(this.part, PRODUCTAttr.MAX_TEMP.name());

		productAttr.put("machine_type", this.machine_type);
		productAttr.put("parallel", this.parallel);
		productAttr.put("min_temp", this.min_temp);
		productAttr.put("max_temp", this.max_temp);
	}

	private void setCADAttr() throws Exception {
		this.color_finish = IBAUtils.getStringValue(this.part, CADAttr.COLOR_FINISH.name());
		this.main_assy = IBAUtils.getStringValue(this.part, CADAttr.MAIN_ASSY.name());
		this.maker = IBAUtils.getStringValue(this.part, "MAKER");
		this.material = IBAUtils.getStringValue(this.part, CADAttr.MATERIAL.name());
		this.modeled_by = IBAUtils.getStringValue(this.part, CADAttr.MODELED_BY.name());
		this.product_name = IBAUtils.getStringValue(this.part, CADAttr.PRODUCT_NAME.name());
		this.spec = IBAUtils.getStringValue(this.part, "SPEC");
		this.treatment = IBAUtils.getStringValue(this.part, CADAttr.TREATMENT.name());
		this.drawing_by = IBAUtils.getStringValue(this.part, CADAttr.DRAWING_BY.name());
		this.dimension = IBAUtils.getStringValue(this.part, CADAttr.DIMENSION.name());
		this.bom = IBAUtils.getStringValue(this.part, CADAttr.BOM.name());
		this.weight = IBAUtils.getStringValue(this.part, CADAttr.WEIGHT.name());
		this.master_type = IBAUtils.getStringValue(this.part, "MASTER_TYPE");
		this.erp_code = IBAUtils.getStringValue(this.part, "ERP_CODE");

		cadAttr.put("color_finish", this.color_finish);
		cadAttr.put("main_assy", this.main_assy);
		cadAttr.put("maker", this.maker);
		cadAttr.put("material", this.material);
		cadAttr.put("modeled_by", this.modeled_by);
		cadAttr.put("product_name", this.product_name);
		cadAttr.put("spec", this.spec);
		cadAttr.put("treatment", this.treatment);
		cadAttr.put("drawing_by", this.drawing_by);
		cadAttr.put("dimension", this.dimension);
		cadAttr.put("bom", this.bom);
		cadAttr.put("weight", this.weight);
		cadAttr.put("master_type", this.master_type);
		cadAttr.put("erp_code", this.erp_code);
	}
}
