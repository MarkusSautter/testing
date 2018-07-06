package startServices;

import org.json.JSONObject;

import eventprocessing.agent.AbstractAgent;
import eventprocessing.agent.DocProposal.DocProposal;
import eventprocessing.agent.GuiAgent.GuiAgent;
import eventprocessing.agent.SessionEnd.SessionState;
import eventprocessing.agent.UserInteraction.UserInteraction;
import eventprocessing.consume.kafka.ConsumerSettings;
import eventprocessing.consume.spark.streaming.NoValidAgentException;
import eventprocessing.consume.spark.streaming.StreamingExecution;
import eventprocessing.event.AbstractEvent;
import eventprocessing.event.Property;
import eventprocessing.produce.kafka.Despatcher;
import eventprocessing.produce.kafka.ProducerSettings;
import eventprocessing.utils.factory.AbstractFactory;
import eventprocessing.utils.factory.FactoryProducer;
import eventprocessing.utils.factory.FactoryValues;
import eventprocessing.utils.mapping.MessageMapper;
import socketHandler.SocketServer;


/**
 * Startpunkt der Anwendung.
 * 
 * hier werden die Agenten initialisiert und die Sparkumgebung ausgefÃ¼hrt.
 * 
 * @author RobertRapp
 *
 */
public class StartServicesDocProposals {


		
		 // FÃ¼r die Versendung der DemoEvents an das Topic nötig.
	private static Despatcher despatcher = null;
	// wandelt die Events in Nachrichten um.
	private static final MessageMapper messageMapper = new MessageMapper();
	private static AbstractFactory eventFactory = FactoryProducer.getFactory(FactoryValues.INSTANCE.getEventFactory());
	private static AbstractFactory agentFactory = FactoryProducer.getFactory(FactoryValues.INSTANCE.getAgentFactory());
	
	
	public static void main(String[] args) throws NoValidAgentException, InterruptedException
	 {
		/*
		despatcher = new Despatcher(new ProducerSettings("10.142.0.2","9092"));
		AbstractAgent GuiAgent = new GuiAgent();
		
		GuiAgent.setConsumerSettings(new ConsumerSettings("10.142.0.2","9092", "Gui"));
		GuiAgent.setProducerSettings(new ProducerSettings("10.142.0.2","9092"));
		*/	
		
		despatcher = new Despatcher(new ProducerSettings("localhost","9092"));
		
		AbstractAgent DocProposal = new DocProposal();
		DocProposal.setConsumerSettings(new ConsumerSettings("localhost","9092", "DocProposal"));
		DocProposal.setProducerSettings(new ProducerSettings("localhost","9092"));

		AbstractAgent Gui = new GuiAgent();
		Gui.setConsumerSettings(new ConsumerSettings("localhost","9092", "Gui"));
		Gui.setProducerSettings(new ProducerSettings("localhost","9092"));
		
		AbstractAgent userInteraction = new UserInteraction();
		userInteraction.setConsumerSettings(new ConsumerSettings("localhost","9092", "UserInteraction"));
		userInteraction.setProducerSettings(new ProducerSettings("localhost","9092"));
		
		AbstractAgent session = new SessionState();
		session.setConsumerSettings(new ConsumerSettings("localhost","9092", "SessionState"));
		session.setProducerSettings(new ProducerSettings("localhost","9092"));

	
		//StreamingExecution.add(activityService);
		//StreamingExecution.add(protocolService);
		StreamingExecution.add(DocProposal);
		StreamingExecution.add(Gui);
		//StreamingExecution.add(userInteraction);
		//StreamingExecution.add(session);

		
		Runnable myRunnable = new Runnable() {
			public void run() {
				try {
					StreamingExecution.start();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} 
			}
		};
		Runnable webSocketserver = new Runnable() {
			public void run() {
				SocketServer.main(null); 
				
				
			}
		};
		Runnable dritterthread = new Runnable() {
			public void run() {
				try {
					publishDemoEvents();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
		
		// Thread wird erzeugt und gestartet
		Thread thread = new Thread(myRunnable);
		Thread thread2 = new Thread(webSocketserver);
		Thread thread3 = new Thread(dritterthread);
		
		thread.start();
		thread2.start();
		thread3.start();
		
	}

	
	private static void publish(AbstractEvent event, String topic) {
		
		String message = messageMapper.toJSON(event);	
		if(message != null && topic != null) {
			despatcher.deliver(message, topic);	
		}
		
	}

	
	private static void publishDemoEvents() throws InterruptedException {		
			
			for (int i = 0; i < 20; i++) {
				
				
				AbstractEvent event = eventFactory.createEvent("AtomicEvent");
				event.setType("DocProposalEvent");
				JSONObject js = new JSONObject("{ \"head\": { \"vars\": [ \"Name\" , \"FileURL\" ] } , \"results\": { \"bindings\": [ { \"Name\": { \"type\": \"literal\" , \"value\": \"costplan\" } , \"FileURL\": { \"type\": \"literal\" , \"value\": \"https://drive.googledasdasdasdo3_iYxXJiXfXq6jYoLl8H4Y\" } } ] } }");
				Property<String> document = new Property<String>("document", js.toString());
				Thread.sleep(1000);	
				event.add(document);
				publish(event,"DocProposal");
			}
/*			
for (int i = 0; i < 3; i++) {
				
				
				AbstractEvent event = eventFactory.createEvent("AtomicEvent");
				event.setType("UserInteractionEvent");
				Property<String> doc = new Property<String>("document", "Document1");
				Property<String> user = new Property<String>("user", "Schoppel");
				//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
				event.add(doc);		
				event.add(user);
				
				publish(event,"Gui");
				
			
				Thread.sleep(1000);
				
			}

for (int i = 0; i < 3; i++) {
	
	
	AbstractEvent event = eventFactory.createEvent("AtomicEvent");
	event.setType("SessionEndEvent");
	Property<String> session = new Property<String>("Session", "Session324324");
	Property<String> user = new Property<String>("user", "Markus");
	//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
	event.add(session);	
	event.add(user);
	
	publish(event,"Gui");
	

	Thread.sleep(1000);
	
}

for (int i = 0; i < 3; i++) {
	
	
	AbstractEvent event = eventFactory.createEvent("AtomicEvent");
	event.setType("SessionStartEvent");
	Property<String> session = new Property<String>("Session", "Session324324");
	Property<String> user = new Property<String>("user", "Markus");
	//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
	event.add(session);	
	event.add(user);
	
	publish(event,"Gui");
	

	Thread.sleep(1000);
	
}
	*/		
				/*
				 * Property<String> type = new Property<String>("type", "Word");
				 
				Property<String> path = new Property<String>("path", "https://drive.google.com/open?id=1QFKrdAlyiL9kJC5Tof4_T1J0w4bu0Du8BFYq8_-ZaXM");
				Property<String> lastEditor = new Property<String>("lastEditor", "Manfred");
				Property<String> lastEdit = new Property<String>("lastEdit", "26.06.2018");
				Property<Long> docProposalID = new Property<Long>("docProposalID", 6471143L);
				Property<String> category = new Property<String>("category", "Analysis");
				//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
				event.add(name);			
				event.add(type);			
				event.add(path);			
				event.add(lastEditor);
				event.add(lastEdit);
				event.add(docProposalID);
				event.add(category);
			//	event.add(timereference);	
				
				publish(event,"DocProposal");
				
			/*	if( i == 10) {
					Property<String> context = new Property<String>("contextupdate", "Das Token ändert den Kontext");
					event.add(context);
				}
				publish(event,"Tokens");
				
				java.util.logging.Logger logger = LoggerFactory.getLogger("StartServices!");				
				logger.log(Level.WARNING, "SESSIONSTATE AUF SESSIONSTATE GEPUSHT");
				*/
//				AbstractEvent event2 = eventFactory.createEvent("AtomicEvent");
//				event2.setType("SpeedEvent");
//				Property<String> repo = new Property<String>("REPORT", "EVENT GEHT INS DIAGNOSIS IP");
//				event2.add(repo);				
//				publish(event2,"SessionState");					
//				logger.log(Level.WARNING, "SESSIONSTATE AUF SESSIONSTATE GEPUSHT");				
/*				
							}
			
for (int i = 0; i < 3; i++) {
				
				
				AbstractEvent event = eventFactory.createEvent("AtomicEvent");
				event.setType("UserInteractionEvent");
				Property<String> doc = new Property<String>("document", "Document1");
				//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
				event.add(doc);			
				
				publish(event,"Gui");
				
			
				Thread.sleep(1000);
				
			}

for (int i = 0; i < 3; i++) {
	
	
	AbstractEvent event = eventFactory.createEvent("AtomicEvent");
	event.setType("SessionEndEvent");
	Property<String> session = new Property<String>("Session", "Session324324");
	//Property<TimeReference> timereference = new Property<TimeReference>("timereference", TimeReference.INSTANCE);
	event.add(session);			
	
	publish(event,"Gui");
	

	Thread.sleep(1000);
	
}
			

			/*
			TokenEvent event3 = (TokenEvent) new TokenEvent();
			event.setSessionID("2");
			Property<Long> sessionStart = new Property<Long>("sessionStart", System.currentTimeMillis());
			event3.add(sessionStart);
			int zaehler = 5;
			for(int i = 0 ; i < zaehler; i++) {
				Thread.sleep(ShowcaseValues.INSTANCE.getThreadSleep());
				publish(event3,"test");
				Logger l = LoggerFactory.getLogger("PUBLISHDEMOEVENTS");
				l.log(Level.WARNING, "Event wurde direkt durch Dispatcher auf Test gepusht");
			}
			
					
			publish(event3,"test");
			Logger l = LoggerFactory.getLogger("PUBLISHDEMOEVENTS");
			l.log(Level.WARNING, "Event wurde direkt durch Dispatcher auf Test gepusht");
			}
			
			*/
	}	 
	
}