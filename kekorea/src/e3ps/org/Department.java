package e3ps.org;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {
				@GeneratedProperty(name = "name", type = String.class, javaDoc = "부서명", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "code", type = String.class, javaDoc = "부서 코드", constraints = @PropertyConstraints(required = true), columnProperties = @ColumnProperties(unique = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "부서 설명", constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "depth", type = Integer.class, javaDoc = "부서 레벨", initialValue = "0"),

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "부서 정렬", initialValue = "0")

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "ParentChildLink",

						foreignKeyRole = @ForeignKeyRole(name = "parent", type = Department.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "child", cardinality = Cardinality.ZERO_TO_ONE))

		})
public class Department extends _Department {

	static final long serialVersionUID = 1;

	public static Department newDepartment() throws WTException {
		Department instance = new Department();
		instance.initialize();
		return instance;
	}
}
