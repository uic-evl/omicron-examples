import omicron.Event;
import omicron.OmicronAPI;
import omicron.OmicronListener;
import processing.core.*;

public class EventViewer {
	
	static OmicronAPI omicron;
	static OmicronListener eventListener;
	  
	public static void main(String[] args) {
		PApplet applet = new PApplet();
		omicron = new OmicronAPI(applet);
		eventListener = new EventListener();
		omicron.setEventListener(eventListener);
		  
		omicron.ConnectToServer(7124, 28000, "127.0.0.1");
		while(true)
		{
			omicron.process();
		}
	}
	
	public static void onEventApp(Event e)
	{
		System.out.println(e);
	}
}
