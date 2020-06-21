class Config {
    constructor() {
        this.config = null;
    }

    setAll(config) {
        this.config = config;
    }

    set(key, value) {
        this.config[key] = value;
    }

    get(key) {
        return this.config[key];
    }

    getAll() {
        return this.config;
    }
}

export default new Config();