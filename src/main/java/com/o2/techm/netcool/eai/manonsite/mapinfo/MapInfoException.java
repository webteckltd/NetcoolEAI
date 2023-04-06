package com.o2.techm.netcool.eai.manonsite.mapinfo;


public class MapInfoException extends Exception
{
   /**
    default constructor.
    */
   public MapInfoException()
   {
		super();
   }

   /**
    Calls the superclass (OSSException) constructor and passes the message to it.
    */
   public MapInfoException(String message)
   {
		super(message);
   }

   /**
    Calls the superclass (OSSException) constructor and passes the Throwable object to it.
    */
   public MapInfoException(Throwable cause)
   {
		super(cause);
   }

}
