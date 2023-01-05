package e3ps.org;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class },

		properties = {
				@GeneratedProperty(name = "name", type = String.class, javaDoc = "이름", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "id", type = String.class, javaDoc = "아이디", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(unique = true)),

				@GeneratedProperty(name = "email", type = String.class, javaDoc = "이메일"),

				@GeneratedProperty(name = "password", type = String.class, javaDoc = "비밀번호"),

				@GeneratedProperty(name = "duty", type = String.class, javaDoc = "직급"),

				@GeneratedProperty(name = "rank", type = String.class, javaDoc = "직위 및 직책"),

				@GeneratedProperty(name = "phone", type = String.class, javaDoc = "전화번호"),

				@GeneratedProperty(name = "mobile", type = String.class, javaDoc = "핸드폰 번호"),

				@GeneratedProperty(name = "fax", type = String.class, javaDoc = "팩스 번호"),

				@GeneratedProperty(name = "css", type = String.class, javaDoc = "CSS 파일"),

				@GeneratedProperty(name = "resign", type = Boolean.class, javaDoc = "퇴사 처리", initialValue = "false"),

				@GeneratedProperty(name = "passwordUpdateTime", type = Timestamp.class)

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "WTUserPeopleLink",

						foreignKeyRole = @ForeignKeyRole(name = "user", type = WTUser.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "people", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "DepartmentPeopleLink",

						foreignKeyRole = @ForeignKeyRole(name = "department", type = Department.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "people", cardinality = Cardinality.ONE)) }

)
public class People extends _People {

	static final long serialVersionUID = 1;

	public static People newPeople() throws WTException {
		People instance = new People();
		instance.initialize();
		return instance;
	}
}
