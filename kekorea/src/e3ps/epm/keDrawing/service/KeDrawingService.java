package e3ps.epm.keDrawing.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface KeDrawingService {

	public abstract void create(Map<String, Object> params) throws Exception;

	public abstract void revise(Map<String, Object> params) throws Exception;

}
