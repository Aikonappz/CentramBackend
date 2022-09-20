import { EventEmitter, Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ChatService {
    chatMessage = new EventEmitter();
    constructor() {

    }

}