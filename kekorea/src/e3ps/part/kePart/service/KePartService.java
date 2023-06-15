package e3ps.part.kePart.service;

import java.util.HashMap;
import java.util.List;

import e3ps.part.kePart.beans.KePartDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface KePartService {

	/**
	 * 그리드에서 KE부품 등록,수정,삭제 하는 함수
	 */
	public abstract void save(HashMap<String, List<KePartDTO>> dataMap) throws Exception;

	/**
	 * 그리드에서 KE부품 개정 함수
	 */
	public abstract void revise(HashMap<String, List<KePartDTO>> dataMap) throws Exception;

}
