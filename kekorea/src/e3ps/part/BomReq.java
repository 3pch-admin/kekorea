package e3ps.part;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;

import wt.doc.WTDocument;
import wt.util.WTException;

@GenAsPersistable(superClass = WTDocument.class, properties = { @GeneratedProperty(name = "area", type = String.class),
		@GeneratedProperty(name = "reqType", type = String.class),
		@GeneratedProperty(name = "topNumber", type = String.class),
		@GeneratedProperty(name = "topVersion", type = String.class) })
public class BomReq extends _BomReq {
	static final long serialVersionUID = 1;

	public static BomReq newBomReq() throws WTException {

		BomReq instance = new BomReq();
		instance.initialize();
		return instance;
	}
}
