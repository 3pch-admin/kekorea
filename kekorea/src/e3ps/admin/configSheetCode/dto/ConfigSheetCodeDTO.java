package e3ps.admin.configSheetCode.dto;

import java.util.ArrayList;

import e3ps.admin.configSheetCode.ConfigSheetCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfigSheetCodeDTO {

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
	private ArrayList<ConfigSheetCodeDTO> children = new ArrayList<>();

	public ConfigSheetCodeDTO() {

	}

	public ConfigSheetCodeDTO(ConfigSheetCode configSheetCode) throws Exception {
		setOid(configSheetCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(configSheetCode.getName());
		setCode(configSheetCode.getCode());
		setCodeType(configSheetCode.getCodeType().toString());
		if (configSheetCode.getParent() != null) {
			setParent_name(configSheetCode.getParent().getName());
			setParent_code(configSheetCode.getParent().getCode());
			setParent_code_type(configSheetCode.getParent().getCodeType().toString());
			setParent_oid(configSheetCode.getParent().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setDescription(StringUtils.replaceToValue(configSheetCode.getDescription()));
		setEnable(configSheetCode.getEnable());
		setCreateDate_txt(CommonUtils.getPersistableTime(configSheetCode.getCreateTimestamp()));
		setSort(configSheetCode.getSort());
	}
}
