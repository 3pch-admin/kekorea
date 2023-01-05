package e3ps.epm;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		iconProperties = @IconProperties(standardIcon = "/jsp/images/back_view.png", openIcon = "/jsp/images/back_view.png"),

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "fileName", type = String.class),

				@GeneratedProperty(name = "number", type = String.class, columnProperties = @ColumnProperties(columnName = "viewerNumber")),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

		})
public class ViewerData extends _ViewerData {

	static final long serialVersionUID = 1;

	public static ViewerData newViewerData() throws WTException {
		ViewerData instance = new ViewerData();
		instance.initialize();
		return instance;
	}
}
