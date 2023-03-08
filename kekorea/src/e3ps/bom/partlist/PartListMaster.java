package e3ps.bom.partlist;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "totalPrice", type = Double.class, javaDoc = "전체 금액", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "수배표이름", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "engType", type = String.class, javaDoc = "설계타입", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "version", type = String.class, initialValue = "\"A\"", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "number", type = String.class, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(columnName = "MASTERNUMBER"))

		}

)
public class PartListMaster extends _PartListMaster {

	static final long serialVersionUID = 1;

	public static PartListMaster newPartListMaster() throws WTException {
		PartListMaster instance = new PartListMaster();
		instance.initialize();
		return instance;
	}
}