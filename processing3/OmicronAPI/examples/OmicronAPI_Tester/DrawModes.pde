void colorByEvent(float xPos, float yPos, float xWidth, float yWidth, OmicronAPI.Type eventType)
{
  if(eventType == OmicronAPI.Type.Down)
  {
    stroke(210, 20, 10);
  }
  else if(eventType == OmicronAPI.Type.Move)
  {
    noStroke();
  }
  else if(eventType == OmicronAPI.Type.Up)
  {
    stroke(10, 20, 210);
  }
  else
  {
    stroke(210, 200, 210);
  }
  noFill();
  strokeWeight(5);
  ellipse(xPos, yPos, xWidth * 2, yWidth * 2);
  
  fill(10, 200, 10);
  noStroke();
  strokeWeight(1);
  ellipse(xPos, yPos, xWidth, yWidth);
}

void colorByID(float xPos, float yPos, float xWidth, float yWidth, int ID)
{
  fill(getColor(ID));
  noStroke();
  strokeWeight(1);
  ellipse(xPos, yPos, xWidth, yWidth);
}

void drawGesture(Event e)
{
  int ID = e.getSourceID();
  OmicronAPI.Type eventType = e.getEventType();
  OmicronAPI.ServiceType serviceType = e.getServiceType();
  
  float xPos = e.getXPos() * width;
  float yPos = e.getYPos() * height;
  float xWidth = e.getFloatData(0) * width;
  float yWidth = e.getFloatData(1) * height;
  
  float initX = e.getFloatData(2) * width;
  float initY = e.getFloatData(3) * height;
  
  float secondaryEventType = e.getFloatData(4);
  float childCount = e.getFloatData(5);
  
  int flags = e.getFlags();
  
  fill(255);
  stroke(getColor(ID));
  strokeWeight(4);
  ellipse(xPos, yPos, 50, 50);
  
  int textSize = 24;
  textSize(textSize);
  fill(255);
  text("ID: " + ID, xPos - textSize * 3, yPos - textSize * 3);
  
  for(int i = 0; i < childCount; i++)
  {
    int childID = (int)e.getFloatData(6 + i * 3);
    float childX = e.getFloatData(7 + i * 3) * width;
    float childY = e.getFloatData(8 + i * 3) * height;
    
    fill(128);
    stroke(getColor(childID));
    strokeWeight(4);
    ellipse(childX, childY, 20, 20);
    
    stroke(100);
    line(xPos, yPos, childX, childY);
    
    fill(128);
    text("cID: " + childID, childX - textSize * 3, childY - textSize * 3);
  }
}

color getColor(int finger){
  int colorNum = finger % 20;
  color shapeColor = #000000;
  switch (colorNum){
  case 0: 
    shapeColor = #D2691E; break;  //chocolate
  case 1: 
    shapeColor = #FC0FC0; break;  //Shocking pink
  case 2:
    shapeColor = #014421; break;  //Forest green (traditional)
  case 3: 
    shapeColor = #FF4500; break;  //Orange Red
  case 4: 
    shapeColor = #2E8B57; break;  //Sea Green        
  case 5: 
    shapeColor = #B8860B; break;  //Dark Golden Rod
  case 6: 
    shapeColor = #696969; break;  //Dim Gray
  case 7: 
    shapeColor = #7CFC00; break;  //Lawngreen
  case 8: 
    shapeColor = #4B0082; break;  //Indigo
  case 9: 
    shapeColor = #6B8E23; break;  //Olive Drab
  case 10: 
    shapeColor = #5D8AA8; break;  //Air Force Blue
  case 11: 
    shapeColor = #F8F8FF; break;  //Ghost White
  case 12: 
    shapeColor = #0000FF; break;  //Ao
  case 13: 
    shapeColor = #00FFFF; break;  //Aqua
  case 14: 
    shapeColor = #7B1113; break;  //UP Maroon
  case 15: 
    shapeColor = #6D351A; break;  //Auburn
  case 16: 
    shapeColor = #FDEE00; break;  //Aureolin
  case 17: 
    shapeColor = #FF0000; break;  //Red
  case 18:
    shapeColor = #0F4D92; break; //Yale Blue
  case 19:
    shapeColor = #C5B358; break; //Vegas gold
  }

  return shapeColor;
}
