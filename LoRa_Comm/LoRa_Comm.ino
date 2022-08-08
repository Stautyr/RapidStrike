#include "heltec.h"
#include "images.h"
#define BAND    915E6  //Change BAND if used in another country. USA: 915E6

char rx_byte = 0;
String rx_str = "";

unsigned int counter = 0;
String rssi = "RSSI --";
String packSize = "--";
String packet ;

//Creates an image when LoRa turns on
//  subject to delete (not neccessary)
void logo()
{
  Heltec.display->clear();
  Heltec.display->drawXbm(0,5,logo_width,logo_height,logo_bits);
  Heltec.display->display();
}

void setup() {
  //Setup launch parameters for the LoRa module
  Heltec.begin(true /*DisplayEnable Enable*/, true /*Heltec.LoRa Disable*/, true /*Serial Enable*/, true /*PABOOST Enable*/, BAND /*long BAND*/);

  //Currently, syncword is off so the LoRa is broadcasting on 0x34
  //LoRa.setSyncWord(0xF3);           // ranges from 0-0xFF, default 0x34

  //Activate the OLED screen
  Heltec.display->init();
  Heltec.display->flipScreenVertically();  
  Heltec.display->setFont(ArialMT_Plain_10);

  //Activate the logo, wait, then clear it
  // subject to delete (not needed)
  logo();
  delay(1500);
  Heltec.display->clear();
  
  //Show that the initialization was completed successfully
  Heltec.display->drawString(0, 0, "Heltec.LoRa Initial success!");
  Heltec.display->display();
  delay(1000);
}

void loop() {
  //clear initializing screen
  Heltec.display->clear();
  Heltec.display->setTextAlignment(TEXT_ALIGN_LEFT);
  Heltec.display->setFont(ArialMT_Plain_10);
  
  //Show what packet # is being sent on the OLED
  Heltec.display->drawString(0, 0, "Sending packet: ");
  Heltec.display->drawString(90, 0, String(counter));
  Heltec.display->display();

  //check if an input string was given
  // if so, concat the chars into a string and pass the msg
  // else, restart the loop
  if(Serial.available() > 0){
    //read the chars
    rx_byte = Serial.read();
    
    //if the current char isn't \n, then concat the chars together
    //else, send the msg
    if (rx_byte != '\n') {
      //a character of the string was received
      rx_str += rx_byte;
    }
    else {
      // send msg once complete
      Serial.print("Sending ");
      Serial.println(rx_str);

      //update the counter due to the msg being sent
      counter++;
      sendMessage(rx_str);

      //clean up
      rx_str = "";                //clear the string for reuse
      Serial.println("==========================================================");
      Serial.println("Please type your message you want to send:");
    } 
  }
}

//LoRa code to send a msg
String sendMessage(String MSG){
  Serial.print("Sending packet: ");
  //begin creating the packet
  LoRa.beginPacket();

  //packet parameters, no need to change at the moment
  LoRa.setTxPower(14,RF_PACONFIG_PASELECT_PABOOST);
  //add the msg to the packet
  LoRa.print(MSG);
  //end the packet
  LoRa.endPacket();

  //flash LED to show the packet sent
  digitalWrite(25, HIGH);   
  delay(1000);                       
  digitalWrite(25, LOW);    
  delay(1000);
}
