package e3ps.epm.dto;

import java.util.HashMap;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.ContentUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.StringUtils;
import e3ps.common.util.ThumnailUtils;
import e3ps.epm.service.EpmHelper;
import e3ps.part.beans.PartViewData;
import wt.epm.EPMDocument;
import wt.epm.EPMDocumentMaster;
import wt.part.WTPart;
import wt.session.SessionHelper;

public class EpmViewData {

	public EPMDocument epm;
	public EPMDocumentMaster master;
	
	public String cadType;

	public String oid;
	public String number;
	public String name;
	public String state;
	public String stateKey;
	public String version;
	public String iteration;
	public String fullVersion;
	public String creator;
	public String creatorFullName;
	public String createDate;
	public String modifier;
	public String modifierFullName;
	public String modifyDate;
	public String name_of_parts;

	// cad type
	public String epmType;

	public String[] thumnail;
	public String[] representationData;

	public String creoView;
	public String iconPath;
	public String[] cadData;
	public String[] dwg;
	public String[] pdf;

	public WTPart part;
	public PartViewData pdata;
	public String location;

	public boolean isLatest = true;
	public EPMDocument latestObj;
	public String latestOid;
	public String description;

	public boolean is2D = false;
	public boolean is3D = false;
	public boolean isModify = false;
	public boolean isDelete = false;

	public boolean isLibrary = false;
	public boolean isProduct = false;

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
	public String itemClassName;
	public String itemClassSeq;

	public HashMap<String, Object> cadAttr = new HashMap<String, Object>();
	public HashMap<String, Object> productAttr = new HashMap<String, Object>();

	public EpmViewData(EPMDocument epm) throws Exception {
		cadType = epm.getAuthoringApplication().toString();
		this.epm = epm;
		this.oid = epm.getPersistInfo().getObjectIdentifier().getStringValue();
		this.master = (EPMDocumentMaster) epm.getMaster();
		this.name = epm.getName();
		// PROE , ACAD
		if ("PROE".equals(cadType)) {
			this.name_of_parts = IBAUtils.getStringValue(epm, "NAME_OF_PARTS");
			this.number = IBAUtils.getStringValue(epm, "DWG_NO");
		} else if ("ACAD".equals(cadType)) {
			this.number = IBAUtils.getStringValue(epm, "DWG_No");
			this.name_of_parts = IBAUtils.getStringValue(epm, "TITLE1") + " " + IBAUtils.getStringValue(epm, "TITLE2");
		} else {
			this.number = epm.getNumber();
			this.name_of_parts = epm.getName();
		}
		this.state = epm.getLifeCycleState().getDisplay(SessionHelper.manager.getLocale());
		this.stateKey = epm.getLifeCycleState().toString();
		this.version = epm.getVersionIdentifier().getSeries().getValue();
		this.iteration = epm.getIterationIdentifier().getSeries().getValue();
		this.fullVersion = this.version + "." + this.iteration;
		this.creator = epm.getCreatorName();
		this.creatorFullName = epm.getCreatorFullName();
		this.createDate = epm.getCreateTimestamp().toString().substring(0, 16);
		this.modifier = epm.getModifierName();
		this.modifierFullName = epm.getModifierFullName();
		this.modifyDate = epm.getModifyTimestamp().toString().substring(0, 16);
//		this.name_of_parts = IBAUtils.getStringValue(epm, "NAME_OF_PARTS");
		this.epmType = epm.getDocType().toString();
		this.thumnail = ThumnailUtils.getThumnail(this.oid);
		this.representationData = ContentUtils.getRepresentationData(this.epm);
		this.location = epm.getLocation();
		this.part = EpmHelper.manager.getPart(epm);
		if (this.part != null) {
			this.pdata = new PartViewData(this.part);
		}

		this.creoView = ThumnailUtils.creoViewURL(this.epm);
		this.iconPath = ContentUtils.getOpenIcon(this.epm);
		this.cadData = ContentUtils.getPrimary(this.epm);
		this.dwg = ContentUtils.getDWG(this.epm);
		this.pdf = ContentUtils.getPDF(this.epm);
		this.isLatest = CommonUtils.isLatestVersion(this.oid);

		this.latestObj = (EPMDocument) CommonUtils.getLatestVersion(this.epm);
		this.latestOid = latestObj.getPersistInfo().getObjectIdentifier().getStringValue();

		this.description = StringUtils.replaceToValue(epm.getDescription());

		// if (this.epm.getContainer().getName().equalsIgnoreCase("Commonspace")) {
		// //this.number = epm.getNumber();
		// this.number = IBAUtils.getStringValue(epm, "DWG_NO");
		// this.isProduct = true;
		// } else if (this.epm.getContainer().getName().equalsIgnoreCase("LIBRARY")) {
		// this.number = IBAUtils.getStringValue(this.epm, "SPEC");
		// this.isLibrary = true;
		// }

		setAuth();
		setEpmType();

		setCADAttr();
		setPRODUCTAttr();
	}

