import { MediaFile } from "./MediaFile";

export class ChatMessage {
    id: number;
    moduleId: number;
    subModuleId: number;
    roomId: string;
    senderId: number;
    recipientId: number;
    senderName: string;
    recipientName: string;
    content: string;
    conversationTime: any;
    status: any;
    attachments: MediaFile[];
    intiateChat: boolean;

    constructor() {
        this.id = null;
        this.moduleId = null;
        this.subModuleId = null;
        this.roomId = null;
        this.senderId = null;
        this.recipientId = null;
        this.senderName = null;
        this.recipientName = null;
        this.content = null;
        this.conversationTime = null;
        this.status = null;
        this.attachments = [];
        this.intiateChat = false;
    }
}
