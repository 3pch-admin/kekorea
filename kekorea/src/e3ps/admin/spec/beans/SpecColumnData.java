package e3ps.admin.spec.beans;

import java.sql.Timestamp;

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
	private String creator;
	private Timestamp createdDate;
	private String oname;
	private int osort;

	public SpecColumnData() {

	}

	public SpecColumnData(Spec spec, SpecOptionsLink link) throws Exception {
		setOid(spec.getPersistInfo().getObjectIdentifier().getStringValue());
		setName(spec.getName());
		setSort(spec.getSort());
		setEnable(spec.getEnable());
		setCreator(spec.getOwnership().getOwner().getFullName());
		setCreatedDate(spec.getCreateTimestamp());
		setVersion(spec.getVersion());
		if (link != null) {
			Options options = link.getOptions();
			setOname(options.getName());
			setOsort(options.getSort());
		}
	}
}
