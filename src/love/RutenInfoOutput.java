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

public class RutenInfoOutput
{
    static final String LOGON_SITE = "member.ruten.com.tw";
    static final int    LOGON_PORT = 443;
    
    private List<String> nextpage;
    private List<String> orderlist;
    private List<Order> orderinfo;
    private Order my;
    private boolean outorder;
    private int page;
    private boolean send711;
    private boolean accept;
   
    public RutenInfoOutput(int cpage) 
    {
        super();
        nextpage = new ArrayList<String>();
        orderlist = new ArrayList<String>();
        orderinfo = new ArrayList<Order>();
        page = cpage;
        send711 = false;
        outorder = false;
        accept = false;
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
    	//get one page
    	String surl; 
    	for (int i=1; i <= page; i++)
    	{
    		surl = "http://mybid.ruten.com.tw/master/my.php?l_type=sel_confirmed&p=" + i +"&p_size=30&o_column=it&o_sort=desc&s_year=2010&s_month=06&s_day=29&e_year=2010&e_month=12&e_day=29&s_type=0&s_hour=11&s_minute=01&s_second=00&e_hour=11&e_minute=01&e_second=00&has_search=0&s_content=";   		
    		getCurrentPageOrder(cookies, surl);
    	}
    	
    	for (int i=0; i < orderlist.size(); i++)
    	{
    		surl = "http://mybid.ruten.com.tw/master/" + orderlist.get(i);   		
        	getCurrentPageDetailOrder(cookies, surl);
    	}
    	
/*
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
    		
    	}*/
    	return null;
    }
    	
    public boolean getCurrentPageOrder(String cookies, String surl)
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
    	int field=0;
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
 	         accept = false;
 	         
 	         field = 11;
 	         for (int r=field; r<15; r++)
 	         {
 	 	    	 boolean rep = loadtable_item(loadtable(htmlfile, r), 1, 1);
 	        	 if (rep == true)
 	        	 {
 	        		 field = r;
 	    	         accept = false;
 	        		 System.out.println("field: " + field);
 	        		 break;
 	        	 }
 	         }
 	         
 	    	 if (outorder == false)
 	    	 {
 		    	 loadtable_item(loadtable(htmlfile, field + 2), 1, 2);
 	    		 loadtable_item(loadtable(htmlfile, field + 3), 1, 3);
 	    	 }
 	    	 else
 	    	 {
 	    		 loadtable_item(loadtable(htmlfile, field + 4), 1, 2); 	    		 
 	    		 loadtable_item(loadtable(htmlfile, field + 5), 1, 3); 	    		 
 	    	 }
 	     }
 	     else
 	     {
 	    	 field = 10;
 	         for (int r=field; r<15; r++)
 	         {
 	 	    	 boolean rep = loadtable_item(loadtable(htmlfile, r), 1, 1);
 	        	 if (rep == true)
 	        	 {
 	        		 field = r;
 	    	         accept = false;
 	        		 System.out.println("field: " + field);
 	        		 break;
 	        	 }
 	         }

 	    	 
 	    	 if (outorder == false)
 	    	 {
 		    	 loadtable_item(loadtable(htmlfile, field + 2), 1, 2);
 	    		 loadtable_item(loadtable(htmlfile, field + 3), 1, 3);
 	    	 }
 	    	 else
 	    	 {
 	    		 loadtable_item(loadtable(htmlfile, field + 4), 1, 2); 	    		 
 	    		 loadtable_item(loadtable(htmlfile, field + 5), 1, 3); 	    		 
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

     public boolean loadtable_item(String html,int start_row, int func) 
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
            	  						
            	  						if (st.equals("通知已出貨") || st.equals("貨物配送中") || st.equals("後送抵門市") || st.equals("買家已取貨"))
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
                    	  		if (td[0].toPlainTextString().trim().equals("買家資訊"))
                    	  		{
                    	  			accept=true;
                    	  		}
                    	  		
                    	  		if (accept == false)
                    	  		{
                    	  			System.out.println("ctag: " + td[0].toPlainTextString().trim());
                    	  			return false;
                    	  		}
                    	  		
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
                    	  		//System.out.println("talk tag:" +  td[k].toPlainTextString().trim());
                    	  		if (k == 0)
                    	  		{
            	  					my.toTalk = td[k+1].toPlainTextString().trim();
                    	  			//System.out.println((k+1) + " talk tag:" +  td[k+1].toPlainTextString().trim());
                    	  		}
                    	  		
                    	  	}
                    	  	else if (func == 4)
                    	  	{
                    	  		//System.out.println(i + " " + j + " " + k +  " " + td[k].toPlainTextString().trim());                    	  		
                    	  	}
                         }
                     }
                 }
             }
         } catch (ParserException e) {
             e.printStackTrace();
         }    	
     	
         return true;
     }

     public boolean loadtable_order_item(String html) 
     { 
       NodeList nodeList = null;
       boolean range=false;
         
       boolean section=false;
       int order_no=0;
       int order_url_no=0;

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
     	                //System.out.println("tag: " + line);
                 	    try
                 	    {
                 	    	if (line.substring(0,7).equals("void(0)"))
                 	    	{
                 	    		section = true;
                 	    	}
                 	    	else if (line.substring(0,25).equals("view_transaction.php?tno=")  && section == true)
                 	    	{
                 	    			orderlist.add(line);
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

  		return range;
}
}