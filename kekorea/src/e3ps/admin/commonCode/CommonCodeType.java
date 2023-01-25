package e3ps.admin.commonCode;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class CommonCodeType extends _CommonCodeType {

	private static final long serialVersionUID = -3208468737232060421L;
	public static final CommonCodeType CUSTOMER = toCommonCodeType("CUSTOMER");
	public static final CommonCodeType STEP = toCommonCodeType("STEP");
	public static final CommonCodeType INSTALL = toCommonCodeType("INSTALL");
	public static final CommonCodeType PROJECT = toCommonCodeType("PROJECT");
	public static final CommonCodeType PROJECT_TYPE = toCommonCodeType("PROJECT_TYPE");
}
