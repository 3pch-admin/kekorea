package e3ps.epm.workOrder.service;

import e3ps.epm.workOrder.dto.WorkOrderDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface WorkOrderService {

	/**
	 * 도면 일람표 생성
	 */
	public abstract void create(WorkOrderDTO dto) throws Exception;

	/**
	 * 도면 일람표 수정
	 */
	public abstract void modify(WorkOrderDTO dto) throws Exception;

	/**
	 * 도면 일람표 삭제
	 */
	public abstract void delete(String oid) throws Exception;

	/**
	 * 도면일람표 개정
	 */
	public abstract void revise(WorkOrderDTO dto) throws Exception;

}
