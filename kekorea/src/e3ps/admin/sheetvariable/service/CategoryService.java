package e3ps.admin.sheetvariable.service;

import java.util.Map;

import wt.method.RemoteInterface;

@RemoteInterface
public interface CategoryService {

	public abstract void create(Map<String, Object> params) throws Exception;

}
