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

				@GeneratedProperty(name = "keys", type = String[].class, javaDoc = "테이블 키"),

				@GeneratedProperty(name = "headers", type = String[].class, javaDoc = "테이블 헤더"),

				@GeneratedProperty(name = "styles", type = String[].class, javaDoc = "테이블 스타일 시트"),

				@GeneratedProperty(name = "cols", type = String[].class, javaDoc = "테이블 컬럼 사이즈"),

				@GeneratedProperty(name = "psize", type = String.class, javaDoc = "개인 페이징"),

				@GeneratedProperty(name = "module", type = String.class, javaDoc = "모듈"),

				@GeneratedProperty(name = "tabIndex", type = String[].class, javaDoc = "테이블 칼럼 순서")

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "WTUserTableSetLink",

						foreignKeyRole = @ForeignKeyRole(name = "wtuser", type = WTUser.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "tableSet", cardinality = Cardinality.DEFAULT))

		}

)
public class UserTableSet extends _UserTableSet {

	static final long serialVersionUID = 1;

	public static UserTableSet newUserTableSet() throws WTException {
		UserTableSet instance = new UserTableSet();
		instance.initialize();
		return instance;
	}
}