	private void setEpmType() {
		// CADCOMPONENTCADASSEMBLYCADCOMPONENTCADCOMPONENTCADCOMPONENTCADCOMPONENTCADCOMPONENTFORMAT
		if (this.epmType.equals("CADCOMPONENT") || this.epmType.equals("CADASSEMBLY")
				|| this.epmType.equals("FORMAT")) {
			this.is3D = true;
		}

		if (this.epmType.equals("CADDRAWING")) {
			this.is2D = true;
		}
	}

	private void setAuth() throws Exception {
		// 수정 권한 ... 최신 버전

		if (this.isLatest && CommonUtils.isAdmin()) {
			this.isModify = true;
		}

		if (this.isLatest && CommonUtils.isAdmin()) {
			this.isDelete = true;
		}
	}

	private void setPRODUCTAttr() throws Exception {
		this.machine_type = IBAUtils.getStringValue(this.epm, PRODUCTAttr.MACHINE_TYPE.name());
		this.parallel = IBAUtils.getStringValue(this.epm, PRODUCTAttr.PARALLEL.name());
		this.min_temp = IBAUtils.getIntegerValue(this.epm, PRODUCTAttr.MIN_TEMP.name());
		this.max_temp = IBAUtils.getIntegerValue(this.epm, PRODUCTAttr.MAX_TEMP.name());

		productAttr.put("machine_type", this.machine_type);
		productAttr.put("parallel", this.parallel);
		productAttr.put("min_temp", this.min_temp);
		productAttr.put("max_temp", this.max_temp);
	}

	private void setCADAttr() throws Exception {
		this.color_finish = IBAUtils.getStringValue(this.epm, CADAttr.COLOR_FINISH.name());
		this.main_assy = IBAUtils.getStringValue(this.epm, CADAttr.MAIN_ASSY.name());
		this.maker = IBAUtils.getStringValue(this.epm, "MAKER");
		this.material = IBAUtils.getStringValue(this.epm, CADAttr.MATERIAL.name());
		this.modeled_by = IBAUtils.getStringValue(this.epm, CADAttr.MODELED_BY.name());
		this.product_name = IBAUtils.getStringValue(this.epm, CADAttr.PRODUCT_NAME.name());
		this.spec = IBAUtils.getStringValue(this.epm, "SPEC");
		this.treatment = IBAUtils.getStringValue(this.epm, CADAttr.TREATMENT.name());
		this.drawing_by = IBAUtils.getStringValue(this.epm, CADAttr.DRAWING_BY.name());
		this.dimension = IBAUtils.getStringValue(this.epm, CADAttr.DIMENSION.name());
		this.bom = IBAUtils.getStringValue(this.epm, CADAttr.BOM.name());
		this.weight = IBAUtils.getStringValue(this.epm, CADAttr.WEIGHT.name());
		this.master_type = IBAUtils.getStringValue(this.epm, "MASTER_TYPE");
		this.erp_code = IBAUtils.getStringValue(this.epm, "ERP_CODE");
		this.itemClassName = IBAUtils.getStringValue(this.epm, "ITEMCLASSNAME");
		this.itemClassSeq = IBAUtils.getStringValue(this.epm, "ITEMCLASSSEQ");

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
		cadAttr.put("itemClassName", this.itemClassName);
		cadAttr.put("itemClassSeq", this.itemClassSeq);
	}
}