class ServerConnection {
    get serverUrl() {
        if (location.host == "localhost:3000") {
            return "http://localhost:8080";
        }
        else {
            return `http://${location.host}`;
        }
    }

    get serverWsUrl() {
        if (location.host == "localhost:3000") {
            return "ws://localhost:8080";
        }
        else {
            return `ws://${location.host}`;
        }
    }
}

export default new ServerConnection();