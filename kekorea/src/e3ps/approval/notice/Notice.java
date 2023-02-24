package e3ps.approval.notice;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.content.FormatContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { Ownable.class, FormatContentHolder.class },

		properties = {

				@GeneratedProperty(name = "name", type = String.class, javaDoc = "공지사항 제목", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "내용", constraints = @PropertyConstraints(upperLimit = 4000))

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
