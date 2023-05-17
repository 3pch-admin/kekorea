package e3ps;

public class Test2 {

	public static void main(String[] args) throws Exception {

		int data = 132;
		int loop = data / 45;
		int gap = data % 45;

		System.out.println(gap);
		if (gap > 0) {
			loop = loop + 1;
		}

		int start = 0;
		int end = 45;
		for (int i = 0; i < loop; i++) {

			for (int k = start; k < end; k++) {

				System.out.println("k=" + k);
			}

			if (i == loop - 2) {
				start += 45;
				end += gap;
				System.out.println(end);
			} else {
				// 어차피 안돈다?
				start += 45;
				end += 45;
				System.out.println(end);
			}

		}

		System.exit(0);
	}
}
