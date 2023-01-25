package e3ps.admin.sheetvariable;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;

import wt.fc.ObjectToObjectLink;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "category", type = Category.class),

		roleB = @GeneratedRole(name = "items", type = Items.class)

)
public class CategoryItemsLink extends _CategoryItemsLink {

	static final long serialVersionUID = 1;

	public static CategoryItemsLink newCategoryItemsLink(Category category, Items items) throws WTException {
		CategoryItemsLink instance = new CategoryItemsLink();
		instance.initialize(category, items);
		return instance;
	}
}
