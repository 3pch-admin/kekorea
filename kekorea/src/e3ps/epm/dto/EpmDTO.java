package e3ps.epm.dto;

import java.sql.Timestamp;
import java.util.HashMap;

import e3ps.common.util.CommonUtils;
import e3ps.common.util.IBAUtils;
import e3ps.common.util.ThumnailUtils;
import lombok.Getter;
import lombok.Setter;
import wt.epm.EPMDocument;

@Getter
@Setter
public class EpmDTO {

	private String oid;
	private String cadType;
	private String thumnail_mini;
	private String thumnail;
	private String name;
	private String version;
	private String modifier;
	private Timestamp modifiedDate;
	private String modifiedDate_txt;
	private String creator;
	private Timestamp createdDate;
	private String createdDate_txt;
	private String state;
	private String location;
	private String[] primary;
	private String description;
	private String preView;
	private String applicationType;

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

	private String creoViewURL;

	public EpmDTO() {

	}

	public EpmDTO(EPMDocument epm) throws Exception {
		String[] thum = ThumnailUtils.getThumnail(epm.getPersistInfo().getObjectIdentifier().getStringValue());
		setOid(epm.getPersistInfo().getObjectIdentifier().getStringValue());
		setCadType(epm.getDocType().getDisplay());
		setApplicationType(epm.getAuthoringApplication().getDisplay());
		setThumnail_mini(thum[1]);
		setThumnail(thum[0]);
		setName(epm.getName());
		setVersion(CommonUtils.getFullVersion(epm));
		setModifier(epm.getModifierFullName());
		setModifiedDate(epm.getModifyTimestamp());
		setModifiedDate_txt(CommonUtils.getPersistableTime(epm.getModifyTimestamp()));
		setCreator(epm.getCreatorFullName());
		setCreatedDate(epm.getCreateTimestamp());
		setCreatedDate_txt(CommonUtils.getPersistableTime(epm.getCreateTimestamp()));
		setState(epm.getLifeCycleState().getDisplay());
		setLocation(epm.getLocation());
		setDescription(epm.getDescription());
		setCreoViewURL(ThumnailUtils.creoViewURL(epm));
		putAttr(epm);
	}

	private void putAttr(EPMDocument epm) throws Exception {
		if (getApplicationType().equalsIgnoreCase("CREO")) {
			setName_of_parts(IBAUtils.getStringValue(epm, "NAME_OF_PARTS"));
			setDwg_no(IBAUtils.getStringValue(epm, "DWG_NO"));
		} else if (getApplicationType().equals("AUTOCAD")) {
			setName_of_parts(IBAUtils.getStringValue(epm, "TITLE1") + " " + IBAUtils.getStringValue(epm, "TITLE2"));
			setDwg_no(IBAUtils.getStringValue(epm, "DWG_No"));
		} else {
			setName_of_parts(epm.getName());
			setDwg_no(epm.getNumber());
		}
		setMaterial(IBAUtils.getStringValue(epm, "MATERIAL"));
		setRemarks(IBAUtils.getStringValue(epm, "REMARKS"));
		setReference(IBAUtils.getStringValue(epm, "REF_NO"));
		setPart_code(IBAUtils.getStringValue(epm, "PART_CODE"));
		setMaker(IBAUtils.getStringValue(epm, "MAKER"));
		setCustname(IBAUtils.getStringValue(epm, "CUSTNAME"));
		setCusname(IBAUtils.getStringValue(epm, "CUSNAME"));
		setPrice(IBAUtils.getStringValue(epm, "PRICE"));
		setCurrname(IBAUtils.getStringValue(epm, "CURRNAME"));
	}
}
