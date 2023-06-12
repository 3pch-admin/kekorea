package e3ps.admin.commonCode.dto;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCodeDTO {

	private String oid;
	private String name;
	private String code;
	private String codeType;
	private String parent_name;
	private String parent_oid;
	private String parent_code;
	private String parent_code_type;
	private String description;
	private boolean enable;
	private String createDate_txt;
	private String _$parent;
	private int sort;

	// 자식 처리
	private ArrayList<CommonCodeDTO> children = new ArrayList<>();

	public CommonCodeDTO() {

	}

	public CommonCodeDTO(CommonCode commonCode) throws Exception {
		setOid(commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(commonCode.getName());
		setCode(commonCode.getCode());
		setCodeType(commonCode.getCodeType().toString());
		if (commonCode.getParent() != null) {
			setParent_name(commonCode.getParent().getName() + " [" + commonCode.getCodeType().getDisplay() + "]");
			setParent_code(commonCode.getParent().getCode());
			setParent_code_type(commonCode.getParent().getCodeType().toString());
			setParent_oid(commonCode.getParent().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setDescription(StringUtils.replaceToValue(commonCode.getDescription()));
		setEnable(commonCode.getEnable());
		setCreateDate_txt(CommonUtils.getPersistableTime(commonCode.getCreateTimestamp()));
		setSort(commonCode.getSort());
	}
}
