package e3ps.common.content;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;
import com.ptc.windchill.annotations.metadata.TableProperties;

import wt.content.ContentHolder;
import wt.fc.Item;
import wt.fc.Persistable;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { ContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "number", type = String.class, columnProperties = @ColumnProperties(columnName = "CONTENTSNUMBER")),

				@GeneratedProperty(name = "name", type = String.class),

				@GeneratedProperty(name = "version", type = String.class),

				@GeneratedProperty(name = "description", type = String.class, constraints = @PropertyConstraints(upperLimit = 4000)),

				@GeneratedProperty(name = "fileName", type = String.class),

		},

		tableProperties = @TableProperties(tableName = "J_CONTENTS"),

		foreignKeys = {
				// front target object, before source user
				@GeneratedForeignKey(name = "ContentsPerLink",

						foreignKeyRole = @ForeignKeyRole(name = "persistables", type = Persistable.class,

								constraints = @PropertyConstraints(required = false)),

						myRole = @MyRole(name = "contents", cardinality = Cardinality.ONE))

		})

public class Contents extends _Contents {
	static final long serialVersionUID = 1;

	public static Contents newContents() throws WTException {
		Contents instance = new Contents();
		instance.initialize();
		return instance;
	}
}
