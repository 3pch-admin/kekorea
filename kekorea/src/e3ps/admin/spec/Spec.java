package e3ps.admin.spec;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "사양관리 헤더명", columnProperties = @ColumnProperties(unique = true), constraints = @PropertyConstraints(upperLimit = 2000, required = true)),

				@GeneratedProperty(name = "columnKey", type = String.class, javaDoc = "사양관리 칼럼 키 값", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "사양관리 정렬 순서", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "latest", type = Boolean.class, javaDoc = "최신버전여부", initialValue = "true", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "version", type = Integer.class, javaDoc = "버전", initialValue = "1", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "enable", type = Boolean.class, javaDoc = "사용여부", initialValue = "true")

		}

)
public class Spec extends _Spec {

	static final long serialVersionUID = 1;

	public static Spec newSpec() throws WTException {
		Spec instance = new Spec();
		instance.initialize();
		return instance;
	}
}
