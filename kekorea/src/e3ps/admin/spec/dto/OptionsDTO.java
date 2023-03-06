package e3ps.admin.spec.dto;

import e3ps.admin.spec.Options;
import e3ps.admin.spec.Spec;
import e3ps.admin.spec.SpecOptionsLink;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionsDTO {

	private String oid;
	private String sname;
	private String name;
	private int sort;

	public OptionsDTO() {

	}

	public OptionsDTO(SpecOptionsLink link) throws Exception {
		Spec spec = link.getSpec();
		Options options = link.getOptions();
		setOid(options.getPersistInfo().getObjectIdentifier().getStringValue());
		setSname(spec.getName());
		setName(options.getName());
		setSort(options.getSort());
	}
}
