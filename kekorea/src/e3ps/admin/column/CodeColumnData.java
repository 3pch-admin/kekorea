package e3ps.admin.column;

import e3ps.common.code.CommonCode;

public class CodeColumnData {

	public String oid;
	public String name;
	public String code;
	public String description;
	public String codeType;
	public String uses;
	public int sort;
	public int depth;

	public String iconPath;

	public CodeColumnData(CommonCode code) throws Exception {
		this.oid = code.getPersistInfo().getObjectIdentifier().getStringValue();
		this.name = code.getName();
		this.code = code.getCode();
		this.codeType = code.getCodeType().toString();
		this.description = code.getDescription();
		this.uses = code.isUses() == true ? "사용" : "사용안함";
		this.sort = code.getSort();
		this.depth = code.getDepth();

		this.iconPath = "/Windchill/jsp/images/analysis.png";
	}
}
