package omicron;

import processing.core.PVector;
import java.nio.ByteBuffer;

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

public class Event
{
  public int sourceID; // Device IDs (touch ids, mocap joints, etc.)
  public int deviceTag; // Used for pointer to denote types (Mouse, touch) usually -1
  public int flags; // Used mostly for Wand/Controller to denote button ID
            // during down/up events
  public OmicronAPI.ServiceType serviceType = OmicronAPI.ServiceType.Generic; // Pointer, mocap, voice, etc.
  public OmicronAPI.Type eventType = OmicronAPI.Type.Null; // Event type i.e. click, move, etc.
  
  public int extraDataSize = 1024;
  
  public OmicronAPI.ExtraDataType extraDataType = OmicronAPI.ExtraDataType.ExtraDataNull;
  public int extraDataItems = 0;
  public int extraDataMask;
  public byte[] extraData = new byte[extraDataSize];
  
  int dataArraySize = 0;
  
  // Data array types
  public float[] dataArray;
  public PVector[] vectorArray;
  public String[] stringArray; // Legacy Omicron data
  public String stringData; // Current Omicron data

  public float[] position;
  public float[] orientation;
  public float timestamp;

  public Event()
  {
    position = new float[] { 0, 0, 0 };
    orientation = new float[] { 0, 0, 0, 0 };
  }
  
  public Event(byte[] data)
  {
    ByteBuffer byteBuffer = ByteBuffer.allocate(data.length);
    
    byteBuffer.put(data); // Load byte data into buffer
    byteBuffer.position(0); // Start at beginning of buffer
    
    // Bytes are in reverse order
    timestamp = Integer.reverseBytes(byteBuffer.getInt());
    sourceID = Integer.reverseBytes(byteBuffer.getInt());
    deviceTag = Integer.reverseBytes(byteBuffer.getInt());
    
    byteBuffer.position(12); // Fix offset due to C++/Java conflict
    
    serviceType = OmicronAPI.ServiceType.values()[Integer.valueOf( Integer.reverseBytes(byteBuffer.getInt()) )];
    byteBuffer.position(16); // Fix offset due to C++/Java conflict
    
    eventType = OmicronAPI.Type.values()[Integer.valueOf( Integer.reverseBytes(byteBuffer.getInt()) )];
    flags = Integer.reverseBytes(byteBuffer.getInt());
    
    // Again, reverse bytes and then convert to float correcting precision
    float xPos = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float yPos = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float zPos = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float wRot = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float xRot = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float yRot = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    float zRot = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
    
    position = new float[] { xPos, yPos, zPos };
    orientation = new float[] { xRot, yRot, zRot, wRot };
    
    OmicronAPI.ExtraDataType extraDataType = OmicronAPI.ExtraDataType.values()[Integer.valueOf( Integer.reverseBytes(byteBuffer.getInt()) )];
    extraDataItems = Integer.reverseBytes(byteBuffer.getInt());
    extraDataMask = Integer.reverseBytes(byteBuffer.getInt());
    
    dataArray = new float[extraDataItems];
    dataArraySize = extraDataItems;
    
    byteBuffer.position(64); // Fix offset due to C++/Java conflict
    if( extraDataType == OmicronAPI.ExtraDataType.ExtraDataFloatArray )
    {
      for( int i = 0; i < extraDataItems; i++ )
      {
        dataArray[i] = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
      }
    }
    else if( extraDataType == OmicronAPI.ExtraDataType.ExtraDataIntArray )
    {
      for( int i = 0; i < extraDataItems; i++ )
      {
        dataArray[i] = Integer.reverseBytes(byteBuffer.getInt());
      }
    }
    else if( extraDataType == OmicronAPI.ExtraDataType.ExtraDataVector3Array )
    {
      vectorArray = new PVector[extraDataItems];
      
      for( int i = 0; i < extraDataItems; i++ )
      {
        PVector vec = new PVector();
        vec.x = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
        vec.y = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
        vec.z = Float.intBitsToFloat( Integer.reverseBytes(byteBuffer.getInt()) );
        
        vectorArray[i] = vec;
      }
    }
    else if( extraDataType == OmicronAPI.ExtraDataType.ExtraDataString )
    {
      String byteSting = new String(byteBuffer.array() ); // Convert the entire buffer to a string
      byteSting = byteSting.substring(byteBuffer.position()); // Parse out the non-extra data part of the buffer
      stringData = byteSting;
    }
  }
  
