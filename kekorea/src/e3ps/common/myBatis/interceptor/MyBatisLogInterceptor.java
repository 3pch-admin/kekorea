package e3ps.common.myBatis.interceptor;

import java.sql.Statement;
import java.util.Properties;

import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.log4j.Logger;

import wt.log4j.LogR;

@Intercepts({ @Signature(type = StatementHandler.class, method = "update", args = { Statement.class }),
		@Signature(type = StatementHandler.class, method = "query", args = { Statement.class, ResultHandler.class }) })
public class MyBatisLogInterceptor implements Interceptor {

	private static final String CLASSNAME = MyBatisLogInterceptor.class.getName();
	private static final Logger logger = LogR.getLogger(CLASSNAME);

	@Override
	public Object intercept(Invocation arg0) throws Throwable {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object plugin(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProperties(Properties arg0) {
		// TODO Auto-generated method stub

	}

}
