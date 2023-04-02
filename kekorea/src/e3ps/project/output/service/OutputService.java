package e3ps.project.output.service;

import e3ps.project.output.dto.OutputDTO;
import wt.method.RemoteInterface;

@RemoteInterface
public interface OutputService {

	public abstract void create(OutputDTO dto) throws Exception;

}
