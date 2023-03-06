package e3ps.admin.commonCode.dto;

import java.sql.Timestamp;

import e3ps.admin.commonCode.CommonCode;
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
	private String parentName;
	private String description;
	private boolean enable;
	private Timestamp createDate;
	private String parent;

	public CommonCodeDTO() {

	}

	public CommonCodeDTO(CommonCode commonCode) throws Exception {
		setOid(commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(commonCode.getName());
		setCode(commonCode.getCode());
		setCodeType(commonCode.getCodeType().toString());
		CommonCode parent = commonCode.getParent();
		if (commonCode.getParent() != null) {
			setParentName(parent.getName() + " [" + parent.getCodeType().getDisplay() + "]");
			setParent(parent.getPersistInfo().getObjectIdentifier().getStringValue());
		}
		setDescription(StringUtils.replaceToValue(commonCode.getDescription()));
		setEnable(commonCode.isEnable());
		setCreateDate(commonCode.getCreateTimestamp());

	}
}
