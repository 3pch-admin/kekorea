package e3ps.org;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {
				
				@GeneratedProperty(name = "url", type = String.class, javaDoc = "즐겨 찾기 주소"),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "즐겨 찾기 이름"),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "즐겨 찾기 설명")

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "WTUserBookmarkLink",

						foreignKeyRole = @ForeignKeyRole(name = "wtuser", type = WTUser.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "bookMark", cardinality = Cardinality.DEFAULT))

		},

		tableProperties = @TableProperties(tableName = "J_BOOKMARK")

)
public class Bookmark extends _Bookmark {
	static final long serialVersionUID = 1;

	public static Bookmark newBookmark() throws WTException {
		Bookmark instance = new Bookmark();
		instance.initialize();
		return instance;
	}
}
