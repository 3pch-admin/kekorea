package e3ps.bom.tbom;

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
				
				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", initialValue = "1", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "T-BOM 이름", constraints = @PropertyConstraints(required = true)),
				
				@GeneratedProperty(name = "number", type = String.class, constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(columnName = "TBOMNumber", unique = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000))

		}

)

public class TBOMMaster extends _TBOMMaster {
	static final long serialVersionUID = 1;

	public static TBOMMaster newTBOMMaster() throws WTException {
		TBOMMaster instance = new TBOMMaster();
		instance.initialize();
		return instance;
	}
}
