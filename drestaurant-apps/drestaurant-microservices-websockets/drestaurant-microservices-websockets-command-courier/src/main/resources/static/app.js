var stompClient = null;

function setConnected(connected) {
    document.getElementById('connect').disabled = connected;
    document.getElementById('disconnect').disabled = !connected;
    document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
    document.getElementById('response').innerHTML = '';
}

function connect() {
    stompClient = Stomp.client('ws://localhost:8081/courier/websocket');
    stompClient.debug = null;
    stompClient.connect({}, function(frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
    });
}

function disconnect() {
    if (stompClient != null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendCommand() {
    var firstName = document.getElementById('firstName').value;
    var lastName = document.getElementById('lastName').value;
    stompClient.send("/app/couriers/createcommand", {}, JSON.stringify({ 'firstName': firstName, 'lastName': lastName }));
}

