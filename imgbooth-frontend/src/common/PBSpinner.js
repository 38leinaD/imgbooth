import { LitElement, html, css } from "../lib/lit-element.js";

export default class PBSpinner extends LitElement {

  static get styles() {
    return css`
            :host {
                display: none;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                position: absolute;
                left: 0;
                top: 0;
                background-color: rgba(0,0,0,0.1);
                z-index: 100;
                height: 100vh;
                width: 100vw;
            }
            .load-box {
                display: flex;
                flex-direction: column;
                justify-content: center;
                align-items: center;
                background-color: white;
                border-radius: 5px;
                padding: 4em;
            }
            .lds-spinner {
                display: inline-block;
                position: relative;
                width: 64px;
                height: 64px;
              }
              .lds-spinner div {
                transform-origin: 32px 32px;
                animation: lds-spinner 1.2s linear infinite;
              }
              .lds-spinner div:after {
                content: " ";
                display: block;
                position: absolute;
                top: 3px;
                left: 29px;
                width: 5px;
                height: 14px;
                border-radius: 20%;
                background: black;
              }
              .lds-spinner div:nth-child(1) {
                transform: rotate(0deg);
                animation-delay: -1.1s;
              }
              .lds-spinner div:nth-child(2) {
                transform: rotate(30deg);
                animation-delay: -1s;
              }
              .lds-spinner div:nth-child(3) {
                transform: rotate(60deg);
                animation-delay: -0.9s;
              }
              .lds-spinner div:nth-child(4) {
                transform: rotate(90deg);
                animation-delay: -0.8s;
              }
              .lds-spinner div:nth-child(5) {
                transform: rotate(120deg);
                animation-delay: -0.7s;
              }
              .lds-spinner div:nth-child(6) {
                transform: rotate(150deg);
                animation-delay: -0.6s;
              }
              .lds-spinner div:nth-child(7) {
                transform: rotate(180deg);
                animation-delay: -0.5s;
              }
              .lds-spinner div:nth-child(8) {
                transform: rotate(210deg);
                animation-delay: -0.4s;
              }
              .lds-spinner div:nth-child(9) {
                transform: rotate(240deg);
                animation-delay: -0.3s;
              }
              .lds-spinner div:nth-child(10) {
                transform: rotate(270deg);
                animation-delay: -0.2s;
              }
              .lds-spinner div:nth-child(11) {
                transform: rotate(300deg);
                animation-delay: -0.1s;
              }
              .lds-spinner div:nth-child(12) {
                transform: rotate(330deg);
                animation-delay: 0s;
              }
              @keyframes lds-spinner {
                0% {
                  opacity: 1;
                }
                100% {
                  opacity: 0;
                }
              }
              
              .message {
                  margin: 10px;
              }
        `;
  }

  static get properties() {
    return {
      message: { type: String },
      shown: { type: Boolean }
    }
  }

  constructor() {
    super();
    this.message = "Loading...";
    this._shown = true;
  }

  connectedCallback() {
    super.connectedCallback();

    if (this.shown) {
      this.show();
    }
    else {
      this.hide();
    }
  }

  render() {
    return html`
        <div class="load-box">
            <div class="lds-spinner"><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div><div></div></div>
            <div class="message">${this.message}</div>
        </div>`;
  }

  show() {
    this.shown = true;
    this.style.display = 'flex';
  }

  hide() {
    this.shown = false;
    this.style.display = 'none';
  }
}

customElements.define('img-spinner', PBSpinner);