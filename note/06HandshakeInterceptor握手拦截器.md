HandshakeInterceptor握手拦截器
===
* 编写一个类，实现一个接口HandshakeInterceptor；
* 写完之后需要在websocket配置里面启用.addInterceptors(new HttpHandShakeIntecepter())
* 实现两个方法beforeHandshake和afterHandshake，在里面可以获取resuest和response
## Demo
```java
public class HttpHandShakeIntecepter implements HandshakeInterceptor{

	@Override
	public boolean beforeHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) throws Exception {

		System.out.println("【握手拦截器】beforeHandshake");
		
		
		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			System.out.println("【握手拦截器】beforeHandshake sessionId="+sessionId);
			attributes.put("sessionId", sessionId);
		}
		
		return true;
	}

	
	
	@Override
	public void afterHandshake(ServerHttpRequest request,
			ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		System.out.println("【握手拦截器】afterHandshake");
		
		if(request instanceof ServletServerHttpRequest) {
			ServletServerHttpRequest servletRequest = (ServletServerHttpRequest)request;
			HttpSession session =  servletRequest.getServletRequest().getSession();
			String sessionId = session.getId();
			System.out.println("【握手拦截器】afterHandshake sessionId="+sessionId);
		}
		
		
		
	}

}
```