package love;

import java.io.FileInputStream; 
import java.io.FileNotFoundException; 
import java.io.IOException; 
import java.io.InputStream; 
import java.util.ArrayList;
import java.util.List;

import jxl.Cell;
import jxl.Sheet; 
import jxl.Workbook; 
import jxl.WorkbookSettings;

public class LoadExeclfile
{
 
	private List<PassBookInq>  pbi;
	private PassBookInq match_account;
	
    public LoadExeclfile() 
    {
        super();
        pbi = new ArrayList<PassBookInq>();
    }

    public int getExcelsize()
    {
    	return pbi.size();
    }
    
    public PassBookInq getExcelfile(int seq)
    {
    	return pbi.get(seq);
    }
    
    public boolean Loadexcel(String filepath) 
    {
    	System.out.println(filepath);

    	try { 
    		InputStream is = new FileInputStream(filepath); 
    		WorkbookSettings wbs = new WorkbookSettings();      
             wbs.setEncoding("Big5"); 
    		 Workbook wb = Workbook.getWorkbook(is, wbs); 
    		 Sheet sheet = wb.getSheet("未登摺資料查詢");
    		 Cell cell;
           for (int i = 0; i < sheet.getRows(); i++) 
            {
        	   	  if (i<4) continue;
        	   	  
                for (int j = 0; j < sheet.getColumns(); j++) 
                  { 
                    cell = sheet.getCell(j, i);
                    if (!cell.getContents().toString().equals(""))
                       {
                    		//System.out.println(j + " " + i + " getColumns: " + cell.getContents());
                    		switch (j)
                    		{
                    			case 0:
                    				match_account = new PassBookInq();
                    				match_account.sdate = cell.getContents();
                    				break;
                    			case 2:
                    				match_account.direction = cell.getContents();
                    				break;
                    			case 4:
                    				match_account.money = cell.getContents();
                    				break;
                    			case 5:
                    				match_account.total = cell.getContents();
                    				break;
                    			case 6:
                    				match_account.BookInq = cell.getContents();
                    				pbi.add(match_account);
                    				break;
                    		}
                    	
                       }
                  }
            }
           wb.close();
    	}
    	catch (Exception X)
    	{
    		return false;
    	}
    
    	return true;
    }
    
    public void printfData() 
    {
    	for (int i=0; i<pbi.size(); i++)
    	{
    		PassBookInq p = pbi.get(i);
    		System.out.println("sdate = " + p.sdate);
    		System.out.println("direction = " + p.direction);
    		System.out.println("total = " + p.total);
    		System.out.println("BookInq = " + p.BookInq);
    		System.out.println(".........................................");    		
    	}
    }
    
    public int matchAccount(String account_no, int cost)
    {
    	
    	
    	return 1;
    }
    


 }