package com.o2.techm.netcool.eai.o2gateway.pluginframework;

/**
 * @author trenaman
 */
public class VariableInfo
{
    private String name;
    private String defaultValue;

    public VariableInfo()
    {
    }

    /**
     * @param name
     * @param defaultValue
     */
    public VariableInfo(String name, String defaultValue)
    {
        super();
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getDefaultValue()
    {
        return defaultValue;
    }
    public void setDefaultValue(String value)
    {
        this.defaultValue = value;
    }
}
