package e3ps;

public class Test2 {

	public static void main(String[] args) throws Exception {
		String qty = "10";

		
		String s = "10.98220";
		
		double ss = Double.parseDouble(qty) * Double.parseDouble("200") * Double.parseDouble(s);
		System.out.println(ss);
		
		
		double bb = Math.floor(ss);
		System.out.println(bb);
		System.exit(0);
	}
}