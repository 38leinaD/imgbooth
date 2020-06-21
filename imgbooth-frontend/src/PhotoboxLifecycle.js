import Config from "./common/Config.js"

export default class PhotoboxLifecycle {

    constructor(server) {
        this.server = server;
    }

    async run() {
        await this.reportState("Startup");

        await this.initializeWebsocketConnection();

        await this.initialConfigLoad()
        await this.initializeWebcamConfig();
        await this.uploadConfig();
        await this.reportState("Online");
    }

    initializeWebsocketConnection() {
        return new Promise((resolve) => {
            this.socket = new WebSocket(this.server.serverWsUrl + "/events/123");
            this.socket.onopen = () => {
                console.log("Websocket connected.");
                resolve();
            }

            this.socket.onerror = (e) => {
                console.error("Websocket error: ", e);
            }

            this.socket.onclose = (e) => {
                console.error("Websocket closed: ", e);
            }

            this.socket.onmessage = (m) => {
                this.onWebsocketMessage(m);
            }
        });
    }

    onWebsocketMessage(message) {
        console.log("Websocket received message: ", message)

        const event = JSON.parse(message.data);
        if (event.type == 'ConfigChangedEvent') {
            console.log("CONFIG CHANGED!!!!")
            this.onConfigChangedEvent();
        }
    }

    async initialConfigLoad() {
        const response = await fetch(this.server.serverUrl + "/resources/config");
        Config.setAll(await response.json());
    }

    async reportState(state) {
        const response = await fetch(this.server.serverUrl + "/resources/client/state", {
            method: 'POST',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(state)
        });
        if (response.status != 204) {
            console.error("reportState returned status: ", response.status)
        }
    }

    async initializeWebcamConfig() {

        const deviceInfos = await navigator.mediaDevices.enumerateDevices();
        console.log("DeviceInfos ", deviceInfos);
        const videoDevices = deviceInfos.filter(deviceInfo => deviceInfo.kind == "videoinput")
        if (videoDevices.length == 0) {
            alert("No VideoInput device found.")
        }
        else {
            Config.set('webcam.availableDevices', videoDevices.map(vd => {
                return {
                    deviceId: vd.deviceId,
                    label: vd.label
                }
            }));

            let selectedDeviceId = videoDevices[0].deviceId;
            const previousSelectedDeviceId = Config.get("webcam.deviceId");

            if (previousSelectedDeviceId) {
                const matches = videoDevices.filter(vd => vd.deviceId == selectedDeviceId);
                if (matches.length > 0) {
                    selectedDeviceId = previousSelectedDeviceId;
                }
            }

            Config.set('webcam.deviceId', selectedDeviceId);
        }
    }

    async uploadConfig() {
        const response = await fetch(this.server.serverUrl + "/resources/config", {
            method: 'PATCH',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(Config.getAll())
        });
        console.log("Pushed config and got " + response.status)
    }

    onConfigChangedEvent() {
        console.log("XXXXXXXXXX")
        window.location.reload();
    }
}