import { Incident } from "./Incident";
import { MediaFile } from "./MediaFile";
import { User } from "./User";

export class IncidentCommunication {
    id: number;
    message: string;
    incident: Incident
    communicatedBy: User;
    attachments: MediaFile[];

    constructor() {
        this.id = null;
        this.message = '';
        this.incident = new Incident();
        this.communicatedBy = new User();
        this.attachments = [];
    }
}