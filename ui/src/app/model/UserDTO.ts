import { MediaFile } from "./MediaFile";

export class UserDTO {
    oldPassword: string;
    newPassword: string;
    mediaFile: MediaFile;

    constructor() {
        this.oldPassword = '';
        this.newPassword = '';
        this.mediaFile = new MediaFile();
    }
}