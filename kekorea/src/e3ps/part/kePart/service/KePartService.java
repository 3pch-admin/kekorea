package e3ps.part.kePart.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface KePartService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
