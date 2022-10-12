#include "heltec.h"
#include "images.h"

#define BAND    915E6  //you can set band here directly,e.g. 868E6,915E6

//Initialize Variables
// Sender Variables
char rx_byte = 0;
String rx_str = "";
int bytecounter = 0;
int leng;
String parsedMSG[25];
int MAXBYTES = 60;
unsigned int counter = 0;
int temp1;
String currentbyte;

// Receiver Variables
String rssi = "RSSI --";
String packSize = "--";
String packet = "";
bool multipack = false;
String temp;

void setup() {
  Heltec.begin(true /*DisplayEnable Enable*/, true /*Heltec.LoRa Disable*/, true /*Serial Enable*/, true /*PABOOST Enable*/, BAND /*long BAND*/);
  LoRa.receive();
}

void loop() {
  //-------------------------==================Reciever Code====================-------------------------
  //get packet size
  int packetSize = LoRa.parsePacket();
  //check if packet size is 1, if so, its a msg that was too big
  //and needed to be split among multiple packets
  //if(packetSize == 1){ multipack = true; }
  //send packet to be disected
  if (packetSize) { cbk(packetSize);  }
  delay(10);
}

//-------------------------==================Sender Functions====================-------------------------
String sendMessage(String MSG){
  Serial.println("Sending packet: ");
  //create the packet
  LoRa.beginPacket();
  //set parameters
  LoRa.setTxPower(14,RF_PACONFIG_PASELECT_PABOOST);
  //add the msg
  LoRa.print(MSG);
  //end the packet and send
  LoRa.endPacket();

  //flash LED twice to show the packet sent
  digitalWrite(25, HIGH);   
  delay(500);                       
  digitalWrite(25, LOW);    
  delay(500);
  digitalWrite(25, HIGH);   
  delay(500);                       
  digitalWrite(25, LOW);    
  delay(500);
}

//Send the divided messages in seperate packets
String sendDivMessage(String* MSG){
  Serial.println("Sending packet multiple packets: ");

  //for all items in the array, send each item in their own packet
  for(int i = 0; i < (sizeof(parsedMSG)/sizeof(parsedMSG[0])); i++){
    //if current item isn't null, send a packet with current item
    if(parsedMSG[i] != NULL){
      LoRa.beginPacket();
      LoRa.setTxPower(14,RF_PACONFIG_PASELECT_PABOOST);
      LoRa.print(parsedMSG[i]);
      LoRa.endPacket();
  
      //flash LED to show the packet sent
      digitalWrite(25, HIGH);   
      delay(250);                       
      digitalWrite(25, LOW);    
      delay(250);
      digitalWrite(25, HIGH);   
      delay(250);                       
      digitalWrite(25, LOW);    
      delay(250);
      digitalWrite(25, HIGH);   
      delay(250);                       
      digitalWrite(25, LOW);    
      delay(250);
    }
    else{
      //if null is found, break the loop
      break;  
    }
  }
  Serial.println("Sending posty: ");
  //send ending packet to signify no more packets are being sent with user msg
  LoRa.beginPacket();
  LoRa.setTxPower(14,RF_PACONFIG_PASELECT_PABOOST);
  LoRa.print("posty");
  LoRa.endPacket();
}

//Divide the current given input
String divMSG(String input) {
  temp1 = 0;
  currentbyte = "";
  //loop through the total number of bytes
  for(int i = 0; i < bytecounter; i++){
    //concat each current byte into a string
    currentbyte += input.charAt(i);
    //once currentbyte is a 30byte str
    if(i%30 == 0){
      //add it to the array
      parsedMSG[temp1] = currentbyte;
      //clear currentbyte
      currentbyte = "";
      //inc temp
      temp1++;
    }
    //if the total amount of bytes in the msg left is < 30, add them to the array
    if(bytecounter-i < 30){
      parsedMSG[temp1] = currentbyte;  
    }
    
  }
  
}
//-------------------------==================Reciever Functions==================-------------------------
//display data from packet on OLED screen
void LoRaData(){
  Heltec.display->clear();
  Heltec.display->setTextAlignment(TEXT_ALIGN_LEFT);
  Heltec.display->setFont(ArialMT_Plain_10);
  Heltec.display->drawString(0 , 15 , "MainNode| Received "+ packSize + " bytes");
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
      Serial.print("multi packet: ");
      Serial.println(packet);
      //send full msg to be displayed
      LoRaData();
      //clean up
      packet = "";
      multipack = false;

      Serial.println("Pushing MSG along (single packet)");
      //divide the message into parts
      divMSG(rx_str);

      //send the array conntaing the parsed msg
      sendDivMessage(parsedMSG);
      
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
    packet += " node1";
    Serial.print("single packet: ");
    Serial.println(packet);
    Serial.println("Pushing MSG along (single packet)");
    sendMessage(packet);
  }
}
