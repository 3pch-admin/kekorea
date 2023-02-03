package e3ps.epm.jDrawing;

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

@GenAsPersistable(superClass = Item.class, interfaces = { Ownable.class, ContentHolder.class },
		// 수정자...
		properties = {

				@GeneratedProperty(name = "lot", type = String.class, javaDoc = "LOT"),

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "JDrawingMasterLink",

						foreignKeyRole = @ForeignKeyRole(name = "master", type = JDrawingMaster.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "jDrawing", cardinality = Cardinality.ONE)

				) }

)
public class JDrawing extends _JDrawing {

	static final long serialVersionUID = 1;

	public static JDrawing newJDrawing() throws WTException {
		JDrawing instance = new JDrawing();
		instance.initialize();
		return instance;
	}

}
