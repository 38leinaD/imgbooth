import { html, LitElement, property, customElement } from '/lib/lit-element.js';
import router from './common/Router.js';

import './take-photos/TakePhotosView.js'
import './view-photos/ViewPhotosView.js'

import serverConnection from './common/ServerConnection.js';
//import PhotoboxLifecycle from './PhotoboxLifecycle.js';
import { registerTranslateConfig, use } from "/lib/lit-translate.js";


export default class ImgBoothMain extends LitElement {

    static get properties() {
        return {
            currentView: { type: Object }
        };
    }

    constructor() {
        super();
        var urlParams = new URLSearchParams(window.location.search);
        this.token = urlParams.get("token");
    }

    async connectedCallback() {
        super.connectedCallback();
        
		registerTranslateConfig({
		  loader: lang => fetch(`/assets/i18n/${lang}.json`).then(res => res.json())
		});
		await use("en");

        this.initRouter();
        //await new PhotoboxLifecycle(this.connection).run();
    }

    firstUpdated(changedProperties) {
    }

    initRouter() {
        router
            .on({
                'view/:id': (params) => this.currentView = html`<img-view-photos photo="${params.id}"></img-view-photo>`,
                '*': () => this.currentView = html`<img-take-photos></img-take-photos>`
            })
            .resolve();

        this.requestUpdate();
    }

    _renderCurrentView() {
        return this.currentView;
    }

    createRenderRoot() {
        return this;
    }

    render() {
        return html`
        <main>
            <header>
            </header>
            <nav>
                <button @click="${(e) => this.toggleAdmin()}">Toggle Admin</button>
            </nav>
            <article id="outlet">
                ${this._renderCurrentView()}
            </article>
            <footer>
            </footer>
        </main>
        `;
    }

    toggleAdmin() {
        let adminIframe = window.parent.document.querySelector('#admin');
        let style = getComputedStyle(adminIframe);
        if (style.display == 'none') {
            adminIframe.style.display = 'block';
        }
        else {
            adminIframe.style.display = 'none';
        }
    }
}

customElements.define('img-booth-main', ImgBoothMain)