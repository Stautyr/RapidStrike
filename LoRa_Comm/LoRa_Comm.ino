#include "heltec.h"
#include "images.h"
#define BAND    915E6  //Change BAND if used in another country. USA: 915E6

//Example of a long message
//Hello, I would like to talk to you about your car's extended warranty. Do you have the time to do so?

//necessary variables
char rx_byte = 0;
String rx_str = "";
int bytecounter = 0;

int leng;
String parsedMSG[25];
int MAXBYTES = 60;

int temp;
String currentbyte;

unsigned int counter = 0;
String rssi = "RSSI --";
String packSize = "--";
String packet ;

//initial logo, subject to deletion
void logo()
{
  Heltec.display->clear();
  Heltec.display->drawXbm(0,5,logo_width,logo_height,logo_bits);
  Heltec.display->display();
}

void setup() {
  //activate LoRa 32 module
  Heltec.begin(true /*DisplayEnable Enable*/, true /*Heltec.LoRa Disable*/, true /*Serial Enable*/, true /*PABOOST Enable*/, BAND /*long BAND*/);

  //activate the OLED display
  Heltec.display->init();
  Heltec.display->flipScreenVertically();  
  Heltec.display->setFont(ArialMT_Plain_10);
  logo();
  delay(1500);
  Heltec.display->clear();
  
  Heltec.display->drawString(0, 0, "Heltec.LoRa Initial success!");
  Heltec.display->display();
  delay(1000);
}

void loop() {
  //clear display and set it up for the program purpose
  Heltec.display->clear();
  Heltec.display->setTextAlignment(TEXT_ALIGN_LEFT);
  Heltec.display->setFont(ArialMT_Plain_10);
  
  Heltec.display->drawString(0, 0, "Sending packet: ");
  Heltec.display->drawString(90, 0, String(counter));
  Heltec.display->display();

  //check for a message given
  if(Serial.available() > 0){
    rx_byte = Serial.read();
     
    if (rx_byte != '\n') {
      //a character of the string was received
      rx_str += rx_byte;
      bytecounter++;
    }
    else {
      Serial.println("your input was: " + rx_str);
      Serial.println("# of bytes from counter = " + String(bytecounter));
      
      //if counter is > 60, split the msg up.  packets get weird after 70 bytes.
      if(bytecounter > MAXBYTES){
        Serial.println("input too big");
        //divide the message into parts
        divMSG(rx_str);

        //send the array conntaing the parsed msg
        sendDivMessage(parsedMSG);
      }
      else{
        //send msg once complete
        Serial.print("Sending ");
        Serial.println(rx_str);
  
        counter++;
        sendMessage(rx_str);
      }
      
      //clean up
      rx_str = "";
      bytecounter = 0;
      Serial.println("==========================================================");
      Serial.println("Please type your message you want to send:");
    } 
  }
}

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

  //flash LED to show the packet sent
  digitalWrite(25, HIGH);   
  delay(1000);                       
  digitalWrite(25, LOW);    
  delay(1000);
}

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
      delay(1000);                       
      digitalWrite(25, LOW);    
      delay(1000);
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

String divMSG(String input) {
  temp = 0;
  currentbyte = "";
  //loop through the total number of bytes
  for(int i = 0; i < bytecounter; i++){
    //concat each current byte into a string
    currentbyte += input.charAt(i);
    //once currentbyte is a 30byte str
    if(i%30 == 0){
      //add it to the array
      parsedMSG[temp] = currentbyte;
      //clear currentbyte
      currentbyte = "";
      //inc temp
      temp++;
    }
    //if the total amount of bytes in the msg left is < 30, add them to the array
    if(bytecounter-i < 30){
      parsedMSG[temp] = currentbyte;  
    }
    
  }
  
}
