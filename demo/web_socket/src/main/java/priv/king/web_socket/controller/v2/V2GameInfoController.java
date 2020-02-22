package priv.king.web_socket.controller.v2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import priv.king.web_socket.model.InMessage;
import priv.king.web_socket.service.WebSocketService;


@Controller
public class V2GameInfoController {


	@Autowired
	private WebSocketService ws;
	
	
	@MessageMapping("/v2/chat")
	public void gameInfo(InMessage message) throws InterruptedException{
		
		ws.sendTopicMessage("/topic/game_rank",message);
	}


	
	

}



