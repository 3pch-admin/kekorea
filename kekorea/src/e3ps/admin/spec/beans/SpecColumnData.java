package e3ps.admin.spec.beans;

import e3ps.admin.spec.Spec;
import e3ps.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecColumnData {

	private String oid;
	private String name;
	private int sort;
	private boolean enable;

	public SpecColumnData() {

	}

	public SpecColumnData(Spec spec) throws Exception {
		setOid(spec.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(spec.getName());
		setSort(spec.getSort());
		setEnable(spec.isEnable());
	}
}
