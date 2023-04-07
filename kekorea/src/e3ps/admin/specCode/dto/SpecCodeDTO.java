package e3ps.admin.specCode.dto;

import java.util.ArrayList;

import e3ps.admin.commonCode.CommonCode;
import e3ps.admin.commonCode.dto.CommonCodeDTO;
import e3ps.admin.numberRuleCode.NumberRuleCode;
import e3ps.admin.spec.SpecCode;
import e3ps.common.util.CommonUtils;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecCodeDTO {

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
	private ArrayList<SpecCodeDTO> children = new ArrayList<>();

	public SpecCodeDTO() {

	}

	public SpecCodeDTO(SpecCode specCode) throws Exception {
		setOid(specCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(specCode.getName());
		setCode(specCode.getCode());
		setCodeType(specCode.getCodeType().toString());
		if (specCode.getParent() != null) {
			setParent_name(specCode.getParent().getName());
			setParent_code(specCode.getParent().getCode());
			setParent_code_type(specCode.getParent().getCodeType().toString());
			setParent_oid(specCode.getParent().getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setDescription(StringUtils.replaceToValue(specCode.getDescription()));
		setEnable(specCode.getEnable());
		setCreateDate_txt(CommonUtils.getPersistableTime(specCode.getCreateTimestamp()));
		setSort(specCode.getSort());
	}
}
