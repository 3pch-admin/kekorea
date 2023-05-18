package e3ps.system;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { Ownable.class })
public class ErrorLog extends _ErrorLog {

	public static final long serialVersionUID = 1;

	public static ErrorLog newErrorLog() throws WTException {
		ErrorLog instance = new ErrorLog();
		instance.initialize();
		return instance;
	}
}
