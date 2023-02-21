package e3ps;

import java.sql.Timestamp;
import java.util.Date;

public class Test {

	public static void main(String[] args) throws Exception {

		Timestamp today = new Timestamp(new Date().getTime());

		System.out.println(today.toString().substring(0, 4));

		System.exit(0);
	}
}
