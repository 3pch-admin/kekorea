package e3ps.project;

import java.sql.Timestamp;

import com.ptc.windchill.annotations.metadata.ColumnProperties;
import com.ptc.windchill.annotations.metadata.GenAsPersistable;
import com.ptc.windchill.annotations.metadata.GeneratedProperty;
import com.ptc.windchill.annotations.metadata.PropertyConstraints;

import wt.ownership.Ownable;
import wt.ownership.Ownership;

@GenAsPersistable(

		interfaces = { Ownable.class },

		properties = {

				@GeneratedProperty(name = "description", type = String.class, javaDoc = "설명", constraints = @PropertyConstraints(upperLimit = 2000)),

				@GeneratedProperty(name = "progress", type = Integer.class, javaDoc = "진행율", initialValue = "0"),

				@GeneratedProperty(name = "duration", type = Integer.class, javaDoc = "기간", initialValue = "0"),

				@GeneratedProperty(name = "planStartDate", type = Timestamp.class, javaDoc = "계획 시작일", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "planEndDate", type = Timestamp.class, javaDoc = "계획 종료일", constraints = @PropertyConstraints(required = true)),

				@GeneratedProperty(name = "startDate", type = Timestamp.class, javaDoc = "실제 시작일"),

				@GeneratedProperty(name = "endDate", type = Timestamp.class, javaDoc = "실제 종료일"),

				@GeneratedProperty(name = "state", type = String.class, javaDoc = "상태", constraints = @PropertyConstraints(required = true)), }

)
public interface ProjectImpl extends _ProjectImpl {

}
