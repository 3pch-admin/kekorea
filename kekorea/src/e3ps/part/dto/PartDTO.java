package e3ps.part.dto;

import java.sql.Timestamp;
import java.util.HashMap;

import e3ps.common.util.AUIGridUtils;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.part.service.PartHelper;
import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;
import wt.part.WTPart;

@Getter
@Setter
public class PartDTO {

	private String oid;
	private String thumnail;
	private String name;
	private String number;
	private String version;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private String state;
	private String location;
	private String preView;

	private HashMap<String, Object> attr = new HashMap<>();
	// cad iba 속성
	private String part_code;
	private String name_of_parts;
	private String material;
	private String remarks;
	private String reference;
	private String dwg_no;
	private String std_unit;
	private String maker;
	private String custname;
	private String cusname;
	private String price;
	private String currname;

	public PartDTO() {

	}

	public PartDTO(WTPart part) throws Exception {

		setOid(part.getPersistInfo().getObjectIdentifier().getStringValue());
		setThumnail(AUIGridUtils.getThumnailSmall(part));
		setName(part.getName());
		setVersion(CommonUtils.getFullVersion(part));
		setCreator(part.getCreatorFullName());
		setCreatedDate(part.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(part.getCreateTimestamp()));
		setModifier(part.getModifierFullName());
		setModifiedDate(part.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(part.getModifyTimestamp()));
		setState(part.getLifeCycleState().getDisplay());
		setLocation(part.getLocation());
//		setPreView(ContentUtils.getPreViewBase64(part));
		putAttr(part);
	}

	private void putAttr(WTPart part) throws Exception {
		EPMDocument epm = PartHelper.manager.getEPMDocument(part);

		if ((epm != null && epm.getAuthoringApplication().getDisplay().equalsIgnoreCase("CREO"))) {
			setName_of_parts(IBAUtils.getStringValue(part, "NAME_OF_PARTS"));
			setDwg_no(IBAUtils.getStringValue(part, "DWG_NO"));
		} else if ((epm != null && epm.getAuthoringApplication().getDisplay().equals("AUTOCAD"))) {
			setName_of_parts(IBAUtils.getStringValue(part, "TITLE1") + " " + IBAUtils.getStringValue(part, "TITLE2"));
			setDwg_no(IBAUtils.getStringValue(part, "DWG_No"));
		} else {
			setName_of_parts(part.getName());
			setDwg_no(part.getNumber());
		}
		setMaterial(IBAUtils.getStringValue(part, "MATERIAL"));
		setRemarks(IBAUtils.getStringValue(part, "REMARKS"));
		setReference(IBAUtils.getStringValue(part, "REF_NO"));
		setPart_code(IBAUtils.getStringValue(part, "PART_CODE"));
		setMaker(IBAUtils.getStringValue(part, "MAKER"));
		setCustname(IBAUtils.getStringValue(part, "CUSTNAME"));
		setCusname(IBAUtils.getStringValue(part, "CUSNAME"));
		setPrice(IBAUtils.getStringValue(part, "PRICE"));
		setCurrname(IBAUtils.getStringValue(part, "CURRNAME"));
	}
}
