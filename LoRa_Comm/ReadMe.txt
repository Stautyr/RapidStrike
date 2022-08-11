LoRa_Comm.ino - Communication code that handles the input from a user, creating the packets, and sending them to another LoRa module.  


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

