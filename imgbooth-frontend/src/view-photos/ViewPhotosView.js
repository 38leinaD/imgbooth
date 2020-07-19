import { html, LitElement, css } from '../lib/lit-element.js';
import { until } from '../lib/lit-html/directives/until.js';
import { translate, get } from "/lib/lit-translate.js";

import router from '../common/Router.js';
import PBLoadScreen from '../common/PBLoadScreen.js'
import config from '../common/Config.js'

const States = {
    View: "View",
    Printing: "Printing"
}

export default class ViewPhotosView extends LitElement {

    constructor() {
        super();

        this.state = States.View;
    }

    static get properties() {
        return {
            state: { type: String }
        };
    }

    connectedCallback() {
        super.connectedCallback();

        this.photoId = this.getAttribute("photo");

        this.keyboardListener = (e) => {
            console.log(e)
            if (this.state != States.View) return;
            if (e.key == "1") {
                router.navigate('/');
                e.preventDefault();
            }
            else if (e.key == "2") {
                this.printPhoto();
            }
        };
        document.addEventListener('keydown', this.keyboardListener);

        //PBLoadScreen.get().shown = true;
    }

    disconnectedCallback() {
        //console.log("DISCONNECTEDCALLBACK FOR ", ViewPhotosView)

        document.removeEventListener('keydown', this.keyboardListener);
        PBLoadScreen.get().shown = false;
    }


    firstUpdated(changedProperties) {

    }

    render() {
        return html`
            <link rel="stylesheet" type="text/css" media="screen" href="./style.css" />
            <div class="container">
            ${this.photoId != null ? html`<img class="postcard" src="${this.photoUrl()}" @load="${() => this.imageLoaded()}"></img>` : this.placeHolderImage()} 
            </div>
            
            ${this.state == States.View ? html`
            <button class="left" @click="${() => router.navigate('/')}">
            <img class="icon" src="../assets/icons/camera-retro.svg"><img>

                <div>${translate('view-photos.new-photo')}</div>
            </button>
            <button class="right" @click="${() => this.printPhoto()}">
                <img class="icon" src="../assets/icons/print.svg"><img>
                <div>${translate('view-photos.print-photo')}</div>
            </button>
            ` : html``}
        `;
    }

    photoUrl() {
        //return `http://localhost:8080/resources/photos/${this.photoId}`;
        return `http://localhost:8080/resources/photos/photoshoots/${this.photoId}`;
    }

    imageLoaded() {
        PBLoadScreen.get().progress = 1.0;
        setTimeout(() => PBLoadScreen.get().shown = false, 500);
    }

    async printPhoto() {
        this.state = States.Printing;

        PBLoadScreen.get().reset();
        PBLoadScreen.get().message = get('view-photos.printing')
        PBLoadScreen.get().autoProgress(config["printer.printduration"]);
        PBLoadScreen.get().shown = true;

        //const imageId = '/tmp/collage_2019-11-02_16:05:28.787.png';
        let response = await fetch(`http://localhost:8080/resources/prints?image=${this.photoId}`, {
            method: 'POST'
        });
        PBLoadScreen.get().stopAutoProgress()

        console.log("---> ", response.status);
        if (response.status == 200) {
            PBLoadScreen.get().message = get('view-photos.take-photo')
            console.log("START")
            console.time("TIMER");
            setTimeout(_ => {
                PBLoadScreen.get().reset();
                console.log("END")
                console.timeEnd("TIMER")
                router.navigate('/take');
            }, 5000);
        }
        else {
            PBLoadScreen.get().message = get('view-photos.print-error')
            setTimeout(_ => {
                PBLoadScreen.get().reset();
                router.navigate('/take');
            }, 5000);
        }

    }

    placeHolderImage() {
        return html`<svg class="postcard" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" width="100%" height="100%"><path d="M464 64H48C21.49 64 0 85.49 0 112v288c0 26.51 21.49 48 48 48h416c26.51 0 48-21.49 48-48V112c0-26.51-21.49-48-48-48zm-6 336H54a6 6 0 0 1-6-6V118a6 6 0 0 1 6-6h404a6 6 0 0 1 6 6v276a6 6 0 0 1-6 6zM128 152c-22.091 0-40 17.909-40 40s17.909 40 40 40 40-17.909 40-40-17.909-40-40-40zM96 352h320v-80l-87.515-87.515c-4.686-4.686-12.284-4.686-16.971 0L192 304l-39.515-39.515c-4.686-4.686-12.284-4.686-16.971 0L96 304v48z"/></svg>`;
    }

    static get styles() {
        return css`
        
        .container {
            overflow: hidden;
            position: absolute;
            width: 100vw;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        svg {
            fill: white;
        }
        
        .card {
            display:inline-block;
            padding: 0.5em;
            background-color: white;
            transform: rotate(3deg) translateX(0px) scale(0.8);       
        }

        .card__content {
            background-color: lightgrey;
        }

        .card.barcode > .card__content {
            padding: 50px;
        }

        a {
            text-decoration: none;
        }

        .postcard {
            min-height: 100vmin;
            max-height: 100vmin;
            border: 10px solid white;
            transform: rotate(3deg) translateX(0px) scale(0.8);  
        }

        .icon {
            filter: invert(0.95);
        }
        `;
    }
}

customElements.define('img-view-photos', ViewPhotosView);