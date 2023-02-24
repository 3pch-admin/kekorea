package e3ps.org;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "url", type = String.class, javaDoc = "즐겨 찾기 주소", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "즐겨 찾기 이름", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "즐겨 찾기 설명", constraints = @PropertyConstraints(upperLimit = 2000))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "BookmarkWTUserLink",

						foreignKeyRole = @ForeignKeyRole(name = "wtUser", type = WTUser.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "bookMark", cardinality = Cardinality.DEFAULT))

		}
)
public class Bookmark extends _Bookmark {
	static final long serialVersionUID = 1;

	public static Bookmark newBookmark() throws WTException {
		Bookmark instance = new Bookmark();
		instance.initialize();
		return instance;
	}
}
