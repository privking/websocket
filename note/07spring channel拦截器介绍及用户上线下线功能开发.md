spring channel拦截器介绍及用户上线下线功能开发
===
* ChannelInterceptorAdapter 频道拦截器适配器,具体实现的接口是ChannelIntecepter ​
* 需要ChannelInterceptorAdapter子类重写override对应的方法，实现自己的逻辑，主要是public void postSend(Message<?> message, MessageChannel channel, boolean sent)
* ChannelInterceptorAdapter子类需要在配置Websocket的配置里面加入
* 在配置类里面加入
```java
@Override
public void configureClientInboundChannel(ChannelRegistration registration) {
    registration.interceptors( new SocketChannelIntecepter());
}

@Override
public void configureClientOutboundChannel(ChannelRegistration registration) {
    registration.interceptors( new SocketChannelIntecepter());
}
```

## Demo
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer{
	/**
	 * 注册端点，发布或者订阅消息的时候需要连接此端点
	 * setAllowedOrigins 非必须，*表示允许其他域进行连接
	 * withSockJS  表示开始sockejs支持
	 */
	public void registerStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/endpoint-websocket").addInterceptors(new HttpHandShakeIntecepter())
		.setAllowedOrigins("*").withSockJS();
	}

	/**
	 * 配置消息代理(中介)
	 * enableSimpleBroker 服务端推送给客户端的路径前缀
	 * setApplicationDestinationPrefixes  客户端发送数据给服务器端的一个前缀
	 */
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		
		registry.enableSimpleBroker("/topic", "/chat");
		registry.setApplicationDestinationPrefixes("/app");
		
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors( new SocketChannelIntecepter());
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
		registration.interceptors( new SocketChannelIntecepter());
	}	
}
```

```java
public class SocketChannelIntecepter extends ChannelInterceptorAdapter{

	/**
	 * 在完成发送之后进行调用，不管是否有异常发生，一般用于资源清理
	 */
	@Override
	public void afterSendCompletion(Message<?> message, MessageChannel channel,
			boolean sent, Exception ex) {
		System.out.println("SocketChannelIntecepter->afterSendCompletion");
		super.afterSendCompletion(message, channel, sent, ex);
	}

	
	/**
	 * 在消息被实际发送到频道之前调用
	 */
	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		System.out.println("SocketChannelIntecepter->preSend");
		
		return super.preSend(message, channel);
	}

	/**
	 * 发送消息调用后立即调用
	 */
	@Override
	public void postSend(Message<?> message, MessageChannel channel,
			boolean sent) {
		System.out.println("SocketChannelIntecepter->postSend");
		
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(message);//消息头访问器
		
		if (headerAccessor.getCommand() == null ) return ;// 避免非stomp消息类型，例如心跳检测
		
		String sessionId = headerAccessor.getSessionAttributes().get("sessionId").toString();
		System.out.println("SocketChannelIntecepter -> sessionId = "+sessionId);
		
		switch (headerAccessor.getCommand()) {
		case CONNECT:
			connect(sessionId);
			break;
		case DISCONNECT:
			disconnect(sessionId);
			break;
		case SUBSCRIBE:
			
			break;
			
		case UNSUBSCRIBE:
			
			break;
		default:
			break;
		}
		
	}

	
	//连接成功
	private void connect(String sessionId){
		System.out.println("connect sessionId="+sessionId);
	}
	
	
	//断开连接
	private void disconnect(String sessionId){
		System.out.println("disconnect sessionId="+sessionId);
		//用户下线操作
		UserChatController.onlineUser.remove(sessionId);
	}
	
	
	
	
	
}
```