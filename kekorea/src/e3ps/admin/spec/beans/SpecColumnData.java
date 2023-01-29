package e3ps.admin.spec.beans;

import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpecColumnData {

	private String oid;
	private String name;
	private int sort;
	private int version;
	private boolean enable;
	private boolean config;
	private boolean history;
	private String oname;
	private int osort;

	public SpecColumnData() {

	}

	public SpecColumnData(Spec spec, SpecOptionsLink link) throws Exception {
		setOid(spec.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(spec.getName());
		setSort(spec.getSort());
		setEnable(spec.isEnable());
		setConfig(spec.isConfig());
		setHistory(spec.isHistroy());
		setVersion(spec.getVersion());
		if (link != null) {
			Options options = link.getOptions();
			setOname(options.getName());
			setOsort(options.getSort());
		}
	}
}