  public float getXPos()
  {
    if (position != null)
      return position[0];
    else
      return -1;
  }

  public float getYPos()
  {
    if (position != null)
      return position[1];
    else
      return -1;
  }

  public float getZPos()
  {
    if (position != null)
      return position[2];
    else
      return -1;
  }

  public OmicronAPI.ServiceType getServiceType()
  {
    return serviceType;
  }

  public int getSourceID()
  {
    return sourceID;
  }
  
  public int getServiceID()
  {
    return deviceTag;
  }
  
  public int getDeviceTag()
  {
    return deviceTag;
  }
  
  public OmicronAPI.Type getEventType()
  {
    return eventType;
  }

  public void setTimeStamp(float ts)
  {
    timestamp = ts;
  }

  public long getTimeStamp()
  {
    return (long) timestamp;
  }

  public float getFloatData(int index)
  {
    dataArraySize = dataArray.length;
    if (dataArraySize > index && dataArray != null )
      return (float) dataArray[index];
    else
      return -1;
  }

  public int getIntData(int index)
  {
    dataArraySize = dataArray.length;
    if (dataArraySize > index && dataArray != null )
      return (int) dataArray[index];
    else
      return -1;
  }

  public String getStringData(int index)
  {
    dataArraySize = dataArray.length;
    if (dataArraySize > index && stringArray != null )
      return stringArray[index];
    else
      return "";
  }
  
  public String getStringData()
  {
    return stringData;
  }
  
  public int getFlags()
  {
    return flags;
  }
  
  public String toString()
  {
    String output = "Omicron Packet:";
    output += "\n" + ("   Timestamp: " + (long)timestamp);
    output += "\n" + ("   SourceID: " + sourceID);
    output += "\n" + ("   DeviceTag: " + deviceTag);
    output += "\n" + ("   ServiceType: " + serviceType + "(" + serviceType.ordinal() + ")");
    output += "\n" + ("   Type: " + eventType + "(" + eventType.ordinal() + ")");
    output += "\n" + ("   Flags: " + flags);
    output += "\n" + ("   Position (x,y,z): " + getXPos() + " " + getYPos() + " " + getZPos());
    output += "\n" + ("   Orientation (x,y,z,w): " + orientation[0] + " " + orientation[1] + " " + orientation[2] + " " + orientation[3]);
    output += "\n" + ("   ExtraDataType: " + extraDataType + "(" + extraDataType.ordinal() + ")");
    output += "\n" + ("   ExtraDataItems: " + extraDataItems);
    output += "\n" + ("   ExtraDataMask: " + extraDataMask);
    for(int i = 0; i < extraDataItems; i++)
    {
      if(serviceType == OmicronAPI.ServiceType.Pointer)
      {
        switch(i)
        {
          case(0): output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (xWidth)"); break;
          case(1): output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (yWidth)"); break;
          case(2): output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (initX)"); break;
          case(3): output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (initY)"); break;
          case(4): output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (EventType)"); break;
          case(5): if(eventType == OmicronAPI.Type.Zoom)
                   {
                     output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (ZoomDelta)");
                   }
                   else
                   {
                     output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + " (Child Touch Count)");
                   }
                   break;
          default:
            output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + "");
        }
      }
      else
      {
        output += "\n" + ("   ExtraData["+i+"]: " + dataArray[i] + "");
      }
    }
    output += "\n";
    return output;
  }
}