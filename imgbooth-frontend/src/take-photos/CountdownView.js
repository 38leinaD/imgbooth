import { LitElement, html, css } from '../lib/lit-element.js';

class CountdownView extends LitElement {
    static get properties() {
        return {
        }
    }

    constructor() {
        super();
    }

    connectedCallback() {
        super.connectedCallback();
    }

    disconnectedCallback() {
    }

    firstUpdated(changedProperties) {
        this.renderRoot.querySelector('audio').play();
    }

    updated(changedProperties) {
    }

    render() {
        return html`
        <audio src="../assets/audio/movie-countdown-beep.mp3" preload="auto"></audio>
        <div class="box">
            <div class="circle circle1"></div>
            <div class="circle circle2"></div>
            <div class="niddle"></div>
            <div class="number">
                <div>3</div>
                <div>2</div>
                <div>1</div>
            </div>
        </div>
        `;
    }

    static get styles() {
        return css`
        
        .box
        {
            position: absolute;
            top: 0;
            left: 0;
            bottom: 0;
            right: 0;
            opacity: 0.5;
            background: radial-gradient(#fff, #757575);
            overflow: hidden;
        }
        .box:before
        {
            content: '';
            position: absolute;
            top: 50%;
            left: 0;
            transform: translateY(-50%);
            width: 100%;
            height: 5px;
            background: #000;
        }
        .box:after
        {
            content: '';
            position: absolute;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            width: 5px;
            height: 100%;
            background: #000;
        }
        .circle
        {
            position: absolute;
            top: 50%;
            left: 50%;
            transform: translate(-50%,-50%);
            width: 500px;
            height: 500px;
            border: 5px solid #fff;
            border-radius: 50%;
            z-index: 1;
        }
        .circle.circle2
        {
            width: 600px;
            height: 600px;
        }
        .niddle
        {
            position: absolute;
            top: calc(50% - 2px);
            left: 50%;
            height: 4px;
            width: 1200px;
            background: #000;
            animation: animate 1s linear infinite;
            transform-origin: left;
        }
        @keyframes animate
        {
            0%
            {
                transform: rotate(-90deg);
            }
            100%
            {
                transform: rotate(270deg);
            }
        }
        .number
        {
            position: absolute;
            width: 100%;
            height: 100%;
        }
        .number div
        {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            font-size: 25em;
            font-weight: bold;
            display: flex;
            justify-content: center;
            align-items: center;
            opacity: 0;
            animation: animateNumber 3s linear infinite;   
        }
        .number div:nth-child(1)
        {
            animation-delay: 0s;
        }
        .number div:nth-child(2)
        {
            animation-delay: 1s;
        }
        .number div:nth-child(3)
        {
            animation-delay: 2s;
        }
        .number div:nth-child(4)
        {
            animation-delay: 3s;
        }
        .number div:nth-child(5)
        {
            animation-delay: 4s;
        }
        .number div:nth-child(6)
        {
            animation-delay: 5s;
        }
        .number div:nth-child(7)
        {
            animation-delay: 6s;
        }
        .number div:nth-child(8)
        {
            animation-delay: 7s;
        }
        .number div:nth-child(9)
        {
            animation-delay: 8s;
        }
        .number div:nth-child(10)
        {
            animation-delay: 9s;
        }
        @keyframes animateNumber
        {
            0%,10%
            {
                opacity: 1;
            }
            10.01%,100%
            {
                opacity: 0;
            }
        }
        `;
    }
}

customElements.define('img-countdown', CountdownView);