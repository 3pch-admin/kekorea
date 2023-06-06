package e3ps;

import java.lang.reflect.Method;

import javax.websocket.DeploymentException;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import e3ps.chat.WebSocketEndPoint;

public class Test2 {

	public static void main(String[] args) throws Exception {

		ServerContainer serverContainer = (ServerContainer) getServerContainer();
		try {
			ServerEndpointConfig endpointConfig = ServerEndpointConfig.Builder
					.create(WebSocketEndPoint.class, "/websocket-endpoint").build();
			System.out.println(endpointConfig);
			serverContainer.addEndpoint(endpointConfig);
		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	private static Object getServerContainer() {
        try {
            Class<?> serverContainerClass = Class.forName("org.eclipse.jetty.websocket.jsr356.server.ServerContainer");
            Method getServerContainerMethod = serverContainerClass.getMethod("getServerContainer");
            return (ServerContainer) getServerContainerMethod.invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
}
