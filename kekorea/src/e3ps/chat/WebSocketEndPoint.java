package e3ps.chat;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/websocket-endpoint")
public class WebSocketEndPoint extends Endpoint {

	@Override
	public void onOpen(Session session, EndpointConfig config) {
		session.addMessageHandler(new MessageHandler.Whole<String>() {
			@Override
			public void onMessage(String message) {
				// 클라이언트로부터 수신된 메시지 처리
				// 여기서는 간단히 수신된 메시지를 대문자로 변환하여 클라이언트로 다시 보냅니다.
				session.getAsyncRemote().sendText(message.toUpperCase());
			}
		});
	}
}