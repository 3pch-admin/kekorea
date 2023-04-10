package e3ps.admin.numberRuleCode;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class NumberRuleCodeType extends _NumberRuleCodeType {

	public static final NumberRuleCodeType SIZE = toNumberRuleCodeType("SIZE");
	public static final NumberRuleCodeType DRAWING_COMPANY = toNumberRuleCodeType("DRAWING_COMPANY");
	public static final NumberRuleCodeType WRITTEN_DOCUMENT = toNumberRuleCodeType("WRITTEN_DOCUMENT");
	public static final NumberRuleCodeType BUSINESS_SECTOR = toNumberRuleCodeType("BUSINESS_SECTOR");
	public static final NumberRuleCodeType CLASSIFICATION_WRITING_DEPARTMENT = toNumberRuleCodeType(
			"CLASSIFICATION_WRITING_DEPARTMENT");
}
