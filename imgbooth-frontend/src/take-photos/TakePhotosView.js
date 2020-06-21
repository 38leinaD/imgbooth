import { html, LitElement, css } from '../lib/lit-element.js';
import { until } from '../lib/lit-html/directives/until.js';
import { translate } from "/lib/lit-translate.js";

import router from '../common/Router.js';
import serverConnection from '../common/ServerConnection.js'

import config from '../common/Config.js'

import './CountdownView.js'
import '../common/PBProgressBar.js'
import PBLoadScreen from '../common/PBLoadScreen.js'
import PhotoService from '../common/PhotoService.js';

// 			<img class="live-preview" src="http://localhost:8080/resources/camera/live"></img>

const Mode = {
    Single: "Single",
    Collage: "Collage"
}

const States = {
    Idle: "Idle",
    Countdown: "Countdown",
    TakePictures: "TakePictures",
    Upload: "Upload"
}
// https://www.html5rocks.com/en/tutorials/getusermedia/intro/
export default class TakePhotosView extends LitElement {

    static get properties() {
        return {
            state: { type: String }
        };
    }

    constructor() {
        super();
        this.reset();
    }

    reset() {
        this.state = States.Idle;
        this.mode = Mode.Collage;
        this.photoService = new PhotoService();
        this.photoCount = 0;
        this.sessionId = this.uuidv4();
        this.serverRefs = [];
    }

    connectedCallback() {
        //console.log("CONNECTEDCALLBACK FOR ", TakePhotosView)
        super.connectedCallback();

        addEventListener('img-photo-upload-finished', e => {
            console.log("EVENT RECEIVED " + this.photoCount)
            PBLoadScreen.get().progress = 1.0 / ((this.mode == Mode.Collage ? 4 : 1) + 1) * this.photoCount;
        });
    }

    disconnectedCallback() {
        //console.log("DISCONNECTEDCALLBACK FOR ", TakePhotosView)
        document.removeEventListener('keydown', this.keyboardListener);
    }

    firstUpdated(changedProperties) {
        this.keyboardListener = (e) => {
            if (this.state != States.Idle) return;
            if (e.key == "1") {
                this.takeSinglePhoto();
                e.preventDefault();
            }
            else if (e.key == "2") {
                this.takeCollagePhoto();
                e.preventDefault();
            }
        };
        document.addEventListener('keydown', this.keyboardListener);

        this.initWebcam();
    }

    takeSinglePhoto() {
        this.mode = Mode.Single;
        this.transition(States.Countdown);
    }

    takeCollagePhoto() {
        this.mode = Mode.Collage;
        this.transition(States.Countdown);
    }

    hasPreviewCamera() {
        return config["previewwebcam.enabled"] == "true";
    }

    async initWebcam() {
        if (!this.hasPreviewCamera()) return;
        const video = this.renderRoot.querySelector('video');
        const deviceInfos = await navigator.mediaDevices.enumerateDevices();
        console.log("DeviceInfos ", deviceInfos);

        let deviceId = null;
        if (config["webcam.deviceid"] != "") {
            deviceId = config["webcam.deviceid"];
        }
        else {
            const videoDevices = deviceInfos.filter(deviceInfo => deviceInfo.kind == "videoinput")
            if (videoDevices.length == 0) {
                alert("No VideoInput device found.")
            }
            else {
                console.info("Selecting videoInput", videoDevices[0])
                deviceId = videoDevices[0].deviceId;
            }
        }
        console.log("Using deviceId", deviceId)
        const videoStream = await navigator.mediaDevices.getUserMedia({
            video: { deviceId: { exact: deviceId }, width: { ideal: 4096 }, height: { ideal: 2160 } }
        });


        video.srcObject = videoStream;

        const dimensions = await this.getVideoDimensions(video);
        console.log("Video dimensions: ", dimensions)

        //clip-path: inset(20px 0 300px 0);
        // clip to target-aspect-ratio
        const aspectRatio = dimensions.width / dimensions.height;
        const targetAspectRatio = config['targetAspectRatio'];

        let croppedHeight = 0.0;
        let croppedWidth = 0.0;
        if (aspectRatio > targetAspectRatio) {
            croppedWidth = dimensions.width - targetAspectRatio * dimensions.height;
        }
        else {
            croppedHeight = dimensions.height - dimensions.width / targetAspectRatio;
        }


        console.log("Clipping to targetAspectRatio. Removing croppedWidth=" + croppedWidth + ", croppedHeight=" + croppedHeight + " at top and bottom.")

        video.style['clip-path'] = `inset(${croppedHeight / 2.0}px ${croppedWidth / 2.0}px ${croppedHeight / 2.0}px ${croppedWidth / 2.0}px)`;
    }

    getVideoDimensions(video) {
        return new Promise(function (resolve, reject) {
            var getVideoSize = function () {
                const width = video.videoWidth;
                const height = video.videoHeight;
                video.removeEventListener('playing', getVideoSize, false);
                resolve({
                    width,
                    height
                })
            };

            video.addEventListener('playing', getVideoSize, false);
        });
    }

    render() {
        return this.renderCamera();
    }

    renderCamera() {
        return html`
            <link rel="stylesheet" type="text/css" media="screen" href="./style.css" />
        
            <div class="live-preview-container">
                <img class="live-preview" src="${this.livePreviewUrl()}" @error="${e => this.onLivePreviewError(e)}"></img>
            </div>
            ${this.state == States.Idle && !this.hasPreviewCamera() ? html`
                <div class="idle-container">
                    <div class="idle-message-container">
                        <img class="icon" src="../assets/icons/camera-retro.svg"></img>
                        <div class="idle-message">${translate('take-photos.choose')}</div>
                    </div>
                </div>
            ` : html``}

            <div class="video-crop"></div>

            <audio src="../assets/audio/camera-shutter.oga" preload="auto"></audio>
            <img-progress-bar></img-progress-bar>

            ${this.state == States.Idle ? this.renderSelection() : html``}
            
            <div class="camera-flash"></div>
            ${this.state == States.Countdown ? html`<img-countdown></img-countdown>` : html``}
        `;
    }

