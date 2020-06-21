import { LitElement, html, css } from '../lib/lit-element.js';
import { styleMap } from '../lib/lit-html/directives/style-map.js'

class PBProgressBar extends LitElement {
    static get properties() {
        return {
            animationState: { type: Object }
        }
    }

    constructor() {
        super();

        this.animationState = {};
    }

    connectedCallback() {
        super.connectedCallback();

        this.renderRoot.addEventListener("animationend", () => {
            this.animationState = { display: 'none' };
            this.callback();
        });
    }

    disconnectedCallback() {
    }

    firstUpdated(changedProperties) {
    }

    updated(changedProperties) {

    }

    start(callback) {
        this.animationState = { display: 'block' };
        this.callback = () => callback();
    }

    render() {
        return html`
            <div class="final__animation-bar-1" style="${styleMap(this.animationState)}">
                <span data-label="Loading"></span>
            </div>
        `;
    }

    static get styles() {
        return css`
        :host {
            display: block;
            position: absolute;
            left: 0;
            bottom: 0;
            width: 100vw;
        }

        .final__animation-bar-1 {
            
            display: none;
            width: 800px;
            margin: 0 auto;
            padding: 5px;
            font-size: 16px;
            line-height: 16px;
            border-radius: 30px;
            background: rgba(0, 0, 0, 0.1);
            box-shadow: inset 0 1px 2px rgba(0, 0, 0, 0.25), 0 1px rgba(255, 255, 255, 0.08);
          }
          .final__animation-bar-1 span {
            position: relative;
            display: inline-block;
            vertical-align: middle;
            height: 20px;
            border-radius: 10px 0 0 10px;
            overflow: hidden;
            background-color: #f56982;
            background-size: 100%;
            background-image: linear-gradient(to bottom, #f2395a, #b90c2b);
            animation: progress-anim-1 2s forwards linear;
          }
          .final__animation-bar-1 span:after {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            content: "";
            background-size: 100%;

            background-size: 30px 30px;
            opacity: 0.3;
          }
        
        
         
          @keyframes progress-anim-1 {
            0% {
              width: 0%;
            }
            100% {
              width: 100%;
            }
          }
          
        `;
    }
}

customElements.define('img-progress-bar', PBProgressBar);