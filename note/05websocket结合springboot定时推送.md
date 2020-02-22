websocket结合springboot定时推送
===
* websocket结合springboot的注解Scheduled实现定时推送
* 在controller的类方法上标注 @Scheduled(fixedRate = 3000) 表示这个方法会定时执行fixedRate表示是多少毫秒 3000就3秒
* 需要在springboot启动类上@EnableScheduling
* 被注解@Scheduled标记的方法，是不能有参数，不然会报错

## @Scheduled 由Spring定义，用于将方法设置为调度任务。如：方法每隔十秒钟被执行、方法在固定时间点被执行等
* @Scheduled(fixedDelay = 1000)
    * 上一个任务结束到下一个任务开始的时间间隔为固定的1秒，任务的执行总是要先等到上一个任务的执行结束
* @Scheduled(fixedRate = 1000)
    * 每间隔1秒钟就会执行任务（如果任务执行的时间超过1秒，则下一个任务在上一个任务结束之后立即执行）
* @Scheduled(fixedDelay = 1000, initialDelay = 2000)
    * 第一次执行的任务将会延迟2秒钟后才会启动
* @Scheduled(cron = “0 15 10 15 * ?”)
    * Cron表达式，每个月的15号上午10点15开始执行任务
    
## Demo
```java
@Controller
public class V4ServerInfoController {

	@Autowired
	private WebSocketService ws;
	
	
	@MessageMapping("/v4/schedule/push")
	@Scheduled(fixedRate = 3000)  //方法不能加参数 
	public void sendServerInfo(){
		ws.sendServerInfo();
	}
	
	
}
```
```java
public void sendServerInfo() {

    int processors = Runtime.getRuntime().availableProcessors();

    Long freeMem = Runtime.getRuntime().freeMemory();

    Long maxMem = Runtime.getRuntime().maxMemory();

    String message = String.format("服务器可用处理器:%s; 虚拟机空闲内容大小: %s; 最大内存大小: %s", processors,freeMem,maxMem );

    template.convertAndSend("/topic/server_info",new OutMessage(message));

}
```
```java
@SpringBootApplication
@EnableScheduling
public class Application {

	public static void main(String [] args){
		SpringApplication.run(Application.class);
	}
	
	
}
```
