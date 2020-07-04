import config from "./common/Config.js"
import { use } from "/lib/lit-translate.js";

export default class PhotoboxLifecycle {

    constructor(server) {
        this.server = server;
    }

    async startup() {
        await this.initializeWebsocketConnection();
        await this.initialConfigLoad()
        await this.finalizeStartup();
    }

    initializeWebsocketConnection() {
        return new Promise((resolve) => {
            this.socket = new WebSocket(this.server.serverWsUrl + "/events");
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
        if (event.type == 'ChangeEvent') {
            this.onConfigChangedEvent(event.event);
        }
    }

    async initialConfigLoad() {
        const response = await fetch(this.server.serverUrl + "/resources/config");
        config.setAll(await response.json());
    }

    async finalizeStartup() {
        // set locale from server
        await this.setLocaleFromConfig();

    }

    async setLocaleFromConfig() {
        await use(config.get("imgbooth.locale"))
    }

    onConfigChangedEvent({ key, newValue }) {
        config.set(key, newValue);
        //window.location.reload();

        if (key == 'imgbooth.locale') {
            this.setLocaleFromConfig();
        }
    }
}