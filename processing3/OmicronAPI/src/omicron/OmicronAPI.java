package omicron;
import hypermedia.net.*;
import processing.core.*;
import processing.net.*;

/**************************************************************************************************
 * THE OMICRON PROJECT
 *-------------------------------------------------------------------------------------------------
 * Copyright 2010-2019    Electronic Visualization Laboratory, University of Illinois at Chicago
 * Authors:                    
 *  Arthur Nishimoto            arthur.nishimoto@gmail.com
 *-------------------------------------------------------------------------------------------------
 * Copyright (c) 2010-2019, Electronic Visualization Laboratory, University of Illinois at Chicago
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this list of conditions 
 * and the following disclaimer. Redistributions in binary form must reproduce the above copyright 
 * notice, this list of conditions and the following disclaimer in the documentation and/or other 
 * materials provided with the distribution. 
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE  GOODS OR SERVICES; LOSS OF 
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN 
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *************************************************************************************************/

public class OmicronAPI {
  public enum ServiceType
  {
    Pointer, Mocap, Keyboard, Controller, UI, Generic, Brain, Wand, Audio, Speech, Kinect
  };

  public enum Type
  {
    Select, Toggle, ChangeValue, Update, Move, Down, Up, Trace, Untrace, Click, DoubleClick, MoveLeft, MoveRight, MoveUp, MoveDown, Zoom, SplitStart, SplitEnd, Split, RotateStart, RotateEnd, Rotate, Null
  };
  
  public enum ExtraDataType
  {
    ExtraDataNull, ExtraDataFloatArray, ExtraDataIntArray, ExtraDataVector3Array, ExtraDataString
  };
  
  // enum ClientFlags
  public final static int DataIn = 1 << 0;
  public final static int ServiceTypePointer = 1 << 1;
  public final static int ServiceTypeMocap = 1 << 2;
  public final static int ServiceTypeKeyboard = 1 << 3;
  public final static int ServiceTypeController = 1 << 4;
  public final static int ServiceTypeUi = 1 << 5;
  public final static int ServiceTypeGeneric = 1 << 6;
  public final static int ServiceTypeBrain = 1 << 7;
  public final static int ServiceTypeWand = 1 << 8;
  public final static int ServiceTypeSpeech = 1 << 9;
  public final static int ServiceTypeImage = 1 << 10;
  public final static int AlwaysTCP = 1 << 11;
  public final static int AlwaysUDP = 1 << 12;
  public final static int ServiceTypeAudio = 1 << 13;
  
  final static int DEFAULT_BUFLEN = 1024;
  
  Client msgClient;
  UDP dataClient;
  
  PApplet applet;
  OmicronListener eventListener;
  
  byte[] msgByteBuffer = new byte[DEFAULT_BUFLEN];
  
  public OmicronAPI(PApplet parent) {
	    applet = parent;
	  }
  
  public OmicronAPI(PApplet parent, int dataPort, int msgPort, String ipAddress) {
    applet = parent;
    ConnectToServer(dataPort, msgPort, ipAddress);
  }

  public OmicronAPI(PApplet parent, int dataPort, int msgPort, String ipAddress, int flags) {
    applet = parent;
    ConnectToServer(dataPort, msgPort, ipAddress, flags);
  }
  
  public void ConnectToServer(int dataPort, int msgPort, String ipAddress)
  {
	msgClient = new Client(applet, ipAddress, msgPort);
    
    dataClient = new UDP(this, dataPort, ipAddress);
    dataClient.setReceiveHandler("receiveUDP");
    dataClient.listen(true);

    sendHandshake(dataPort, GetDefaultFlag());
  }
  
  public void ConnectToServer(int dataPort, int msgPort, String ipAddress, int flags)
  {
	msgClient = new Client(applet, ipAddress, msgPort);
    
    dataClient = new UDP(this, dataPort, ipAddress);
    dataClient.setReceiveHandler("receiveUDP");
    dataClient.listen(true);

    sendHandshake(dataPort, flags);
  }
  
  public void process()
  {
    receiveTCP();
  }
  
  private void sendHandshake(int dataPort, int flags)
  {
    String handshake = "omicronV3_data_on," + dataPort + "," + flags;
    byte[] handshakeBytes = handshake.getBytes();
    byte[] handshakePacket = new byte[DEFAULT_BUFLEN];

    System.arraycopy(handshakeBytes, 0, handshakePacket, 0, handshakeBytes.length);
    
    msgClient.write(handshakePacket);
  }
  
  private int GetDefaultFlag()
  {
  	int flag = 0;
  	flag |= ServiceTypePointer;
  	flag |= ServiceTypeMocap;
  	flag |= ServiceTypeKeyboard;
  	flag |= ServiceTypeController;
  	flag |= ServiceTypeUi;
  	flag |= ServiceTypeGeneric;
  	flag |= ServiceTypeBrain;
  	flag |= ServiceTypeWand;
  	flag |= ServiceTypeSpeech;
  	// flag |= ServiceTypeImage;
  	flag |= ServiceTypeAudio;
  	return flag;
  }
  
  public void receiveUDP(byte[] data)
  {
    Event event = new Event(data);
    if (eventListener != null)
    {
      eventListener.onEvent(event);
    }
  }
  
  public void receiveTCP()
  {
    
    if (msgClient.available() > 0) { 
      int byteCount = msgClient.readBytes(msgByteBuffer); 
      if (byteCount > 0 ) {
        Event event = new Event(msgByteBuffer);
        if (eventListener != null)
        {
          eventListener.onEvent(event);
        }
      } 
    }
  }
  
  public void setEventListener(OmicronListener listener)
  {
    eventListener = listener;
  }
}
