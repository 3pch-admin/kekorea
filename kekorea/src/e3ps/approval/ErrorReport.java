package e3ps.approval;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { Ownable.class, ContentHolder.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "completeTime", type = Timestamp.class),

				@GeneratedProperty(name = "comments", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "complete", type = Boolean.class)

		}

)

public class ErrorReport extends _ErrorReport {

	static final long serialVersionUID = 1;

	public static ErrorReport newErrorReport() throws WTException {
		ErrorReport instance = new ErrorReport();
		instance.initialize();
		return instance;
	}

}
