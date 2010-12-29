package love;

import java.io.*;
import java.util.*;
import jxl.*;
import jxl.write.*;
 
public class WriteExcelfile {
 
    public WriteExcelfile()
    {
    	super();    	
    }
 
    public void WriteExcel(RutenInfoOutput info) 
    {
 
        WritableWorkbook writableWorkbook = null;
        WritableSheet writableSheet = null;
 
        try {
            writableWorkbook = Workbook.createWorkbook( new File("c:\\output.xls") );
        }
        catch(Exception ex) {
            System.out.println("Error: unable to create an output excel file");
        }
        
        int data_size = info.getOrderlength();
 
        writableSheet = writableWorkbook.createSheet("Sheet", 0);
        Label label = null;
        Order mydata = null;
        
        for(int j = 0; j < 7; j++) 
        {
          	switch (j)
           	{
           		case 0:
       	            label = new Label(j, 0, "會員名稱");
           			break;
           		case 1:
       	            label = new Label(j, 0, "姓名");
           			break;
           		case 2:
       	            label = new Label(j, 0, "電話");
           			break;
           		case 3:
       	            label = new Label(j, 0, "手機");
           			break;
           		case 4:
       	            label = new Label(j, 0, "email");
           			break;
           		case 5:
       	            label = new Label(j, 0, "地址");
            			break;
           		case 6:
      	            label = new Label(j, 0, "賣家的話");
           			break;
           	}
          	
            try {
	                writableSheet.addCell(label);
	                int l = 0;
	              	switch (j)
	               	{
	               		case 0:
	           	            l = 20;
	               			break;
	               		case 1:
	           	            l = 14;
	               			break;
	               		case 2:
	           	            l = 11;
	               			break;
	               		case 3:
	           	            l = 11;
	               			break;
	               		case 4:
	           	            l = 30;
	               			break;
	               		case 5:
	           	            l = 57;
	                			break;
	               		case 6:
	           	            l = 80;
	               			break;
	               	}
	                writableSheet.setColumnView(j, l);
	            }
	            catch(Exception ex) {
	                System.out.println( ex.toString() );
	            }
        }
        
        for(int i = 0; i < data_size; i++) 
        {
        	mydata = info.getOrderiinfo(i);
            for(int j = 0; j < 7; j++) 
            {
            	switch (j)
            	{
            		case 0:
        	            label = new Label(j, i+1, mydata.memberid);
            			break;
            		case 1:
        	            label = new Label(j, i+1, mydata.recname);
            			break;
            		case 2:
        	            label = new Label(j, i+1, mydata.recphone);
            			break;
            		case 3:
        	            label = new Label(j, i+1, mydata.reccellphone);
            			break;
            		case 4:
        	            label = new Label(j, i+1, mydata.email);
            			break;
            		case 5:
        	            label = new Label(j, i+1, mydata.rec711addr);
            			break;
            		case 6:
        	            label = new Label(j, i+1, mydata.toTalk);
            			break;
            	}
            	
	            try {
	                writableSheet.addCell(label);
	                int l = 0;
	              	switch (j)
	               	{
	               		case 0:
	           	            l = 20;
	               			break;
	               		case 1:
	           	            l = 14;
	               			break;
	               		case 2:
	           	            l = 11;
	               			break;
	               		case 3:
	           	            l = 11;
	               			break;
	               		case 4:
	           	            l = 30;
	               			break;
	               		case 5:
	           	            l = 57;
	                			break;
	               		case 6:
	           	            l = 80;
	               			break;
	               	}
	                writableSheet.setColumnView(j, l);
	            }
	            catch(Exception ex) {
	                System.out.println( ex.toString() );
	            }
            }
            
        }
 
        try {
            writableWorkbook.write();
            writableWorkbook.close();
        }
        catch(Exception ex) {
            System.out.println( ex.toString() );
        }
 
    }
 
}
