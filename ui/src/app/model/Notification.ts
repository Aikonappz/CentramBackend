import { Base } from "./Base";
import { NotificationType } from "./enumerator/NotificationType";
import { Status } from "./enumerator/Status";
import { User } from "./User";

export class Notification extends Base {
    id: number;
    notificationTitle: string;
    notificationBody: string;
    user: User;
    notificationType: NotificationType;
    status: any;
    constructor() {
        super();
        this.id = null;
        this.notificationBody = '';
        this.notificationTitle = '';
        this.user = new User();
        this.notificationType = NotificationType.INFO;
        this.status = Status.PUSHED;
    }
}
export interface NotificationList {
    content: Notification[];
    totalElements: number;
}