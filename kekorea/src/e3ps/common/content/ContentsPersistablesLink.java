package e3ps.common.content;

import com.ptc.windchill.annotations.metadata.GenAsBinaryLink;
import com.ptc.windchill.annotations.metadata.GeneratedRole;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.ObjectToObjectLink;
import wt.fc.Persistable;
import wt.util.WTException;

@GenAsBinaryLink(superClass = ObjectToObjectLink.class,

		roleA = @GeneratedRole(name = "contents", type = Contents.class),

		roleB = @GeneratedRole(name = "persistables", type = Persistable.class),

		tableProperties = @TableProperties(tableName = "J_CONTENTSPERSISTABLESLINK"))
public class ContentsPersistablesLink extends _ContentsPersistablesLink {
	static final long serialVersionUID = 1;

	public static ContentsPersistablesLink newContentsPersistablesLink(Contents contents, Persistable persistables)
			throws WTException {
		ContentsPersistablesLink instance = new ContentsPersistablesLink();
		instance.initialize(contents, persistables);
		return instance;
	}

}
