/**
 * ---------------------------------------------
 * OmicronAPI_Tester.pde
 * Description: A testing application for Omicron events. Specfically for
 *              Pointer events on a touch wall
 *
 * Class: 
 * System: Processing 3.5.3, Windows 10 x64
 * Author: Arthur Nishimoto
 * Version: 3.0
 *
 * Version Notes:
 * 6/14/12     - Initial version
 * 6/18/12     - Added example for fullscreen, scaling, and touch
 * 6/20/12     - Cleaned up example
 * 9/20/12     - Updated for 2.0b3
 * 6/21/18     - Updated/cleaned up for Processing 3 (v2.0)
 * 3/15/19     - Updated for Omicron v3 protocol
 * ---------------------------------------------
 */

import omicron.*;
import omicron.Event;
import processing.net.*;
import hypermedia.net.*;

OmicronAPI omicron;
EventListener eventListener;

ArrayList eventList = new ArrayList();

enum DrawMode {EventBlob, IDColor, Gesture};

DrawMode drawMode = DrawMode.EventBlob;

boolean gestureDetected = false;

void setup()
{
  omicron = new OmicronAPI(this, 7817, 28000, "localhost");
  
  eventListener = new EventListener();
  omicron.setEventListener(eventListener);
  
  //size(1920, 1080);
  fullScreen(P3D);
  background(10);
}

void draw()
{
  if(drawMode == DrawMode.Gesture)
  {
    clearScreen();
  }
  
  int textSize = 16;
  int textLines = 3;
  textSize(textSize);
  fill(50);
  rect(0, 0, 200, textSize * 1.2 * textLines);
  
  fill(255);
  text("FPS: " + frameRate, 5, textSize);
  text("Draw Mode: " + drawMode, 5, textSize * 2);
  
  if(gestureDetected)
  {
    text("Gesture Detected", 5, textSize * 3);
  }
  
  omicron.process();
  
  for(int i = 0; i < eventList.size(); i++)
  {
    Event e = (Event)eventList.get(i);
    //println(e);
    
    int ID = e.getSourceID();
    OmicronAPI.Type eventType = e.getEventType();
    OmicronAPI.ServiceType serviceType = e.getServiceType();
    
    if(serviceType == OmicronAPI.ServiceType.Pointer)
    {
      float xPos = e.getXPos() * width;
      float yPos = e.getYPos() * height;
      float xWidth = e.getFloatData(0) * width;
      float yWidth = e.getFloatData(1) * height;
      
      int flags = e.getFlags();
      if(flags > 0)
      {
        gestureDetected = true;
      }

      if(drawMode == DrawMode.EventBlob)
      {
        colorByEvent(xPos, yPos, xWidth, yWidth, eventType);
      }
      else if(drawMode == DrawMode.IDColor)
      {
        colorByID(xPos, yPos, xWidth, yWidth, ID);
      }
      else if(drawMode == DrawMode.Gesture)
      {
        drawGesture(e);
      }
    }
  }
  eventList.clear();
}

void onEventApp(Event e)
{
  eventList.add(e);
}

void mouseDragged() {
  fill(210, 200, 210);
  noStroke();
  ellipse(mouseX, mouseY, 10, 10);
}

void keyPressed()
{
  if( key == 'c' )
  {
    clearScreen();
  }
  if( key == 'm' )
  {
    if(drawMode == DrawMode.EventBlob)
    {
      drawMode = DrawMode.IDColor;
    }
    else if(drawMode == DrawMode.IDColor)
    {
      drawMode = DrawMode.Gesture;
    }
    else
    {
      drawMode = DrawMode.EventBlob;
    }
  }
}

void clearScreen()
{
  background(0);
}
