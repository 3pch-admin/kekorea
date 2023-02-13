package e3ps;

import java.math.BigDecimal;

import javax.media.j3d.Alpha;

import e3ps.project.task.Task;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.query.ClassAttribute;
import wt.query.ColumnExpression;
import wt.query.ConstantExpression;
import wt.query.QuerySpec;
import wt.query.SQLFunction;
import wt.query.SearchCondition;

public class Test {

	public static void main(String[] args) throws Exception {

		String a = "SRN/AIN/SRO";
		String b = "SRN/AlN/SRO";
		
		System.out.println(a.equals(b));
		
	}
}