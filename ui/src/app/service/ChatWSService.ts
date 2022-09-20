import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { NotificationService } from './NotificationService';
import { environment } from '../../environments/environment';
import { LoggedInUserService } from './LoggedInUserService';
import { ChatService } from './ChatService';


@Injectable({
    providedIn: 'root'
})
export class ChatWSService {
    stompClient: any;
    constructor(
        private chatService: ChatService,
        private loggedInUserService: LoggedInUserService
    ) { }

    connect(chatRoomId: string): void {
        console.log('Web Socket Connection for chat');
        const ws = new SockJS(
            environment.appWSServiceEndpoint,
            null,
            {}
        );
        let loggedInUser = this.loggedInUserService.getLoggedInUser();
        let topicName = environment.appWSChatTopic + "/" + chatRoomId;
        this.stompClient = Stomp.over(ws);
        //TODO : need to check
        this.stompClient.debug = null;
        const _this = this;
        _this.stompClient.connect(
            environment.appWSCred,
            function (frame) {
                _this.stompClient.subscribe(topicName,
                    function (sdkEvent) {
                        _this.onMessageReceived(sdkEvent);
                    }
                );
            }, function (error) {
                console.log('chat errorCallBack -> ' + error);
                setTimeout(() => {
                    this.connect();
                }, 5000);
            });
    }

    disconnect(): void {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            this.chatService.chatMessage.emit({});
        }
        console.log('Disconnected');
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error) {
        // console.log('errorCallBack -> ' + error);
        // setTimeout(() => {
        //     this.connect();
        // }, 5000);
    }
    onMessageReceived(message) {
        //console.log('Message Recieved from Server :: ' + message);
        // Emits the event.
        this.chatService.chatMessage.emit(JSON.parse(message.body));
    }
}