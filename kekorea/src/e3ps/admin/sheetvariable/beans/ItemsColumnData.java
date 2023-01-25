package e3ps.admin.sheetvariable.beans;

import e3ps.admin.sheetvariable.Category;
import e3ps.admin.sheetvariable.CategoryItemsLink;
import e3ps.admin.sheetvariable.Items;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemsColumnData {

	private String oid;
	private String cname;
	private String name;
	private int sort;

	public ItemsColumnData() {

	}

	public ItemsColumnData(CategoryItemsLink link) throws Exception {
		Category category = link.getCategory();
		Items items = link.getItems();
		setOid(items.getPersistInfo().getObjectIdentifier().getStringValue());
		setCname(category.getName());
		setName(items.getName());
		setSort(items.getSort());
	}
}
