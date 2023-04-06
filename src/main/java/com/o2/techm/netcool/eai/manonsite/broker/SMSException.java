package com.o2.techm.netcool.eai.manonsite.broker;


public class SMSException extends Exception
{

    /**
     * 
     */
    public SMSException()
    {
        super();
    }

    /**
     * @param message
     */
    public SMSException(String message)
    {
        super(message);
    }

    /**
     * @param cause
     */
    public SMSException(Throwable cause)
    {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public SMSException(String message, Throwable cause)
    {
        super(message, cause);
    }

}