    livePreviewUrl() {
        //return '../assets/preview.jpg';
        return 'http://localhost:8080/resources/camera/live';
    }

    onLivePreviewError(e) {
        console.log("live preview error", e)
    }

    renderSelection() {
        return html`<button class="left" @click="${() => this.takeSinglePhoto()}">
            <img class="photo-icon" src="../assets/single-image.jpg"><img>
            <div>${translate('take-photos.singleshot')}</div>
        </button>
        <button class="right" @click="${() => this.takeCollagePhoto()}">
            <img class="photo-icon" src="../assets/collage-image.jpg"><img>
            <div>${translate('take-photos.collage')}</div>
        </button>`;
    }

    allImagesCaptured() {
        if (this.mode == Mode.Single && this.photoCount == 1) {
            return true;
        }
        else if (this.mode == Mode.Collage && this.photoCount == 4) {
            return true;
        }
        else {
            return false;
        }
    }

    transition(state) {
        if (state == States.Countdown) {

            setTimeout(_ => this.transition(States.TakePictures), 3000);
        }
        else if (state == States.TakePictures) {
            this.takePicture().then(_ => {
                if (this.allImagesCaptured()) {
                    setTimeout(() => this.postProcess(), 500);
                }
                else {
                    this.scheduleNextPhoto();
                }
            },
                error => {
                    console.error(error);
                    this.reset();
                });
        }
        this.state = state;
    }

    scheduleNextPhoto() {
        const timer = this.renderRoot.querySelector("img-progress-bar");
        timer.start(() => {
            // timer ended
            this.requestUpdate();

            this.takePicture()
                .catch(e => {
                    alert("rror happend")
                })
                .then(_ => {
                    if (this.allImagesCaptured()) {
                        setTimeout(() => this.postProcess(), 500);
                    }
                    else {
                        setTimeout(() => this.scheduleNextPhoto(), 500);
                    }
                });


        });
    }

    postProcess() {
        this.state = States.Upload;

        PBLoadScreen.get().shown = true;

        clearInterval(this.pictureTimerHandler);

        this.awaitUpload()
            .then(_ => {
                //this.reset();
                //document.querySelector('app-main').router();
                router.navigate('/view/' + this.sessionId + ".jpeg");
            })
    }

    uuidv4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
            var r = Math.random() * 16 | 0, v = c == 'x' ? r : (r & 0x3 | 0x8);
            return v.toString(16);
        });
    }

    async takePicture() {
        console.log("Take picture #" + (this.photoCount + 1))
        this.flash();

        const photoMeta = this.mode == Mode.Collage ?
            {
                //mode: this.mode,
                index: this.photoCount,
                size: 4,
                id: this.sessionId
            }
            :
            {
                //mode: this.mode,
                index: 0,
                size: 1,
                id: this.sessionId
            };

        this.photoCount++;

        const response = await fetch(`${serverConnection.serverUrl}/resources/camera`, {
            method: 'POST',
            headers: {
                'X-PhotoMeta': JSON.stringify(photoMeta)
            }
        });

        if (response.status != 201) throw new Error("Taking a picture failed. (error=" + response.status + ")");
        this.serverRefs.push({
            "imageId": response.headers.get("X-ImageId"),
            "imageResourceServer": response.headers.get("Location")
        })
        console.log("Photo uploaded successfully.")
        const event = new CustomEvent('img-photo-upload-finished', {
            detail: {},
            bubbles: true
        });
        document.dispatchEvent(event);
    }

    async awaitUpload() {
        await this.photoService.uploadPromise
        window.currentImage = this.sessionId + ".jpeg";
    }

    flash() {
        this.renderRoot.querySelector("audio").play();
        const element = this.renderRoot.querySelector('.camera-flash');
        console.log(element)
        element.ontransitionend = () => {
            element.classList.remove('flash');
            element.ontransitionend = null;
        }
        setTimeout(() => element.classList.remove('flash'), 200);
        element.classList.add('flash');
    }

    static get styles() {
        return css`
          
        :host {
            display: block;
            height: 100vh;
        }

        video, {
            position: absolute;
            top: 0;
            left: 0;
            max-width: 100%;
            width: 100%;
            height: 100%;
        }

        .live-preview-container {
            position: absolute;
            display: flex;
            align-items: center;
            justify-content: center;
            width: 100vw;
            height: 100vh;
        }

        .live-preview {
            min-height: 100vmin;
            max-height: 100vmin;
        }
                
        .flash{ 
            position:fixed; 
            top:0;
            left:0;
            width:100%;
            height:100%;
            background-color:#fff;
        }
        
        .camera-flash {
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            right: 0;
            background-color:#fff;
            visibility: hidden;
            opacity: 0;
        
            transition: opacity 0.2s linear,
                        visibility 0s linear 0.2s;
        }
        
        .camera-flash.flash {
            visibility: visible;
            opacity: 1.0;
        
            transition-delay: 0s;
        }

        .idle-container {
            display: flex;
            align-items: center;
            justify-content: center;
            height: 100%;
            z-index: 1;
        }

        .idle-message-container {
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 1;
            background-color: rgba(0,0,0,0.5);
            padding: 20px;
            border-radius: 5px;
        }

        .idle-message {
            font-size: 50px;
            color: white;
        }

        .icon {
            filter: invert(0.95);
        }
        `;
    }
}

customElements.define('img-take-photos', TakePhotosView);