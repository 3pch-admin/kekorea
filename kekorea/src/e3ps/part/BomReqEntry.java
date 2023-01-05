package e3ps.part;

import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, properties = {
		@GeneratedProperty(name = "parentPartNumber", type = String.class),
		@GeneratedProperty(name = "parentPartVersion", type = String.class),
		@GeneratedProperty(name = "parentPartDescription", type = String.class),

		@GeneratedProperty(name = "addedPartNumber", type = String.class),
		@GeneratedProperty(name = "addedPartVersion", type = String.class),
		@GeneratedProperty(name = "addedPartQuantity", type = String.class),
		@GeneratedProperty(name = "addedPartUnit", type = String.class),
		@GeneratedProperty(name = "addedPartMinusFlag", type = String.class),

		@GeneratedProperty(name = "removedPartNumber", type = String.class),
		@GeneratedProperty(name = "removedPartVersion", type = String.class),
		@GeneratedProperty(name = "removedPartQuantity", type = String.class),
		@GeneratedProperty(name = "removedPartUnit", type = String.class),
		@GeneratedProperty(name = "removedPartMinusFlag", type = String.class) }, foreignKeys = {
				@GeneratedForeignKey(name = "BomReqEntryLink", foreignKeyRole = @ForeignKeyRole(name = "bomReq", type = BomReq.class, constraints = @PropertyConstraints(required = true)), myRole = @MyRole(name = "entry", cascade = true)) })
public class BomReqEntry extends _BomReqEntry {
	static final long serialVersionUID = 1;

	public static BomReqEntry newBomReqEntry() throws WTException {
		BomReqEntry instance = new BomReqEntry();
		instance.initialize();
		return instance;
	}

}
