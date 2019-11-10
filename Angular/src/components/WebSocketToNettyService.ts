import {Injectable} from '@angular/core';
import {interval, Subject} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WebSocketToNettyService {
  messageSubject;                                 // subject用於發送事件
  private url;                                    // default 的url
  private webSocket: WebSocket;                   // websocket 對象
  connectSuccess = false;                         // websocket 連線成功
  period = 60 * 1000 * 10;                        // 10分鐘檢查一次
  serverTimeoutSubscription = null;               // 定時檢測連接對象
  reconnectFlag = false;                          // 重連
  reconnectPeriod = 5 * 1000;                     // 重連失敗，則五分鐘重連一次
  reconnectSubscription = null;                   // 重連訂閱對象
  runTimeSubscription;                            // 紀錄執行連線 subscription
  runTimePeriod = 60 * 10000;                     // 紀錄執行連線時間

  constructor() {
    this.messageSubject = new Subject();
    console.log('開始心跳檢測');
    this.heartCheckStart();
    this.calcRunTime();
  }

  sendMessage(message) {
    if (this.webSocket.onopen) {
      this.webSocket.send(message);
    } 
  }
  
  connect(url) {
    if (!!url) {
      this.url = url;
    }
    this.createWebSocket();
  }

  createWebSocket() {
    // 如果沒有建立過連線，則建立連線並加入時間監聽
    this.webSocket = new WebSocket(this.url);
    // 建立連線成功
    this.webSocket.onopen = (e) => this.onOpen(e);
    // 接收到消息
    this.webSocket.onmessage = (e) => this.onMessage(e);
    // 連線關閉
    this.webSocket.onclose = (e) => this.onClose(e);
    // 異常
    this.webSocket.onerror = (e) => this.onError(e);
  }

  onOpen(e) {
    console.log('websocket 已連線');
    // 連線成功
    this.connectSuccess = true;
    // 如果是重連線中
    if (this.reconnectFlag) {
      // 1.停止重連
      this.stopReconnect();
      // 2.重新開啟心跳檢測
      this.heartCheckStart();
      // 3.重新開始計算運行時間
      this.calcRunTime();
    }
  }

  /**
   * 接受到消息
   */
  onMessage(event) {
    console.log('接收到的消息', event.data);
    console.log('接收到的消息時間', new Date().getTime());
    this.messageSubject.next(event.data);
  }

  /**
   * 關閉連線
   */
  private onClose(e) {
    console.log('關閉連線', e);
    this.connectSuccess = false;
    this.webSocket.close();
    // 關閉後重新連線
    this.reconnect();
    this.stopRunTime();
    // throw new Error('webSocket connection closed:)');
  }

  /**
   * 連線異常
   */
  private onError(e) {
    // 出現異常時一定會執行onClose,所以只在onClose做一次重連動作
    console.log('連線異常', e);
    this.connectSuccess = false;
    // throw new Error('webSocket connection error:)');
  }

  /**
   * 開始重新連線
   */
  reconnect() {
    // 如果已重連,則直接 return,避免重新連線
    if (this.connectSuccess) {
      this.stopReconnect();
      console.log('已重連成功,停止重連!');
      return;
    }
    // 如果正在連線中,則直接return,避免產生多個輪循
    if (this.reconnectFlag) {
      console.log('正在重連,直接返回');
      return;
    }
    // 開始重連
    this.reconnectFlag = true;
    // 如果沒能重新連線,則定時重新連線
    this.reconnectSubscription = interval(this.reconnectPeriod).subscribe(async (val) => {
      console.log(`重連:${val}次`);
      const url = this.url;
      // 重新連接
      this.connect(url);
    });
  }

  /**
   * 停止重連
   */
  stopReconnect() {
    // 連線標誌設為false
    this.reconnectFlag = false;
    // 取消訂閱
    if (typeof this.reconnectSubscription !== 'undefined' && this.reconnectSubscription != null) {
      this.reconnectSubscription.unsubscribe();
    }
  }

  /**
   * 開始心跳檢測
   */
  heartCheckStart() {
    this.serverTimeoutSubscription = interval(this.period).subscribe((val) => {
      // 保持連線狀態下
      if (this.webSocket != null && this.webSocket.readyState === 1) {
        console.log(val, '連線狀態，發送消息以保持連線');
      } else {
        // 停止心跳檢測
        this.heartCheckStop();
        // 開始重新連線
        this.reconnect();
        console.log('连接已断开,重新连接');
      }
    });
  }

  /**
   * 停止心跳檢測
   */
  heartCheckStop() {
    // 取消訂閱心跳檢測
    if (typeof this.serverTimeoutSubscription !== 'undefined' && this.serverTimeoutSubscription != null) {
      this.serverTimeoutSubscription.unsubscribe();
    }
  }

  /**
   * 開始計算執行時間
   */
  calcRunTime() {
    this.runTimeSubscription = interval(this.runTimePeriod).subscribe(period => {
      console.log('執行時間', `${period}分鐘`);
    });
  }

  /**
   * 停止計算執行時間
   */
  stopRunTime() {
    if (typeof this.runTimeSubscription !== 'undefined' && this.runTimeSubscription !== null) {
      this.runTimeSubscription.unsubscribe();
    }
  }
}