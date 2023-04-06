package com.o2.techm.netcool.eai.manonsite;

public class WorkItem
{
    
    /**
     * @param shortCode
     * @param network
     * @param msisdn
     * @param cellSite
     * @param prefix
     * @param command
     */
    public WorkItem(String shortCode, String network, String msisdn, String cellSite, String prefix, String command)
    {
        super();
        this.shortCode = shortCode;
        this.network = network;
        this.msisdn = msisdn;
        this.cellSite = cellSite;
        this.prefix = prefix;
        this.command = command;
    }

    
    /**
     * @return Returns the cellSite.
     */
    public String getCellSite()
    {
        return cellSite;
    }
    /**
     * @return Returns the msisdn.
     */
    public String getMsisdn()
    {
        return msisdn;
    }
    /**
     * @return Returns the network.
     */
    public String getNetwork()
    {
        return network;
    }
    /**
     * @return Returns the shortCode.
     */
    public String getShortCode()
    {
        return shortCode;
    }
    
    public String getCommand()
    {
        return command;
    }
    public String getPrefix()
    {
        return prefix;
    }
    private String shortCode;
    private String network;
    private String msisdn;
    private String cellSite;
    private String prefix;
    private String command;

}
