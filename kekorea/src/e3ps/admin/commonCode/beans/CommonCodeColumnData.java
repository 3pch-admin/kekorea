package e3ps.admin.commonCode.beans;

import java.sql.Timestamp;

import e3ps.admin.commonCode.CommonCode;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonCodeColumnData {

	private String oid;
	private String name;
	private String code;
	private String codeType;
	private String parentName;
	private String description;
	private boolean enable;
	private Timestamp createDate;

	public CommonCodeColumnData() {

	}

	public CommonCodeColumnData(CommonCode commonCode) throws Exception {
		setOid(commonCode.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(commonCode.getName());
		setCode(commonCode.getCode());
		setCodeType(commonCode.getCodeType().toString());
		CommonCode parent = commonCode.getParent();
		if (commonCode.getParent() != null) {
			setParentName(parent.getName() + " [" + parent.getCodeType().getDisplay() + "]");
		}
		setDescription(StringUtils.replaceToValue(commonCode.getDescription()));
		setEnable(commonCode.isEnable());
		setCreateDate(commonCode.getCreateTimestamp());
	}
}
