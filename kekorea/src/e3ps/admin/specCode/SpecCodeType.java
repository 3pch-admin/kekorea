package e3ps.admin.specCode;

import com.ptc.windchill.annotations.metadata.GenAsEnumeratedType;

@GenAsEnumeratedType
public class SpecCodeType extends _SpecCodeType {

	public static final SpecCodeType SPEC = toSpecCodeType("SPEC");
	public static final SpecCodeType OPTION = toSpecCodeType("OPTION");
}
