/*Modification  History :
* Date          Version Modified by     Brief Description of Modification
* 11-Jan-2011   1.10      Keane          Modified for OSC 1558511
*/
package com.o2.techm.netcool.eai.manonsite.mrs;

import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MRSRequestInfo
{

    private String prefix="";

    private String cellSiteId="";

    private String command="";

    private String text="";
    
    private String bcf="";

    private String trx="";
    
    private String bts="";
    
    private short  debugval = -1;
    
    private static final Logger log = LoggerFactory.getLogger(MRSRequestInfo.class);
    
//Create a pattern to match map 
    private Pattern	bcfAndtrx = Pattern.compile("(\\w+) *(BCF) *([0-9]+) *(TRX) *([0-9]+)"); 
    //Added for OSC 1558511
    private Pattern bcfPattern = Pattern.compile("(\\w+) *(BCF) *([0-9]+)");
    private Pattern btsPattern = Pattern.compile("(\\w+) *(BTS) *([0-9]+)");
    //End of Addition for OSC 1558511
    //  private Pattern	bcfAndtrx = Pattern.compile("(\\w+) *(BCF) *([0-9]+) *(TRX) *([0-9]+) *(\\d?)");
    public MRSRequestInfo(String text) throws MRSRequestInfoException
    {    	
        try
        {
            this.text = text;

            StringTokenizer st = new StringTokenizer(text);
            String token;
            if (st.hasMoreTokens())
            {
                token = st.nextToken();

                if (!isPrefixValid(token))
                {
                    cellSiteId = token;
                    prefix = null;
                } else
                {
                    prefix = token;
                    
                    if (st.hasMoreTokens())
                    {
                    	token = st.nextToken();
                        cellSiteId = token;
                    } else
                    {
                        String msg = "Your message '" + text
                                + "' was not recognised.  Please correct and resend or contact the NMC.";
                        log.warn(msg);
                        throw new MRSRequestInfoException(msg);
                    }
                }

                StringBuffer sb = new StringBuffer();
                while (st.hasMoreTokens())
                {
                    token = st.nextToken();
                    sb.append(token + " ");
                }
                command = sb.toString().trim();
               isExtendedCmd(command.toUpperCase());
               
            }

            if (log.isDebugEnabled())
                log.debug("cellSiteId = '" + cellSiteId + "', command = '" + command + "', prefix = '" + prefix + "'");
        } catch (Throwable ex)
        {
            String msg = "Your message '" + text
                   + "' was not recognised.  Please correct and resend or contact the NMC.";
            log.warn(msg,ex);
            throw new MRSRequestInfoException(msg);
        }
    }
    private boolean isExtendedCmd(String command)
    {    	 
    	Matcher m = bcfAndtrx.matcher(command);
    	//Added for OSC 1558511
    	Matcher m1 = bcfPattern.matcher(command);
    	Matcher m2 = btsPattern.matcher(command);
    	//End of Addition for OSC 1558511
    	
    	if(m.find() && (m.group(1) != null &&  (m.group(1).equals("HOPON") 
    		|| m.group(1).startsWith("HOPOF") || m.group(1).startsWith("UNL")||
    		m.group(1).startsWith("LOCK"))) && command.contains("TRX"))
	    {  		 
    		setBCF(m.group(3));
    		setTRX(m.group(5));  
    		/*if(m.group(6) == null || m.group(6).equals(""))
    			log.debug("Debug is not set");
    		else
    			setDebug(Short.parseShort(m.group(6)));*/
		
    		return true;
	    }
    	//Added for OSC 1558511
    	else if (m1.find() && (m1.group(1) != null && 
    		(m1.group(1).equals("HOPON") || m1.group(1).startsWith("HOPOF") 
    		|| m1.group(1).startsWith("UNL")|| m1.group(1).startsWith("LOCK"))) 
    		&& command.contains("BCF"))
    	{
    		log.debug(" Checking Pattern for BCF and setting group 1  ");
    		setBCF(m1.group(3));
    		return true;
    	}
    	else if (m2.find() && (m2.group(1) != null && 
    		(m2.group(1).startsWith("UNL")|| m2.group(1).startsWith("LOCK"))) 
    		&& command.contains("BTS")){
    		
    		log.debug(" Checking Pattern for BTS and setting group 1  ");
    		setBts(m2.group(3));
    		return true;
    	}
    	//End of Addition for OSC 1558511
    	else
    	{ 
    		return false;
    	}
    }
    private void setBCF(String s)
    {
    	this.bcf=s;
    }
    public String getBCF()
    {
    	return bcf;
    }
    
    private void setTRX(String s)
    {
    	this.trx=s;
    }
    public String getTRX()
    {
    	return trx;
    }
    
    
    
    private boolean isCellSite(String s)
    {
    	// It's a cellsite if the RESPONSE contains only digits. Note that we don't check 
        // that the length be less than or equal to six.
            	
        try
        {
            Integer.parseInt(s);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
    
    private boolean isPrefixValid(String s)
    {
    	if (s.toLowerCase().equals("dev") || s.toLowerCase().equals("int") || s.toLowerCase().equals("uat") || s.toLowerCase().startsWith("liv"))
    		return true;
    	else 
    		return false;
    }
    public String getCellSiteId()
    {
        return cellSiteId;
    }

    public void setCellSiteId(String cellSiteId)
    {
        this.cellSiteId = cellSiteId;
    }

    public String getCommand()
    {
        return command;
    }

    public void setCommand(String command)
    {
        this.command = command;
    }

    public String getPrefix()
    {
        return prefix;
    }

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
	/**
	 * @return Returns the debug.
	 */
	public short getDebug() {
		return debugval;
	}
	/**
	 * @param debug The debug to set.
	 */
	public short setDebug(short debug) { 
			this.debugval = debug;
		return debugval;
	}
	public String getBts() {
		return bts;
	}
	private void setBts(String s) {
		this.bts = s;
	}
}
