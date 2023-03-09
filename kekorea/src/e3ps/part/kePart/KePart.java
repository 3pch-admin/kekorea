package e3ps.part.kePart;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "state", type = String.class, constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "version", type = Integer.class, constraints = @PropertyConstraints(required = true), initialValue = "1"),

				@GeneratedProperty(name = "latest", type = Boolean.class, constraints = @PropertyConstraints(required = true), initialValue = "true"),

				@GeneratedProperty(name = "note", type = String.class, constraints = @PropertyConstraints(upperLimit = 2000))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "KePartMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = KePartMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "kePart", cardinality = Cardinality.ONE))

		}

)

public class KePart extends _KePart {

	static final long serialVersionUID = 1;

	public static KePart newKePart() throws WTException {
		KePart instance = new KePart();
		instance.initialize();
		return instance;
	}
}
