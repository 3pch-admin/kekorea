package e3ps.korea.cip;

import com.ptc.windchill.annotations.metadata.Cardinality;
import com.ptc.windchill.annotations.metadata.ForeignKeyRole;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedForeignKey;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.MyRole;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import e3ps.admin.commonCode.CommonCode;
import wt.content.FormatContentHolder;
import wt.fc.Item;
import wt.ownership.Ownable;
import wt.util.WTException;

@GenAsPersistable(superClass = Item.class, interfaces = { FormatContentHolder.class, Ownable.class },

		properties = {

				@GeneratedProperty(name = "item", type = String.class, javaDoc = "항목"),

				@GeneratedProperty(name = "improvements", type = String.class, javaDoc = "개선항목"),

				@GeneratedProperty(name = "improvement", type = String.class, javaDoc = "개선책"),

				@GeneratedProperty(name = "apply", type = String.class, javaDoc = "적용/미적용"),

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "비고"),

		},

		foreignKeys = {

				@GeneratedForeignKey(name = "CipMakLink",

						foreignKeyRole = @ForeignKeyRole(name = "mak", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "cip", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "CipDetailLink",

						foreignKeyRole = @ForeignKeyRole(name = "detail", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "cip", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "CipCustomerLink",

						foreignKeyRole = @ForeignKeyRole(name = "customer", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "cip", cardinality = Cardinality.ONE)),

				@GeneratedForeignKey(name = "CipInstallLink",

						foreignKeyRole = @ForeignKeyRole(name = "install", type = CommonCode.class,

								constraints = @PropertyConstraints(required = true)),

						myRole = @MyRole(name = "cip", cardinality = Cardinality.ONE))

		}

)
public class Cip extends _Cip {

	static final long serialVersionUID = 1;

	public static Cip newCip() throws WTException {
		Cip instance = new Cip();
		instance.initialize();
		return instance;
	}
}
