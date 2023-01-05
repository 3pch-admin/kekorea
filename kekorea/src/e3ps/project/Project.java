package e3ps.project;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.ContentHolder;
import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class, interfaces = { ContentHolder.class, ProjectImpl.class },

		iconProperties = @IconProperties(standardIcon = "/jsp/images/project.gif", openIcon = "/jsp/images/projec.gif"),

		properties = {

				@GeneratedProperty(name = "kekNumber", type = String.class, javaDoc = "KEK 작번", columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "pDate", type = Timestamp.class, javaDoc = "작번 발행일", columnProperties = @ColumnProperties(index = true)),

				@GeneratedProperty(name = "keNumber", type = String.class, javaDoc = "KE 작번"),

				@GeneratedProperty(name = "userId", type = String.class, javaDoc = "USER ID"),

				@GeneratedProperty(name = "mak", type = String.class, javaDoc = "막종"),

				@GeneratedProperty(name = "model", type = String.class, javaDoc = "기종"),

				@GeneratedProperty(name = "customer", type = String.class, javaDoc = "거래처"),

				@GeneratedProperty(name = "ins_location", type = String.class, javaDoc = "설치 장소"),

				@GeneratedProperty(name = "pType", type = String.class, javaDoc = "작번 유형"),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "작업 내용", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "customDate", type = Timestamp.class, javaDoc = "요구납기일"),

				@GeneratedProperty(name = "systemInfo", type = String.class, javaDoc = "System Info"),

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
				// front target object, before source user
				@GeneratedForeignKey(name = "TemplateProjectLink",

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
