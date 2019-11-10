import { AfterViewInit, Component, ElementRef, OnInit, Renderer2, ViewChild } from '@angular/core';
import { WebSocketToNettyService } from 'src/components/WebSocketToNettyService';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, AfterViewInit {
  title: string = 'Spring boot with netty';
  error: any;
  completed = false;

  @ViewChild('chat', { static: false }) chatDiv: ElementRef<any>;
  @ViewChild('input', { static: false }) inputText: ElementRef<any>;

  constructor(private renderer2: Renderer2, private webSocketToNettyService: WebSocketToNettyService) { }

  ngOnInit(): void {
    // 連接 websocket
    console.log('url ...' + 'ws://' + window.location.hostname + ':8090/ws');  
    this.webSocketToNettyService.connect('ws://' + window.location.hostname + ':8090/ws');
  }

  ngAfterViewInit() {
    // 接收消息
    this.webSocketToNettyService.messageSubject.subscribe(
      msg => {
        if (msg) {
          const div = this.renderer2.createElement('div');
          const text = this.renderer2.createText(msg);
          this.renderer2.appendChild(div, text);
          this.renderer2.appendChild(this.chatDiv.nativeElement, div);
        }
      },
      err => this.error = err,
      () => this.completed = true
    );
  }

  sendMessage() {
    this.webSocketToNettyService.sendMessage(this.inputText.nativeElement.value);
    this.inputText.nativeElement.value = '';
  }

}
