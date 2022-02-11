import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { SpinnerService } from '../../service/SpinnerService';
declare var $: any;
@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnInit {
  constructor() { }
  ngOnInit(): void {
    $(function () {
      $("#chat-box-title").click(function () {
        $('.main-section').toggleClass("open-more");
      });
      $("#minimize-chat").click(function () {
        $('.main-section').toggleClass("open-more");
      });
      $('#share-screen').click(function () {
        let res = window.confirm("Do you really want to share the screen?")
        if (!res) {
          return;
        } else {
          alert("Sharing....");
        }
      });
    });
  }
}