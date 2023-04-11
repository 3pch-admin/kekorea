package e3ps.admin.numberRuleCode.dto;

import java.util.ArrayList;

import e3ps.admin.numberRuleCode.NumberRuleCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NumberRuleCodeDTO {

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
	private ArrayList<NumberRuleCodeDTO> children = new ArrayList<>();

	public NumberRuleCodeDTO() {

	}

	public NumberRuleCodeDTO(NumberRuleCode numberRuleCode) throws Exception {
		setOid(numberRuleCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(numberRuleCode.getName());
		setCode(numberRuleCode.getCode());
		setCodeType(numberRuleCode.getCodeType().toString());
		if (numberRuleCode.getParent() != null) {
			setParent_name(numberRuleCode.getParent().getName());
			setParent_code(numberRuleCode.getParent().getCode());
			setParent_code_type(numberRuleCode.getParent().getCodeType().toString());
			setParent_oid(numberRuleCode.getParent().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setDescription(StringUtils.replaceToValue(numberRuleCode.getDescription()));
		setEnable(numberRuleCode.getEnable());
		setCreateDate_txt(CommonUtils.getPersistableTime(numberRuleCode.getCreateTimestamp()));
		setSort(numberRuleCode.getSort());
	}
}
