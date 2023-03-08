package e3ps.bom.tbom;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.enterprise.Managed;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Managed.class, interfaces = { ContentHolder.class, ContentHolder.class,
		Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "T-BOM 이름", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "tNumber", type = String.class, javaDoc = "TBOM NUMBER", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, unique = true)),

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
