package com.fr.data;

import java.util.logging.Logger;

import mobile.MobileCodeWSStub;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.rpc.client.RPCServiceClient;

import com.fr.data.AbstractTableData;
import com.fr.general.data.TableDataException;
import com.fr.third.javax.xml.namespace.QName;

public class WebServiceWsdlTableDataDemo2 extends AbstractTableData{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String[][] data;  
    
    public WebServiceWsdlTableDataDemo2() {  
        this.data = this.initData();
    }
    
    public int getColumnCount() throws TableDataException {  
        return data[0].length;  
    }  
  
    //��ȡ�е�����Ϊ�����е�һ�е�ֵ  
    public String getColumnName(int columnIndex) throws TableDataException {  
        return data[0][columnIndex];  
    }  
  
    //��ȡ����Ϊ���ݵĳ���-1  
    public int getRowCount() throws TableDataException {  
        return data.length - 1;  
    }  
  
    //��ȡֵ  
    public Object getValueAt(int rowIndex, int columnIndex) {  
        return data[rowIndex + 1][columnIndex];  
    }  
  
	public String[][] initData() {
		 try {
		      String url = "http://www.webxml.com.cn/WebServices/MobileCodeWS.asmx?wsdl";
		      MobileCodeWSStub stub = new MobileCodeWSStub(url);
//		      MobileCodeWSStub.GetMobileCodeInfo aa = new MobileCodeWSStub.GetMobileCodeInfo();
		      MobileCodeWSStub.GetDatabaseInfo bb = new MobileCodeWSStub.GetDatabaseInfo();
//		      aa.setMobileCode("18795842832");
//		      String rs=stub.getMobileCodeInfo(aa).getGetMobileCodeInfoResult();
		     String[] p=stub.getDatabaseInfo(bb).getGetDatabaseInfoResult().getString();
		     String result[][] = new String[p.length][3];
		     String b1,b2,b3;
		     for(int i = 0;i<p.length;i++)
		     {		       
		    	 if(p[i].length()!=0)
		    	 {
		       b1 = p[i].substring(0, p[i].indexOf(" "));
		       b2 = p[i].substring(p[i].indexOf(" ")+1).substring(0,p[i].substring(p[i].indexOf(" ")+1).indexOf(" "));
		       b3 = p[i].substring(p[i].indexOf(" ")+1).substring(p[i].substring(p[i].indexOf(" ")+1).indexOf(" ")+1);
		       result[i][0] = b1;
		       result[i][1] = b2;
		       result[i][2] = b3;
			}			
		   }
		     return result;
		} catch (org.apache.axis2.AxisFault e) {
			e.printStackTrace();
		} catch (java.rmi.RemoteException e) {
			e.printStackTrace();
		}
		return new String[][] { {} };
	}
	public static void main(String[] args) {
		for(int i=0; i<new WebServiceWsdlTableDataDemo2().initData().length; i++) {
			System.out.println(new WebServiceWsdlTableDataDemo2().initData()[i]);
		}
	}
    }