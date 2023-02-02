package e3ps;

import e3ps.epm.jDrawing.JDrawing;
import e3ps.epm.jDrawing.JDrawingMaster;
import wt.fc.PersistenceHelper;

public class Test {

	public static void main(String[] args) throws Exception {

		JDrawingMaster m = JDrawingMaster.newJDrawingMaster();
		m.setNumber("123");
		m.setName("21333");
		m = (JDrawingMaster) PersistenceHelper.manager.save(m);

		JDrawing d = JDrawing.newJDrawing();
		d.setLot("AsASDSA");
		d.setMaster(m);
		PersistenceHelper.manager.save(d);
		System.out.println("저장..");

	}

}
