package e3ps.approval;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.StringCase;

import e3ps.org.Department;
import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "공지사항 제목", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "내용",

						// columnProperties = @ColumnProperties(columnType = ColumnType.INLINE_BLOB),

						constraints = @PropertyConstraints(upperLimit = 4000, stringCase = StringCase.UPPER_CASE))

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "NoticeDepartmentLink",

						foreignKeyRole = @ForeignKeyRole(name = "department", type = Department.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "notice", cardinality = Cardinality.ONE))

		}

)

public class Notice extends _Notice {

	static final long serialVersionUID = 1;

	public static Notice newNotice() throws WTException {
		Notice instance = new Notice();
		instance.initialize();
		return instance;
	}
}
