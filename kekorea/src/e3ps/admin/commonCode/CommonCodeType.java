package e3ps.admin.commonCode;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class CommonCodeType extends _CommonCodeType {

	public static final CommonCodeType CUSTOMER = toCommonCodeType("CUSTOMER");
	public static final CommonCodeType STEP = toCommonCodeType("STEP");
	public static final CommonCodeType INSTALL = toCommonCodeType("INSTALL");
	public static final CommonCodeType PROJECT_TYPE = toCommonCodeType("PROJECT_TYPE");
	public static final CommonCodeType MAK = toCommonCodeType("MAK");
	public static final CommonCodeType SIZE = toCommonCodeType("SIZE");
	public static final CommonCodeType DRAWING_COMPANY = toCommonCodeType("DRAWING_COMPANY");
	public static final CommonCodeType WRITTEN_DOCUMENT = toCommonCodeType("WRITTEN_DOCUMENT");
	public static final CommonCodeType BUSINESS_SECTOR = toCommonCodeType("BUSINESS_SECTOR");
	public static final CommonCodeType CLASSIFICATION_WRITING_DEPARTMENT = toCommonCodeType(
			"CLASSIFICATION_WRITING_DEPARTMENT");
}
