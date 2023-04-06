/*
 * Created on 19-May-2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.o2.techm.netcool.eai.manonsite.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

/**
 * @author aademij1
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SmsErrorCodes {

	static final String ERRORCODESARRAY[][]= 
	{
		{"0","Success","The call was successful"},
		{"2","Quota Exceeded","The call has been rejected as inserting the messages will cause the account�s quota to be exceeded"},
		{"4","Internal network error. Please try again later ","There has been an internal problem with the application"},
		{"5","Invalid Message datastructure","The Message array does not conform to the prescribed structure"},
		{"6","Invalid MSISDN datastructure ","The MSISDN array does not conform to the prescribed structure"},
		{"7","Invalid NumOfParts ","The NumOfParts parameter�s value does not agree with the actual number of parts passed in the message structure"},
		{"8","Invalid RequestSize ","The RequestSize parameter�s value does not tally with the number of MSISDNs that have actually been passed in the MSISDN array"},
		{"9","Error! This account is not currently active ","The account that is attempting to make the call is not currently active within the application."},
		{"10","This API is unavailable ","The API that is being called is currently set to inactive"},
		{"11","Invalid message type ","The message type passed does not correspond to one of the allowed message types."},
		{"13","Index does not exist for preindexed content ","The user has attempted to pass pre-split multipart messages without passing an Index parameter as part of the Message array"},
		{"15","At least one mandatory parameter is missing ","One of the mandatory parameters for the call has not been passed"},
		{"18","Argument of wrong type passed ","One of the parameters passed does not conform to the prescribed types."},
		{"19","Personalisation not allowed for SEND API ","Personalisation may not be used for the Send API"},
		{"21","You are not authorised to use MT billing ","The customer�s account does not have the necessary security privileges to send an MT billed request."},
		{"22","You are not authorised to use receipting ","The customer�s account does not have the necessary security privileges to send a receipted request."},
		{"31","TimeToLive outside valid range ","The TimeToLive does not fall within the valid time range. 3600 -> 432,000"},
		{"41","Account ID is an empty FRESPONSE ","There is an internal problem with the application. Please try again later."},
		{"43","Unrecognised method call ","The method called is not one of the defined methods"},
		{"49","Invalid Account ID ","There is an internal problem with the application. Please try again later."},
		{"50","Invalid Request ID ","The request Id passed is not a valid parameter."},
		{"51","Request ID is an empty FRESPONSE ","The Request Id parameter has not been populated"},
		{"52","Request is not a valid SEND request ","The Request ID passed does not correspond to a valid Send Request"},
		{"53","Request is not a valid BATCH request ","The Request ID passed does not correspond to a valid Batch Request"},
		{"59","Datetime not in correct format ","The starttime should be passed as Unix epoch seconds. Any other format will be rejected"},
		{"61","Invalid Message type ","The message type passed does not correspond to one of the allowed message types."},
		{"62","Account attempting to request data for a request belonging to another account","The user is attempting to access data that belongs to another account. This action will be logged"},
		{"63","No default route configured for this Account ","The Account has not been configured with a route"},
		{"64","Route specified is not permitted for this Account ","The route that the user is trying to use is not permitted"},
		{"65","Message type specified is not permitted for this Account ","The message type specified is not permitted for the account"},
		{"66","Message type specified for the Account is not permitted on this Route","The message type specified is not permitted on the selected account"},
		{"67","Originator specified is not permitted for this Account ","The originator that the user has passed is not allowed"},
		{"68","Originator specified for the Account is not permitted on this Route","The originator the user has passed is not allowed on the selected route"},
		{"69","MTCode specified is not permitted for this Account ","The MTCode passed by the user is not permitted on this account"},
		{"70","Receipting for this Account is not supported on this Route ","The selected route does not support route"},
		{"71","Start date out of acceptable range ","The passed start date does not fall within the accepted range"},
		{"72","Max MSISDN fail limit exceeded ","The request has too many MSISDNs which fail validation"},
		{"73","Dynamic Originator is not permitted for this Account ","The use of dynamic originators is not allowed for this account."},
		{"74","Dynamic Originator is too long ","The dynamic originator that the user has passed is too long. Max length is 16 characters."},
		{"80","Message file processing error ","There is an internal problem with the application. Please try again later."},
		{"99","You are not authorised to call this API ","The user does not have sufficient security privileges to call the API"},
		{"100","Internal error ","An internal problem has caused the call to fail."},
		{"101","Action not supported, submitted request is in QUEUED state","The request is in the wrong state for the requested action to be carried out"},
		{"102","Action not supported, submitted request is already in PROCESSING state","The request is in the wrong state for the requested action to be carried out"},
		{"103","Action not supported, submitted request is in SUSPENDED state","The request is in the wrong state for the requested action to be carried out"},
		{"104","Action not supported, submitted request is in FAILED state ","The request is in the wrong state for the requested action to be carried out"},
		{"105","Action not supported, submitted request is already FINISHED","The request is in the wrong state for the requested action to be carried out"},
		{"106","Action not supported, submitted request is already REMOVED","The request is in the wrong state for the requested action to be carried out"},
		{"201","SimpleSend and SimpleMTSend only support one entry in both the Message and MSISDN arrays","It is only possible to call the simplesend and simplemtsend with one msisdn at a time."},
		{"202","Invalid format specified for num in MSISDN structure ","This error will be specific to a particular MSISDN and will be passed as part of the Failed_MSISDN structure. This error is raised whenever the number in question does not pass the validation required"},
		{"203","Number is not MT Billable ","This error will be specific to a particular MSISDN and will be passed as part of the Failed_MSISDN structure. This error is raised whenever the number in question does not pass MT Billing checks"},
		{"204","Message length is more than 160 characters ","This error will be specific to a particular MSISDN and will be passed as part of the Failed_MSISDN structure. This error is raised whenever the personalisation requested causes the message length to exceed the normal parameters"},
		{"205","MSISDN contains invalid personalisation field ","This error will be specific to a particular MSISDN and will be passed as part of the Failed_MSISDN structure. This error is raised whenever a personalisation field is passed which does not exist within the message text"},
		{"206","Unable to send MTBilled messages at this time, please try later","The MT Billing capability is temporarily unavailable so no MT Billed messages can be sent."},
		{"GX-8","Unable to send the message due to illegal message content ","The message has been transmitted without the characters having been correctly escaped. E.g. A � sign not sent as &#163;"},
		{"MT-1","N/A ","MSISDN is blacklisted and cannot be billed"},
		{"MT-2","N/A ","The phone number does not fall under any known operator"},
		{"MT-3","N/A ","The Number is not a billable number for the operator"},
		{"MT-4","N/A ","MTCode is not valid for the operator"},
		{"MT-5","N/A ","Number is not billable due to the operator it belongs to"},
		{"MT-8","N/A ","The number has exceeded its quota for that day"}
	};
	private static Logger log =LoggerFactory.getLogger(SmsErrorCodes.class);
	public static HashMap errCodes = new HashMap(ERRORCODESARRAY.length);
	static{
		loadErrorCode();
	}
	/**
	 * 
	 */
	public SmsErrorCodes() {
		super();
//		 TODO Auto-generated constructor stub
	}
	
	public static void loadErrorCode()
	{
		
		log.debug("Loading SMS ErrorCodes");
		 
		for( int i=0; i<ERRORCODESARRAY.length; i++ )
		{
			String []volerr = new String[ERRORCODESARRAY[i].length];
			for( int j=0; j<ERRORCODESARRAY[i].length; j++ )
			{
				volerr[j] = ERRORCODESARRAY[i][j];
			}
			errCodes.put(ERRORCODESARRAY[i][0],volerr);
			//log.debug("inserted array entry : " + volerr[0] + "/" + volerr[1] + volerr[2] + " into map with" + ERRORCODESARRAY[i][0] + " key");
        }
		 
	}

}
