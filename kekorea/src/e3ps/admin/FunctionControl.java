package e3ps.admin;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "rightClick", type = boolean.class, javaDoc = "일반 목록 마우스 오른쪽 메뉴 활성화", initialValue = "false"),

				@GeneratedProperty(name = "contextMenu", type = boolean.class, javaDoc = "헤더 마우스 오른쪽 메뉴 활성화", initialValue = "false"),

				@GeneratedProperty(name = "excelExport", type = boolean.class, javaDoc = "공통 엑셀 출력", initialValue = "false"),

				@GeneratedProperty(name = "columnHide", type = boolean.class, javaDoc = "컬럼 설정", initialValue = "false"),

				@GeneratedProperty(name = "headerDnd", type = boolean.class, javaDoc = "헤더 이동", initialValue = "false"),

				@GeneratedProperty(name = "thumnailView", type = boolean.class, javaDoc = "썸네일 리스트", initialValue = "false"),

		},

		tableProperties = @TableProperties(tableName = "J_FUNCTIONCONTROL")

)
public class FunctionControl extends _FunctionControl {

	static final long serialVersionUID = 1;

	public static FunctionControl newFunctionControl() throws WTException {
		FunctionControl instance = new FunctionControl();
		instance.initialize();
		return instance;
	}
}
