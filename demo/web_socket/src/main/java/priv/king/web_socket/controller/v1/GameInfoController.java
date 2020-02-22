package priv.king.web_socket.controller.v1;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import priv.king.web_socket.model.InMessage;
import priv.king.web_socket.model.OutMessage;

@Controller
public class GameInfoController {

    @MessageMapping("/v1/chat")
    @SendTo("/topic/game_chat")
    public OutMessage gameInfo(InMessage message){

        return new OutMessage(message.getContent());
    }
}
