package com.o2.techm.netcool.eai.o2gateway.serviceview;

public class Recalc
{
   
   /** OBJECT_TYPE */
   private int m_objectType;

   /** CDS_ID  */
   private String m_CDSId;
   
   
   public Recalc() 
   {
		m_objectType=0;
		m_CDSId=null;
   }

   /** m_objectType=objectType   */
   public void setObjectType(int objectType)
   {
		m_objectType=objectType;
   }

   /** return m_objectType */
   public int getObjectType ()
   {
		return m_objectType;
   }

   /** m_CDSId=CDSId   */
   public void setCDSId(String CDSId)
   {
		m_CDSId=CDSId;
   }

   /** return m_CDSId */
   public String getCDSId()
   {
		return m_CDSId;
   }

}
