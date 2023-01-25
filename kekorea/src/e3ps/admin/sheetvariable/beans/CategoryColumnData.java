package e3ps.admin.sheetvariable.beans;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryColumnData {

	private String oid;
	private String cname;
	private int csort;
	private int version;
	private String iname;
	private int isort;

	public CategoryColumnData() {

	}

	public CategoryColumnData(Category category, CategoryItemsLink link) throws Exception {
		setOid(category.getPersistInfo().getObjectIdentifier().getStringValue());
		setCname(category.getName());
		setCsort(category.getSort());
		setVersion(category.getVersion());
		if (link != null) {
			Items items = link.getItems();
			setIname(items.getName());
			setIsort(items.getSort());
		}
	}
}
