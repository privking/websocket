QuickStart
===
## pom.xml
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>net.xdclass</groupId>
  <artifactId>xdclass_websocket</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  
   <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>1.5.9.RELEASE</version>
    </parent>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-websocket</artifactId>
        </dependency>

        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>webjars-locator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>sockjs-client</artifactId>
            <version>1.0.2</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>stomp-websocket</artifactId>
            <version>2.3.3</version>
        </dependency>
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>bootstrap</artifactId>
            <version>3.3.7</version>
        </dependency>
        
       
        <dependency>
            <groupId>org.webjars</groupId>
            <artifactId>jquery</artifactId>
            <version>3.1.0</version>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <properties>
        <java.version>1.8</java.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

## config
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

		registry.addEndpoint("/endpoint-websocket").setAllowedOrigins("*").withSockJS();
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
}

```
## contoller
```java

@Controller
public class GameInfoController {

	
	@MessageMapping("/v1/chat")
	@SendTo("/topic/game_chat")
	public OutMessage gameInfo(InMessage message){
		
		return new OutMessage(message.getContent());
	}
}

```

## Model
```java
public class InMessage {
	
	private String from;
	
	private String to;
	
	private String content;
	
	private Date time;

	public String getFrom() {
		return from;
	}

	
	public InMessage(){}
	
	public InMessage(String content) {
		this.content = content;
	}
	
	
	
	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}

```
```java
public class OutMessage {

	private String from;
	
	private String content;
	
	private Date time = new Date();

	public OutMessage(){}
	
	public OutMessage(String content){
		this.content = content;
		
	}
	
	
	public String getFrom() {
		return from;
	}
	
	

	public void setFrom(String from) {
		this.from = from;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
}
```

## static
### app.js
```js
var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#notice").html("");
}

function connect() {
    var socket = new SockJS('/endpoint-websocket'); //连接上端点(基站)
    
    stompClient = Stomp.over(socket);			//用stom进行包装，规范协议
    stompClient.connect({}, function (frame) {	
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/game_chat', function (result) {
        	console.info(result)
        	showContent(JSON.parse(result.body));
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
	
    stompClient.send("/app/v1/chat", {}, JSON.stringify({'content': $("#content").val()}));
}

function showContent(body) {
    $("#notice").append("<tr><td>" + body.content + "</td> <td>"+new Date(body.time).toLocaleString()+"</td></tr>");
}





$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
});


```
### admin.html
```html
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<head>
    <title>Hello WebSocket</title>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/v1/main.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script src="/v1/app.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">建立连接通道:</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="name">发布新公告</label>
                    <input type="text" id="content" class="form-control" placeholder="请输入...">
                </div>
                <button id="send" class="btn btn-default" type="submit">发布</button>
            </form>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>游戏公告内容</th>
                </tr>
                </thead>
                <tbody id="notice">
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>


```
### index.html
```java
<!DOCTYPE html>
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<head>
    <title>Hello WebSocket</title>
    <link href="/webjars/bootstrap/css/bootstrap.min.css" rel="stylesheet">
    <link href="/v1/main.css" rel="stylesheet">
    <script src="/webjars/jquery/jquery.min.js"></script>
    <script src="/webjars/sockjs-client/sockjs.min.js"></script>
    <script src="/webjars/stomp-websocket/stomp.min.js"></script>
    <script src="/v1/app.js"></script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websocket relies on Javascript being
    enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div id="main-content" class="container">
    <div class="row">
        <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="connect">建立连接通道:</label>
                    <button id="connect" class="btn btn-default" type="submit">Connect</button>
                    <button id="disconnect" class="btn btn-default" type="submit" disabled="disabled">Disconnect
                    </button>
                </div>
            </form>
        </div>
        <!-- <div class="col-md-6">
            <form class="form-inline">
                <div class="form-group">
                    <label for="name">发布新公告</label>
                    <input type="text" id="content" class="form-control" placeholder="请输入...">
                </div>
                <button id="send" class="btn btn-default" type="submit">发布</button>
            </form>
        </div> -->
    </div>
    <div class="row">
        <div class="col-md-12">
            <table id="conversation" class="table table-striped">
                <thead>
                <tr>
                    <th>游戏公告内容</th>
                </tr>
                </thead>
                <tbody id="notice">
                </tbody>
            </table>
        </div>
    </div>
</div>
</body>
</html>

```
### main.css
```css
body {
    background-color: #f5f5f5;
}

#main-content {
    max-width: 940px;
    padding: 2em 3em;
    margin: 0 auto 20px;
    background-color: #fff;
    border: 1px solid #e5e5e5;
    -webkit-border-radius: 5px;
    -moz-border-radius: 5px;
    border-radius: 5px;
}
```