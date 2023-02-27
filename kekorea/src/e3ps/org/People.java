package e3ps.org;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.FormatContentHolder;
import wt.fc.Item;
import wt.org.WTUser;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { FormatContentHolder.class },

		properties = {
				@GeneratedProperty(name = "name", type = String.class, javaDoc = "이름", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "id", type = String.class, javaDoc = "아이디", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(index = true, unique = true)),

				@GeneratedProperty(name = "email", type = String.class, javaDoc = "이메일"),

				@GeneratedProperty(name = "duty", type = String.class, javaDoc = "직급"),

				@GeneratedProperty(name = "resign", type = Boolean.class, javaDoc = "퇴사 처리", initialValue = "false"), },

		foreignKeys = {

				@GeneratedForeignKey(name = "PeopleWTUserLink",

						foreignKeyRole = @ForeignKeyRole(name = "wtUser", type = WTUser.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "people", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "PeopleDepartmentLink",

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
