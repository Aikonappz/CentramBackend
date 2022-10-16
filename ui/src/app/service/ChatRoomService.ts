import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChatRoomService {
    private chatRoomId = new BehaviorSubject(null);
    currentChatRoomId = this.chatRoomId.asObservable();

    constructor() { }

    setChatRoomId(chatRoomId: string) {
        this.chatRoomId.next(chatRoomId)
    }

}