package e3ps;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) throws Exception {

		ArrayList<String> list = new ArrayList<>();
		list.add("1");
		list.add("2");

		String[] s = list.toArray(new String[list.size()]);
		for (String ss : s) {
			System.out.print(ss);
		}

		System.exit(0);
	}
}