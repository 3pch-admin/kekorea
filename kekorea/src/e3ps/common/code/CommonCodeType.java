package e3ps.common.code;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class CommonCodeType extends _CommonCodeType {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3208468737232060421L;

	/**
	 * 고객사
	 */
	public static final CommonCodeType CUSTOMER = toCommonCodeType("CUSTOMER");

	/**
	 * 단계구분
	 */
	public static final CommonCodeType STEP = toCommonCodeType("STEP");

	/**
	 * 설치장소
	 */
	public static final CommonCodeType INSTALL = toCommonCodeType("INSTALL");

	/**
	 * 프로젝트
	 */
	public static final CommonCodeType PROJECT = toCommonCodeType("PROJECT");

	/**
	 * 프로젝트유형
	 */
	public static final CommonCodeType PROJECT_TYPE = toCommonCodeType("PROJECT_TYPE");

	/**
	 * 고객사
	 */
}
