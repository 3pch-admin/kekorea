package e3ps;

public class Test2 {

	public static void main(String[] args) throws Exception {

		int data = 132;
		int loop = data / 45;
		int gap = data % 45;
		
		System.out.println(loop);
		System.out.println(gap);

		int start = 0;
		int end = 45;
		for (int i = 0; i < loop; i++) {

			for (int k = start; k < end; k++) {

//				System.out.println("k=" + k);
			}

			start += 45;
			end += 45;

		}

		System.exit(0);
	}
}
