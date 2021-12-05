import { NotificationType } from "./enumerator/NotificationType";
import { Status } from "./enumerator/Status";

export class NotificationVO {
    id: number;
    title: string;
    body: string;
    notificationType: NotificationType;
    status: any;
    userId: number;
    constructor() {
        this.id = null;
        this.title = '';
        this.body = '';
        this.userId = null;
        this.notificationType = NotificationType.INFO;
        this.status = Status.PUSHED;
    }
}