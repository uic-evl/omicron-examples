import omicron.Event;
import omicron.OmicronListener;

class EventListener implements OmicronListener{
 
  // This is called on every Omicron event
  public void onEvent( Event e ){
    //if (e.getEventType() != OmicronAPI.Type.Move && e.getEventType() != OmicronAPI.Type.Zoom)
    //{
    //  println(e);
    //}
    EventViewer.onEventApp(e);
  }// OnEvent
  
}// EventListener