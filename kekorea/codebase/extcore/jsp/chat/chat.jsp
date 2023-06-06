<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>WebSocket Example</title>
<script type="text/javascript">
	var socket;

	function initWebSocket() {
		socket = new WebSocket("ws://plmdev.kekorea.co.kr/Windchill/servlet/websocket-endpoint");

		socket.onopen = function(event) {
			console.log("WebSocket connection established.");
		};

		socket.onmessage = function(event) {
			var receivedMessage = event.data;
			console.log("Received message: " + receivedMessage);

			// 받은 메시지를 화면에 표시
			document.getElementById("message").innerHTML = receivedMessage;
		};

		socket.onclose = function(event) {
			console.log("WebSocket connection closed.");
		};
	}

	function sendMessage() {
		var message = document.getElementById("inputMessage").value;
		socket.send(message);
		console.log("Sent message: " + message);
	}
</script>
</head>
<body onload="initWebSocket()">
	<h1>WebSocket Example</h1>

	<input type="text" id="inputMessage" placeholder="Enter a message">
	<button onclick="sendMessage()">Send</button>

	<h2>Received message:</h2>
	<div id="message"></div>
</body>
</html>