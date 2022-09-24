LoRa_Send_Reciever.ino - Communication code that handles the input from a user, creating the packets, and sending them to another LoRa module.  Also, handles receiving packets and displaying the message.

-----------------------------------Sending-----------------------------------

Input Management:
	Currently, input is taken in the Serial Monitor within Arduino.  This will be changed to take an input within an Android app. sent via bluetooth.

Packet Handling:
	All messages will have their size checked due to the LoRa module being unreliable when sending a packet with a message larger then 70 bytes.  Depending on the size of the message, two events could happen.  Either the message is small enough to be sent in a single packet, or the message will be larger and need to be divided up into smaller messages and sent in multiple packets.

	Message is smaller than 60 bytes:
		If the message is smaller than 60 bytes, go ahead and put it into a single packet and send it.

	Message is larger than 60 bytes:
		If the message is larger than 60 bytes, split the message into different parts and send them in their own packet.  The first packet will always be 1 byte, which will be the first char of the input. 

		ex: input = "Hello, I would like to talk to you about your car's extended warranty. Do you have the time to do so?"
		
		Packet 1 will contain "H"

		The next packet will contain 30 bytes of the message after the first letter.

		Packet 2 will contain "ello, I would like to talk to "
		
		This will continue for the rest of the message. (The last packet containing the input can be less than 30 bytes)

		The last packet sent will contain "posty", this is a flag in the reciever code to signify that no more packets holding the input message are going to be sent.


Packet Creation:
	Packet creation is handled by the heltec.h library.

-----------------------------------Receiving-----------------------------------

Recieving the Packet:
	First, we check the size of the packet, if its 1, then its a large input so multiple packets will be accepted to create the whole message.  So we set a flag to allow for that.  if not, we check the size, if no size then no packet has been received.  else we send the packet and its size to another function.

Parsing the packet:
	Once the packet is sent to the cbk function, it will check if multiple packets will be needed. if so, it will take the current message within the first packet, put it into a variable then look for the next packet. it will do this until the message "posty" is found in its own packet, signalling that no other packets will be sent containing a part of the message.  each packet before this packet will have had the message extracted from the packet and concatenated with the previous messages.  "posty" will not be concatenated with the message.  it will then send the message to be displayed, empty the packet, then revert the multimessage flag.

