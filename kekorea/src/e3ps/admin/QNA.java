package e3ps.admin;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.IconProperties;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = { @GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000))

		},

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "ParentChildLink",

						foreignKeyRole = @ForeignKeyRole(name = "parent", type = QNA.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "child", cardinality = Cardinality.ZERO_TO_ONE))

		},

		iconProperties = @IconProperties(standardIcon = "/jsp/images/qna.png", openIcon = "/jsp/images/qna.png"),

		tableProperties = @TableProperties(tableName = "J_QNA")

)
public class QNA extends _QNA {
	static final long serialVersionUID = 1;

	public static QNA newQNA() throws WTException {
		QNA instance = new QNA();
		instance.initialize();
		return instance;
	}
}
