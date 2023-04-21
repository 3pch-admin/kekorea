package e3ps.bom.tbom;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.part.kePart.KePart;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "lotNo", type = Integer.class, javaDoc = "LOT", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "code", type = String.class, javaDoc = "중간코드", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "qty", type = Integer.class, javaDoc = "수량", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "unit", type = String.class, javaDoc = "단위", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "provide", type = String.class),

				@GeneratedProperty(name = "discontinue", type = String.class),

				@GeneratedProperty(name = "sort", type = Integer.class)

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "TBOMKePartLink",

						foreignKeyRole = @ForeignKeyRole(name = "kePart", type = KePart.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "tbom", cardinality = Cardinality.ONE)), }

)

public class TBOMData extends _TBOMData {

	static final long serialVersionUID = 1;

	public static TBOMData newTBOMData() throws WTException {
		TBOMData instance = new TBOMData();
		instance.initialize();
		return instance;
	}
}
