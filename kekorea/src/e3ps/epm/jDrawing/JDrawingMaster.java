package e3ps.epm.jDrawing;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class },
		// 최초등록자
		properties = {
				@GeneratedProperty(name = "number", type = String.class, javaDoc = "도면번호", columnProperties = @ColumnProperties(columnName = "drawingNumber")),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "도면번호") }

)
public class JDrawingMaster extends _JDrawingMaster {

	static final long serialVersionUID = 1;

	public static JDrawingMaster newJDrawingMaster() throws WTException {
		JDrawingMaster instance = new JDrawingMaster();
		instance.initialize();
		return instance;
	}
}
