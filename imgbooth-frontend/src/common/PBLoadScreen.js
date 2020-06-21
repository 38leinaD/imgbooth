import { LitElement, html, css } from '../lib/lit-element.js';

export default class PBLoadScreen extends LitElement {
    static get properties() {
        return {
            shown: { type: Boolean },
            message: { type: String },
            progress: { type: Number }
        }
    }

    constructor() {
        super();
        this.reset();
    }

    connectedCallback() {
        super.connectedCallback();
    }

    disconnectedCallback() {
    }

    firstUpdated(changedProperties) {
    }

    updated(changedProperties) {
    }

    autoProgress(secondsToCompletion) {
        const millisToCompletion = secondsToCompletion * 1000;
        const interval = 500;
        const steps = millisToCompletion / interval;
        let progressToCompletion = 0;
        this.shown = true;

        this.autoProgressTimer = setInterval(() => {
            if (progressToCompletion < millisToCompletion) {
                progressToCompletion += interval;
                this.progress = progressToCompletion / millisToCompletion;
            }
            else {
                clearInterval(this.autoProgressTimer);
            }
        }, interval);
    }

    reset() {
        this.progress = 0.0;
        this.shown = false;
        this.message = 'Bitte warten...';
    }

    stopAutoProgress() {
        clearInterval(this.autoProgressTimer);
    }

    render() {
        return this.shown ? html`<div class="container">    
                <div class="message_container">
                    <div class="message">${this.message}</div>
                    <div class="line stripesLoader" style="background-position: ${100 * this.progress}%;"></div>
                </div>
            </div>` : html``;
    }

    static get() {
        let loader = document.querySelector("img-load-screen");
        if (loader == null) {
            loader = document.createElement("img-load-screen");
            document.body.appendChild(loader);
        }
        return loader;
    }

    static get styles() {
        return css`
        
        .container {
            position: absolute;
            left: 0;
            top: 0;
            width: 100vw;
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        @keyframes pulse {
            0% { transform: scale(0.9); opacity: 0.7; }
            50% { transform: scale(1); opacity: 1; }
            100% { transform: scale(0.9); opacity: 0.7; }
        }

        .loading {
            vertical-align: middle;
            max-height: 50vmin;
            fill: lightgrey; 
            animation: pulse 3s infinite ease-in-out;
        }

        .message_container {
            background-color: #eee;
            border-radius: 5px;
            position: absolute;
            width: 50%;
            height: 190px;
                        
            display: flex;
            align-items: center;
            flex-direction: column;
        }

        .message {
            color: #333;
            font-size: 3rem;
            margin: 20px 0;
        }

        .line {
            width: 70%;
            border: 1px solid #333;
        }

        .stripesLoader {
            height: 40px;
            background: linear-gradient(
                90deg,
                #eee 50%,
                transparent 50%
            );
            background-color: crimson;
            background-size: 200%;
            border-radius: 5px;
            position: relative;
            transform: scale(-1);
        }

        .stripesLoader:before {
            position: absolute;
            content: "";
            width: 100%;
            height: 100%;
            border-radius: 5px;
            background: linear-gradient(
                45deg,
                transparent 25%,
                rgba(255, 255, 255, 0.5) 25%,
                rgba(255, 255, 255, 0.5) 60%,
                transparent 60%
            );
            background-size: 120px 100%;
            animation: shift 2s linear infinite;
        }

        @keyframes shift {
            to{background-position: 120px, 100%;}
        }
        `;
    }
}

customElements.define('img-load-screen', PBLoadScreen);