package e3ps.bom.partlist;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.fc.WTObject;
import wt.util.WTException;

@GenAsPersistable(superClass = WTObject.class,

		properties = {

				@GeneratedProperty(name = "lotNo", type = Integer.class, javaDoc = "LOT NO", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "unitName", type = String.class, javaDoc = "UNIT NAME"),

				@GeneratedProperty(name = "partNo", type = String.class, javaDoc = "부품번호"),

				@GeneratedProperty(name = "partName", type = String.class, javaDoc = "부품이름"),

				@GeneratedProperty(name = "standard", type = String.class, javaDoc = "규격"),

				@GeneratedProperty(name = "maker", type = String.class, javaDoc = "Maker"),

				@GeneratedProperty(name = "customer", type = String.class, javaDoc = "거래처"),

				@GeneratedProperty(name = "quantity", type = Integer.class, javaDoc = "수량"),

				@GeneratedProperty(name = "unit", type = String.class, javaDoc = "단위"),

				@GeneratedProperty(name = "price", type = Integer.class, javaDoc = "단가"),

				@GeneratedProperty(name = "currency", type = String.class, javaDoc = "화폐"),
				
				@GeneratedProperty(name = "won", type = Integer.class, javaDoc = "원화금액"),

				@GeneratedProperty(name = "partListDate", type = Timestamp.class, javaDoc = "수배일자"),

				@GeneratedProperty(name = "exchangeRate", type = Integer.class, javaDoc = "환율"),

				@GeneratedProperty(name = "referDrawing", type = String.class, javaDoc = "참고도면"),

				@GeneratedProperty(name = "classification", type = String.class, javaDoc = "조달구분"),

				@GeneratedProperty(name = "note", type = String.class, javaDoc = "비고"),

				@GeneratedProperty(name = "sort", type = Integer.class, javaDoc = "정렬")

		}

)

public class PartListData extends _PartListData {

	static final long serialVersionUID = 1;

	public static PartListData newPartListData() throws WTException {
		PartListData instance = new PartListData();
		instance.initialize();
		return instance;
	}
}
