package e3ps.project;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "특이사항 제목"),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

		}

)
public class Issue extends _Issue {

	static final long serialVersionUID = 1;

	public static Issue newIssue() throws WTException {
		Issue instance = new Issue();
		instance.initialize();
		return instance;
	}
}
