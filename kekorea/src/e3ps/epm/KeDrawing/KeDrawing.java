package e3ps.epm.KeDrawing;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.FormatContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { Ownable.class, FormatContentHolder.class },
		// 수정자...
		properties = {

				@GeneratedProperty(name = "lot", type = String.class, javaDoc = "LOT"),

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "KeDrawingMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = KeDrawingMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "keDrawing", cardinality = Cardinality.ONE)

				) }

)
public class KeDrawing extends _KeDrawing {

	static final long serialVersionUID = 1;

	public static KeDrawing newKeDrawing() throws WTException {
		KeDrawing instance = new KeDrawing();
		instance.initialize();
		return instance;
	}

}
