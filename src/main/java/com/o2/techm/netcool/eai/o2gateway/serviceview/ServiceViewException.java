package com.o2.techm.netcool.eai.o2gateway.serviceview;


public class ServiceViewException extends Exception
{
   /**
    default constructor.
    Calls the superclass (OSSException) constructor.
    */
   public ServiceViewException() 
   {
		super();
   }
   
   /**
    Calls the superclass (OSSException) constructor and passes the message to it.
    */
   public ServiceViewException(String message) 
   {
		super(message);
   }
   
   /**
    Calls the superclass (OSSException) constructor and passes the Throwable object to it.
    */
   public ServiceViewException(Throwable cause) 
   {
		super(cause);
   }
   
   public ServiceViewException(String message, Throwable cause) 
   {
		super(message, cause);
   }
   
   public ServiceViewException(Throwable cause, String message) 
   {
		super(message, cause);
   }
}
