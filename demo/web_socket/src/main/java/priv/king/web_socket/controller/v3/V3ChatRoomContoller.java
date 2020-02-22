package priv.king.web_socket.controller.v3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import priv.king.web_socket.model.InMessage;
import priv.king.web_socket.service.WebSocketService;


/**
 * 
 * 功能描述：简单版单人聊天
 *
 * <p> 创建时间：Jan 4, 2018 </p> 
 * <p> 贡献者：小D学院, 官网：www.xdclass.net </p>
 *
 * @author <a href="mailto:xd@xdclass.net">小D老师</a>
 * @since 0.0.1
 */
@Controller
public class V3ChatRoomContoller {

	@Autowired
	private WebSocketService ws;
	
	
	@MessageMapping("/v3/single/chat")
	public void singleChat(InMessage message) {
		
		ws.sendChatMessage(message);
		
		
	}
	
	
	
}
