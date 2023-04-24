package e3ps.korea.history;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "value", type = String.class, javaDoc = "이력관리 값"),

				@GeneratedProperty(name = "dataField", type = String.class, javaDoc = "그리드 키"),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "HistoryValueLink",

						foreignKeyRole = @ForeignKeyRole(name = "history", type = History.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "value", cardinality = Cardinality.ONE)

				)

		}

)

public class HistoryValue extends _HistoryValue {

	static final long serialVersionUID = 1;

	public static HistoryValue newHistoryValue() throws WTException {
		HistoryValue instance = new HistoryValue();
		instance.initialize();
		return instance;
	}
}
