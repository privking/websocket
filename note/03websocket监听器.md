websocket监听器
===
* SpringBoot里面websocekt监听器的使用，包含订阅、取消订阅，socekt连接和断开连接4类监听器的编写和使用 ​
* 注意点：
    * 需要监听器类需要实现接口ApplicationListener<T> T表示事件类型，下列几种都是对应的websocket事件类型
    * 在监听器类上注解 @Component，spring会把改类纳入管理
* websocket模块监听器类型：
    * SessionSubscribeEvent 	订阅事件
    * SessionUnsubscribeEvent	取消订阅事件
    * SessionDisconnectEvent 	断开连接事件
    * SessionDisconnectEvent 	建立连接事件
## demo
```java
package priv.king.web_socket.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;

@Component
public class ConnectEventListener implements ApplicationListener<SessionConnectEvent>{

	public void onApplicationEvent(SessionConnectEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		System.out.println("【ConnectEventListener监听器事件 类型】"+headerAccessor.getCommand().getMessageType());
	}
}
```
```java
@Component
public class SubscribeEventListener implements ApplicationListener<SessionSubscribeEvent>{

	public void onApplicationEvent(SessionSubscribeEvent event) {
		StompHeaderAccessor headerAccessor =  StompHeaderAccessor.wrap(event.getMessage());
		System.out.println("【SubscribeEventListener监听器事件 类型】"+headerAccessor.getCommand().getMessageType());
		
		
	}

}
```