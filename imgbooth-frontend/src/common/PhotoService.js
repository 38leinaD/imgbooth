import config from './Config.js'
export default class PhotoService {

    constructor() {
        this.reset();
    }

    reset() {
        this.uploadPromise = null;
        this.serverRefs = [];
    }

    uploadPhoto(img, partOfSequence) {

        if (this.uploadPromise == null) {
            this.uploadPromise = this._queuePhotoUpload(img, partOfSequence);
        }
        else {
            this.uploadPromise = this.uploadPromise.then(_ => this._queuePhotoUpload(img, partOfSequence))
        }

        return this.uploadPromise;
    }

    _queuePhotoUpload(img, meta) {
        console.log("Queueing photo for upload.")
        return fetch(`${config.backendUrl}/photos`, {
            method: 'POST',
            headers: {
                'X-PhotoMeta': JSON.stringify(meta)
            },
            body: img.src
        })
            .then(response => {
                if (response.status != 201) return Promise.reject("Upload failed with status " + response.status);
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
                return Promise.resolve();
            })
    }
}
