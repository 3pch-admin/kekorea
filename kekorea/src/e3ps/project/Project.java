package e3ps.project;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import e3ps.project.template.Template;
import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, ProjectImpl.class },

		properties = {

				@GeneratedProperty(name = "kekNumber", type = String.class, javaDoc = "KEK 작번", columnProperties = @ColumnProperties(index = true, unique = true), constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "pDate", type = Timestamp.class, javaDoc = "작번 발행일", columnProperties = @ColumnProperties(index = true), constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "keNumber", type = String.class, javaDoc = "KE 작번", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "userId", type = String.class, javaDoc = "USER ID"),

				@GeneratedProperty(name = "model", type = String.class, javaDoc = "기종"),

				@GeneratedProperty(name = "customDate", type = Timestamp.class, javaDoc = "요구납기일"),

				@GeneratedProperty(name = "kekState", type = String.class, javaDoc = "작번 상태"),

				@GeneratedProperty(name = "machinePrice", type = Double.class, javaDoc = "설계 금액"),

				@GeneratedProperty(name = "elecPrice", type = Double.class, javaDoc = "전기 금액"),

				@GeneratedProperty(name = "outputMachinePrice", type = Double.class, javaDoc = "설계 금액-수배표"),

				@GeneratedProperty(name = "outputElecPrice", type = Double.class, javaDoc = "전기 금액-수배표"),

				@GeneratedProperty(name = "gate1", type = Integer.class, javaDoc = "GATE1"),

				@GeneratedProperty(name = "gate2", type = Integer.class, javaDoc = "GATE2"),

				@GeneratedProperty(name = "gate3", type = Integer.class, javaDoc = "GATE3"),

				@GeneratedProperty(name = "gate4", type = Integer.class, javaDoc = "GATE4"),

				@GeneratedProperty(name = "gate5", type = Integer.class, javaDoc = "GATE5"),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "ProjectCustomerLink",

						foreignKeyRole = @ForeignKeyRole(name = "customer", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "ProjectInstallLink",

						foreignKeyRole = @ForeignKeyRole(name = "install", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "ProjectTypeLink",

						foreignKeyRole = @ForeignKeyRole(name = "projectType", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "ProjectMakLink",

						foreignKeyRole = @ForeignKeyRole(name = "mak", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "ProjectDetailLink",

						foreignKeyRole = @ForeignKeyRole(name = "detail", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "ProjectTemplateLink",

						foreignKeyRole = @ForeignKeyRole(name = "template", type = Template.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "project", cardinality = Cardinality.ONE))

		})

public class Project extends _Project {

	static final long serialVersionUID = 1;

	public static Project newProject() throws WTException {
		Project instance = new Project();
		instance.initialize();
		return instance;
	}
}
