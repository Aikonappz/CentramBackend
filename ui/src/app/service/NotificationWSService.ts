import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import * as SockJS from 'sockjs-client';

import { NotificationService } from './NotificationService';
import { environment } from '../../environments/environment';
import { AppUtility } from '../config/AppUtility';
import { LoggedInUserService } from './LoggedInUserService';


@Injectable({
    providedIn: 'root'
})
export class NotificationWSService {

    stompClient: any;

    constructor(
        private notificationService: NotificationService,
        private loggedInUserService: LoggedInUserService
    ) { }

    connect(): void {
        console.log('webSocket Connection');
        const ws = new SockJS(
            environment.appWSServiceEndpoint,
            null,
            {}
        );
        let loggedInUser = this.loggedInUserService.getLoggedInUser();
        let topicName = environment.appWSNotificationTopic + "/" + loggedInUser.userId;
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
            }, this.errorCallBack);
    }

    disconnect(): void {
        if (this.stompClient !== null) {
            this.stompClient.disconnect();
            this.notificationService.notificationMessage.emit({});
        }
        console.log('Disconnected');
    }

    // on error, schedule a reconnection attempt
    errorCallBack(error) {
        console.log('errorCallBack -> ' + error);
        setTimeout(() => {
            this.connect();
        }, 5000);
    }
    onMessageReceived(message) {
        //console.log('Message Recieved from Server :: ' + message);
        // Emits the event.
        this.notificationService.notificationMessage.emit(JSON.parse(message.body));
    }
}