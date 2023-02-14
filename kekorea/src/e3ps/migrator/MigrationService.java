package e3ps.migrator;

import java.util.HashMap;

import wt.method.RemoteInterface;

@RemoteInterface
public interface MigrationService {

	public abstract void projectToMak(HashMap<String, Object> params) throws Exception;

	public abstract void projectToCustomer() throws Exception;

	public abstract void projectToInstall() throws Exception;

	public abstract void projectToProjectType() throws Exception;
}
