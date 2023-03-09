package e3ps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.lowagie.text.List;

public class Test {

	public static void main(String[] args) throws Exception {
		ArrayList<Integer> list1 = new ArrayList<>();
		ArrayList<Integer> list2 = new ArrayList<>();

		// list1 초기화
		list1.add(1);
		list1.add(2);
		list1.add(3);
		list1.add(4);

		// list2 초기화
		list2.add(6);
		list2.add(5);

		// 크기가 작은 ArrayList를 찾아 null 값을 추가
		int diff = Math.abs(list1.size() - list2.size());
		if (list1.size() < list2.size()) {
			for (int i = 0; i < diff; i++) {
				list1.add(null);
			}
		} else if (list1.size() > list2.size()) {
			for (int i = 0; i < diff; i++) {
				list2.add(null);
			}
		}
		System.out.println(list1);
		System.out.println(list2);

		ArrayList<Integer> list3 = new ArrayList<>(list1.size());
		ArrayList<Integer> list4 = new ArrayList<>(list1.size());

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				int v1 = list1.get(i);
				int v2 = list2.get(j);

				if (v1 == v2) {
					list3.add(v1);
					list4.add(v2);
					list2.remove(j);
					break;
				} else if (v1 != v2) {
					list3.add(v1);
					list4.add(null);
					break;
				}
			}
		}
		
		list4.addAll(list2);
		
		int diff2 = Math.abs(list3.size() - list4.size());
		if (list3.size() < list4.size()) {
			for (int i = 0; i < diff2; i++) {
				list3.add(null);
			}
		} else if (list1.size() > list4.size()) {
			for (int i = 0; i < diff2; i++) {
				list4.add(null);
			}
		}
		
		System.out.println(list3);
		System.out.println(list4);
	}
}
