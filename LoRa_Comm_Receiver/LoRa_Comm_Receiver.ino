#include "heltec.h"
#include "images.h"

#define BAND    915E6  //you can set band here directly,e.g. 868E6,915E6

//initialize variables
String rssi = "RSSI --";
String packSize = "--";
String packet = "";
bool multipack = false;
String temp;

//initial logo, subject to deletion
void logo(){
  Heltec.display->clear();
  Heltec.display->drawXbm(0,5,logo_width,logo_height,logo_bits);
  Heltec.display->display();
}

//display data from packet on OLED screen
void LoRaData(){
  Heltec.display->clear();
  Heltec.display->setTextAlignment(TEXT_ALIGN_LEFT);
  Heltec.display->setFont(ArialMT_Plain_10);
  Heltec.display->drawString(0 , 15 , "Received "+ packSize + " bytes");
  Heltec.display->drawStringMaxWidth(0 , 26 , 128, packet);
  Heltec.display->drawString(0, 0, rssi);  
  Heltec.display->display();
}

//get data from packet
void cbk(int packetSize) {
  //flash LED to show the packet recieved
  digitalWrite(25, HIGH);   
  delay(1000);                       
  digitalWrite(25, LOW);    
  delay(1000);

  //check if multiple packets need to be recieved to create the full msg
  if(multipack == true){
    temp ="";
    packSize = String(packetSize,DEC);
    //grab the msg
    for (int i = 0; i < packetSize; i++) { temp += (char) LoRa.read(); }
    rssi = "RSSI " + String(LoRa.packetRssi(), DEC) ;
    //check if the ending flag was sent
    if(temp == "posty"){
      Serial.println(packet);
      //send full msg to be displayed
      LoRaData();
      //clean up
      packet = "";
      multipack = false;
    }
    //if flag was not recieved, add current packets msg to a string
    else{
      packet += temp;  
    }
  }
  //msg is on one packet
  else{
    packet ="";
    packSize = String(packetSize,DEC);
    //grab msg from packet
    for (int i = 0; i < packetSize; i++) { packet += (char) LoRa.read(); }
    rssi = "RSSI " + String(LoRa.packetRssi(), DEC) ;
    //display the msg
    LoRaData();
  }
}

void setup() {
  //activate the LoRa 32
  Heltec.begin(true /*DisplayEnable Enable*/, true /*Heltec.LoRa Disable*/, true /*Serial Enable*/, true /*PABOOST Enable*/, BAND /*long BAND*/);
  Heltec.display->init();
  Heltec.display->flipScreenVertically();  
  Heltec.display->setFont(ArialMT_Plain_10);
  logo();
  delay(1500);
  Heltec.display->clear();

  //display initial text
  Heltec.display->drawString(0, 0, "Heltec.LoRa Initial success!");
  Heltec.display->drawString(0, 10, "Wait for incoming data...");
  Heltec.display->display();
  delay(1000);
  //look for packets being sent
  LoRa.receive();
}

void loop() {
  //get packet size
  int packetSize = LoRa.parsePacket();
  //check if packet size is 1, if so, its a msg that was too big
  //and needed to be split among multiple packets
  if(packetSize == 1){ multipack = true; }
  //send packet to be disected
  if (packetSize) { cbk(packetSize);  }
  delay(10);

}
