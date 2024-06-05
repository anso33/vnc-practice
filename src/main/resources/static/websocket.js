document.getElementById('connectButton').addEventListener('click', function () {
    for (let i = 0; i < 30; i++) {
        let ws = new WebSocket('ws://localhost:8080/ws');
        ws.onopen = function () {
            console.log(`WebSocket ${i} connected`);
        };
        ws.onerror = function (error) {
            console.log(`WebSocket ${i} error: `, error);
        };
        ws.onclose = function () {
            console.log(`WebSocket ${i} closed`);
        };
    }
});

document.getElementById('singleConnectButton').addEventListener('click', function () {
    let ws = new WebSocket('ws://localhost:8080/ws');
    ws.onopen = function () {
        console.log('Single WebSocket connected');
    };
    ws.onerror = function (error) {
        console.log('Single WebSocket error: ', error);
    };
    ws.onclose = function () {
        console.log('Single WebSocket closed');
    };
});
