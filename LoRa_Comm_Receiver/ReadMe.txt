LoRa_Comm_Receiver.ino - This code handles receiving packets and displaying the message.

Recieving the Packet:
	First, we check the size of the packet, if its 1, then its a large input so multiple packets will be accepted to create the whole message.  So we set a flag to allow for that.  if not, we check the size, if no size then no packet has been received.  else we send the packet and its size to another function.

Parsing the packet:
	Once the packet is sent to the cbk function, it will check if multiple packets will be needed. if so, it will take the current message within the first packet, put it into a variable then look for the next packet. it will do this until the message "posty" is found in its own packet, signalling that no other packets will be sent containing a part of the message.  each packet before this packet will have had the message extracted from the packet and concatenated with the previous messages.  "posty" will not be concatenated with the message.  it will then send the message to be displayed, empty the packet, then revert the multimessage flag.