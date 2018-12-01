package org.rb.mtable2.controllers.helper;

public class StatusInfo {

   private StatusType type;
   private String message;
   
   
public StatusInfo(StatusType type, String message) {
	
	this.type = type;
	this.message = message;
}


public StatusType getType() {
	return type;
}


public void setType(StatusType type) {
	this.type = type;
}


public String getMessage() {
	return message;
}


public void setMessage(String message) {
	this.message = message;
}
   
   
	
}
