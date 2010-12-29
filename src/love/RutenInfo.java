package love;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;
import java.util.zip.GZIPInputStream;
import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class RutenInfo
{
    static final String LOGON_SITE = "member.ruten.com.tw";
    static final int    LOGON_PORT = 443;
    
    private int date_range;
    private Date date;
    private String rangedate;
    private List<String> nextpage;
    private List<String> orderlist;
    private List<Order> orderinfo;
    private Order my;
    private boolean outorder;
    
    
    private boolean send711;
   
    public RutenInfo(int date_range_d) 
    {
        super();
        nextpage = new ArrayList<String>();
        orderlist = new ArrayList<String>();
        orderinfo = new ArrayList<Order>();
        
        date_range = date_range_d;
       
    	//get date range for order
    	Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE , -date_range);
        date = calendar.getTime();
        
        send711 = false;
        outorder = false;
    }

    public int getOrderlength()
    {
    	return orderinfo.size();
    }
   
    
    public Order getOrderiinfo(int index)
    {
    	return orderinfo.get(index);
    }
    
    public String getCurrentOrderTNO(String cookies) 
    {
        int current_page=0;

    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	rangedate = sdf.format(date);
    	
        System.out.println(rangedate);
        
    	//get one page
    	String surl = "http://mybid.ruten.com.tw/master/my.php?l_type=sel_full"; 
    	boolean fnextpage = getCurrentPageOrder(cookies, surl, true);
    	
    	//get next page date until date match
    	while (fnextpage == false)
    	{
    		surl = "http://mybid.ruten.com.tw/master/my.php" + nextpage.get(current_page);   		
    		fnextpage = getCurrentPageOrder(cookies, surl, false);
    		current_page++;
    	}
    	
    	for (int i=0; i < orderlist.size(); i++)
    	{
    		surl = "http://mybid.ruten.com.tw/master/" + orderlist.get(i);   		
        	getCurrentPageDetailOrder(cookies, surl);
    	}
   
    	for (int i=0; i < orderinfo.size(); i++)
    	{
    		Order info = orderinfo.get(i);
    		
    		System.out.println("");
    		System.out.println("new " + i);
    		System.out.println("date: " + info.buyerdate);
    		System.out.println("tno: " + info.tno);
    		System.out.println("memberid: " + info.memberid);
    		System.out.println("salename: " + info.salename);
    		System.out.println("status: " + info.status);
    		System.out.println("salename: " + info.buyername);
    		System.out.println("byuerphone: " + info.byuerphone);
    		System.out.println("byuercellphone: " + info.byuercellphone);
    		System.out.println("byueremail: " + info.byueremail);
    		System.out.println("recname: " + info.recname);
    		System.out.println("recphone: " + info.recphone);
    		System.out.println("reccellphone: " + info.reccellphone);
    		System.out.println("rec711addr: " + info.rec711addr);
    		System.out.println("email: " + info.email);
    		System.out.println("account_bankname: " + info.account_bankname);
    		System.out.println("account_bankno: " + info.account_bankno);
    		System.out.println("numberofaccount: " + info.numberofaccount);
    		System.out.println("account_cost: " + info.account_cost);
    		System.out.println("save_time: " + info.save_time);
    		System.out.println("toTalk: " + info.toTalk);
    		System.out.println("buytogether: " + info.buytogether);
    		System.out.println("no_check: " + info.no_check);
    		
    	}
    	return null;
    }
    
    public boolean getCurrentPageOrder(String cookies, String surl, boolean findnextpage)
    {
 	    BufferedReader br = null;
 	    boolean range = false;
 	    System.out.println(surl);
 	    
 	    try{
 	     URL url = new URL( surl );
 	     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
 	     connection.setDoInput( true );
 	     connection.setDoOutput(true);
 	     connection.setUseCaches(false);
 	     connection.setRequestMethod( "GET" );
 	     connection.setRequestProperty( "Host", url.getHost() );
 	     connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20100316 Firefox/3.6.2" );
 	     connection.setRequestProperty( "Accept", "*/*" );
 	     connection.setRequestProperty( "Cookie", cookies);  
 	     connection.setRequestProperty( "Accept-Encoding", "gzip, deflate" );
 	     connection.setRequestProperty( "Connection", "Keep-Alive" );
 	     connection.connect();
 	    
 	     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "big5"));
 	     
 	     out.flush();
 	     out.close();
 	    
 	     if( "gzip".equalsIgnoreCase( connection.getContentEncoding() ) )
 	    	 br = new BufferedReader( new InputStreamReader( new GZIPInputStream( connection.getInputStream() ), "Big5" ) );
 	     else
 	    	 br = new BufferedReader( new InputStreamReader( connection.getInputStream(), "Big5" ) );

 	     String inputLine = "";
 	     String htmlfile="";
 	     while( ( inputLine = br.readLine() ) != null )
 	     {
 	    	 htmlfile = htmlfile + inputLine;
 	     }
 	     
 	     //collect url for nextpage
 	     if (findnextpage == true)
 	    	 loadtable_nextpage_item(loadtable(htmlfile, 19));

 	     //collect url for infomation
 	     range = loadtable_order_item(loadtable(htmlfile, 17));
 	     
 	     br.close();
 	     br = null;
 	    }
 	    catch( Exception e )
 	    {
 	    	try{
 	    		if( br != null ){
 	    			br.close();
 	    		}
 	    	}
 	    	catch( IOException ioe ){}
 	   	}
 	    
 	    return range;
    }

    public void getCurrentPageDetailOrder(String cookies, String surl)
    {
    	outorder = false;
    	
 	    BufferedReader br = null;
 	    System.out.println(surl);
 	    
 	    try{
 	     URL url = new URL( surl );
 	     HttpURLConnection connection = (HttpURLConnection) url.openConnection();
 	     connection.setDoInput( true );
 	     connection.setDoOutput(true);
 	     connection.setUseCaches(false);
 	     connection.setRequestMethod( "GET" );
 	     connection.setRequestProperty( "Host", url.getHost() );
 	     connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20100316 Firefox/3.6.2" );
 	     connection.setRequestProperty( "Accept", "*/*" );
 	     connection.setRequestProperty( "Cookie", cookies);  
 	     connection.setRequestProperty( "Accept-Encoding", "gzip, deflate" );
 	     connection.setRequestProperty( "Connection", "Keep-Alive" );
 	     connection.connect();
 	    
 	     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "big5"));
 	     
 	     out.flush();
 	     out.close();
 	    
 	     if( "gzip".equalsIgnoreCase( connection.getContentEncoding() ) )
 	    	 br = new BufferedReader( new InputStreamReader( new GZIPInputStream( connection.getInputStream() ), "Big5" ) );
 	     else
 	    	 br = new BufferedReader( new InputStreamReader( connection.getInputStream(), "Big5" ) );

 	     String inputLine = "";
 	     String htmlfile="";
 	     while( ( inputLine = br.readLine() ) != null )
 	     {
 	    	 htmlfile = htmlfile + inputLine;
 	     }
 	     
 	     my = new Order();
 	     my.tno = surl.substring(58, surl.length());
 	     
 	     //Collect URL for information 	     
 	     loadtable_item(loadtable(htmlfile, 7), 1, 0);
 	     
 	     if (send711 == true)
 	     {
 	    	 System.out.println("7-11");
 	         send711 = false;
 	    	 loadtable_item(loadtable(htmlfile, 11), 1, 1);
 	    	 if (outorder == false)
 	    	 {
 		    	 loadtable_item(loadtable(htmlfile, 13), 1, 2);
 	    		 loadtable_item(loadtable(htmlfile, 14), 1, 3);
 	    	 }
 	    	 else
 	    	 {
 	    		 loadtable_item(loadtable(htmlfile, 15), 1, 2); 	    		 
 	    		 loadtable_item(loadtable(htmlfile, 16), 1, 3); 	    		 
 	    	 }
 	     }
 	     else
 	     {
 	    	 loadtable_item(loadtable(htmlfile, 10), 1, 1);
 	    	 if (outorder == false)
 	    	 {
 		    	 loadtable_item(loadtable(htmlfile, 12), 1, 2);
 	    		 loadtable_item(loadtable(htmlfile, 13), 1, 3);
 	    	 }
 	    	 else
 	    	 {
 	    		 loadtable_item(loadtable(htmlfile, 14), 1, 2); 	    		 
 	    		 loadtable_item(loadtable(htmlfile, 15), 1, 3); 	    		 
 	    	 }
 	     }
 	     
 	     orderinfo.add(my);

 	     br.close();
 	     br = null;
 	    }
 	    catch( Exception e )
 	    {
 	    	e.printStackTrace(); 
 	    	try{
 	    		if( br != null ){
 	    			br.close();
 	    		}
 	    	}
 	    	catch( IOException ioe ){
 	    		
 	    		e.printStackTrace(); 

 	    	}
   		}
    }
    
    
    
     //25, secret, 17, order
     public String loadtable(String html, int item) { 

         String result ="";
         
         //get sale table
         try { 
               //Parser parser = new Parser ("test.html");
         	  Parser parser = Parser.createParser(html,"Big5");
               parser.setEncoding("Big5");

               String filterStr="table";
               NodeFilter filter = new TagNameFilter(filterStr);
               NodeList nodeList = parser.extractAllNodesThatMatch(filter);
           	  TableTag tabletag = (TableTag) nodeList.elementAt(item);
           	  result =  tabletag.toHtml();
         }catch (Exception e) { 

             e.printStackTrace(); 

         }
          return result;         
     }

     public void loadtable_item(String html,int start_row, int func) 
     { 
     	NodeList nodeList = null;
     	Parser myParser = Parser.createParser(html,"Big5");
     	NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
        OrFilter lastFilter = new OrFilter();
        lastFilter.setPredicates(new NodeFilter[] { tableFilter});
        try {
             nodeList = myParser.parse(lastFilter);
             for (int i = 0; i <= nodeList.size(); i++) {
                 if (nodeList.elementAt(i) instanceof TableTag) {
                     TableTag tag = (TableTag) nodeList.elementAt(i);
                     TableRow[] rows = tag.getRows();

                     for (int j = 0; j < rows.length; j++) 
                     {
                         TableRow tr = (TableRow) rows[j];
                         TableColumn[] td = tr.getColumns();
                         for (int k = 0; k < td.length; k++) 
                         {
                     	  	if (func == 0 && i == 0)
                    	  	{
                    	  		if (k == 0)
                    	  		{
                    	  			my.buyerdate = td[k].toPlainTextString().trim();
                    	  		}
                    	  		else if (k == 1)
                    	  		{
                    	  			my.salename = td[k].toPlainTextString().trim();
                    	  		}
                    	  		else if (k == 3)
                    	  		{
                    	  			String format_d = td[k].toPlainTextString().trim();
                    	 			StringTokenizer Tok = new StringTokenizer(format_d);
                    	  			
                    	  			while (Tok.hasMoreElements())
                    	  			{
                 	  					format_d = Tok.nextElement().toString();
                 	  					break;
                    	  			}
                    	  			
                    	  			if (format_d.substring(0, 1).equals("7"))
                    	  			{
                    	  				 System.out.println("use 7-11 format");
                    	  				 send711 = true;
                    	  			}
                    	  			
                    	  		}
                    	  		else if (k == 5)
                    	  		{
                    	  			my.status = td[k].toPlainTextString().trim();
            	  					my.no_check = false;
            	  					
            	  					if (my.status.length() >= 5)
            	  					{
            	  						String st = my.status.substring( my.status.length() - 5, my.status.length());
        	  							System.out.println("OUT: " + st);
            	  						
            	  						if (st.equals("通知已出貨") || st.equals("貨物配送中") || st.equals("後送抵門市"))
            	  						{
            	  							System.out.println("OUT tag: 通知已出貨");
            	  							outorder = true;            	  						
            	  						}
            	  						else if (my.status.length() > 7)
                	  					{
                	  						if (my.status.substring(0,7).equals("銀行或郵局轉帳"))
                	  						{
                	  							my.no_check = true;                   	  							
                	  						}                    	  						
                	  					}
            	  					}
                    	  		}
                    	  	}
                    	  	else if (func == 1)
                    	  	{
                    	  		if (k == 0)
                    	  		{
                    	  			switch (j)
                    	  			{
                    	  				case 1:
                    	  					my.memberid = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 2:
                    	  					my.buyername = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  			}
                    	  		}
                    	  		else if (k == 2)
                    	  		{
                    	  			switch (j)
                    	  			{
                    	  				case 1:
                    	  					my.byuerphone = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 2:
                    	  					my.byuercellphone = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 3:
                    	  					my.byueremail = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  			}
                    	  		}
                    	  		
                    	  	}
                    	  	else if (func == 2)
                    	  	{
                    	  		System.out.println(i + " " + j + " " + k +  " " + td[k].toPlainTextString().trim());                    	  		
                    	  		if (k == 0)
                    	  		{
                    	  			switch (j)
                    	  			{
                    	  				case 1:
                    	  					my.account_bankname = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 2:
                    	  					my.account_bankno = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 3:
                    	  					my.numberofaccount = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 4:
                    	  					my.account_cost = td[k+1].toPlainTextString().trim();
                    	  					
                    	  					if (!my.account_cost.equals("--"))
                    	  					{
	                    	  					int money = Integer.parseInt(my.account_cost);
	                    	  					//Determine buytogether or not
	                    	  					if (money >= 1500) 
	                    	  						my.buytogether = true;
	                    	  					else
	                    	  						my.buytogether = false;
                    	  					}
                    	  					else
                    	  						my.buytogether = false;                    	  					
                    	  					break;
                    	  				case 5:
                    	  					my.save_time = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  			}
                    	  		}                    	  		
                    	  		else if (k == 2)
                    	  		{
                    	  			switch (j)
                    	  			{
                    	  				case 1:
                    	  					my.recname = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 2:
                    	  					my.recphone = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 3:
                    	  					my.reccellphone = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 4:
                    	  					my.email = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  				case 5:
                    	  					my.rec711addr = td[k+1].toPlainTextString().trim();
                    	  					break;
                    	  			}
                    	  		}
                    	  	}
                    	  	else if (func == 3)
                    	  	{
                    	  		System.out.println("talk tag:" +  td[k].toPlainTextString().trim());
                    	  		if (k == 0)
                    	  		{
            	  					my.toTalk = td[k+1].toPlainTextString().trim();
                    	  			System.out.println((k+1) + " talk tag:" +  td[k+1].toPlainTextString().trim());
                    	  		}
                    	  		
                    	  	}
                    	  	else if (func == 4)
                    	  	{
                    	  		System.out.println(i + " " + j + " " + k +  " " + td[k].toPlainTextString().trim());                    	  		
                    	  	}
                         }
                     }
                 }
             }
         } catch (ParserException e) {
             e.printStackTrace();
         }    	
     	
     }

     public boolean loadtable_order_item(String html) 
     { 
       NodeList nodeList = null;
       boolean range=false;
         
       boolean section=false;
       int order_no=0;
       int order_url_no=0;

       Parser myParser = Parser.createParser(html,"Big5");
 	    NodeFilter tableFilter = new NodeClassFilter(TableTag.class);
       NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
       OrFilter lastFilter = new OrFilter();
       lastFilter.setPredicates(new NodeFilter[] { tableFilter, linkFilter});
       
       try {
             nodeList = myParser.parse(lastFilter);
             for (int i = 0; i <= nodeList.size(); i++) {
                 if (nodeList.elementAt(i) instanceof TableTag) {
                     TableTag tag = (TableTag) nodeList.elementAt(i);
                     TableRow[] rows = tag.getRows();

                     for (int j = 1; j < rows.length-1; j++) 
                     {
                         TableRow tr = (TableRow) rows[j];
                         TableColumn[] td = tr.getColumns();
                         
                         //get date
                         if (rangedate.equals(td[1].toPlainTextString().trim()))
                         {
                        	 range = true;
                        	// System.out.println(order_no + " get it, " + td[1].toPlainTextString().trim());
                         }
                         else if (range == false)
                         {
                        	// System.out.println(order_no + " " + td[1].toPlainTextString().trim());
                        	 order_no++;
                         }
                     }
                 }
                 if ((nodeList.elementAt(i) instanceof LinkTag))
                 {
                 	    LinkTag linknode = (LinkTag) nodeList.elementAt(i);
                 	    String line = linknode.getLink();
                 	    try
                 	    {
                 	    	if (line.substring(0,8).equals("void(0);"))
                 	    	{
                 	    		//handler last section
                 	    		if (section != false && order_url_no < order_no)
                 	    		{
                 	    			//orderlist.add("nopay"); 
                 	                //System.out.println(order_url_no + " nopay");
                 	    			order_url_no++;
                 	    		}
                 	    		section = true;
                 	    	}
                 	    	else if (line.substring(0,25).equals("view_transaction.php?tno=")  && section == true)
                 	    	{
                 	    		if (order_url_no < order_no)
                 	    		{
                 	    			orderlist.add(line);
                 	                //System.out.println(order_url_no + " " + line);
                 	    			order_url_no++;
                 	    		}
                 	    		section = false;
                 	    	}
                 	    }
                 	    catch (Exception X)
                 	    {
                 	    }                 	    
                 }
             }

         } catch (ParserException e) {
             e.printStackTrace();
         }    

         /*
        //if no, void
  		if (section != false && order_url_no < order_no)
 		{
            System.out.println(order_url_no + " nopay");
 			//orderlist.add("nopay"); 
 		}
  		*/
         
  		return range;
}

public void loadtable_nextpage_item(String html) 
{ 
	NodeList nodeList = null;
	Parser myParser = Parser.createParser(html,"Big5");
	NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
    OrFilter lastFilter = new OrFilter();
    lastFilter.setPredicates(new NodeFilter[] { linkFilter});
  
    try {
        nodeList = myParser.parse(lastFilter);
        for (int i = 0; i <= nodeList.size(); i++) {
            if ((nodeList.elementAt(i) instanceof LinkTag))
            {
            	    LinkTag linknode = (LinkTag) nodeList.elementAt(i);
            	    String line = linknode.getLink();
            	    try
            	    {
            	    	//System.out.println(line);
            	    	nextpage.add(line);
            	    }
            	    catch (Exception X)
            	    {
            	    }                 	    
            }
        }

    } catch (ParserException e) {
        e.printStackTrace();
    }    	
}

}