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

public class LoadSecretHtml
{
    static final String LOGON_SITE = "member.ruten.com.tw";
    static final int    LOGON_PORT = 443;
    
    private int date_range;
    private Date date;
    private String rangedate;
    private List<String> nextsecretlist;
    private List<String> secretlist;
    private List<SecretItem> secretinfo;
    private SecretItem my;
    private int total_page;
   
    public LoadSecretHtml(int date_range_d) 
    {
        super();
        nextsecretlist = new  ArrayList<String>();
        secretlist = new ArrayList<String>();
        secretinfo = new ArrayList<SecretItem>();
        
        total_page = 0;
        date_range = date_range_d;
       
    	//get date range for order
    	Calendar calendar = new GregorianCalendar();
        calendar.add(Calendar.DATE , -date_range);
        date = calendar.getTime();
    }

    public int getSecretitemlength()
    {
    	return secretinfo.size();
    }
    
    public SecretItem getSecretitem(int item)
    {
    	return secretinfo.get(item);
    }
    
    public boolean getAllSecretMessage(String cookies) 
    {
       int current_page=0;
		String dtype = "";
		String sort = "";
		String page = "";
    	String sec_uid="";
    	String secret_read_type="";
    	String listno="";

    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
    	rangedate = sdf.format(date);
    	
        System.out.println(rangedate);
    	//get one page for last 7 days
    	String surl = "http://mybid.ruten.com.tw/secret/secret.php?act=&sec_uid=&dtype=sd&sort=desc&secret_read_type=all&seldate=7&secret_type=receive&listno="; 
    	        
    	getSecretlist(cookies, surl, true);
    	
    	total_page = nextsecretlist.size();
    	//get other page data
    	for (int i=0; i<total_page-1; i++)
    	{
  			StringTokenizer Tok = new StringTokenizer(nextsecretlist.get(i),",");
  			int j=0;
  			while (Tok.hasMoreElements())
  			{
  				switch (j)
  				{
  					case 0:
  						dtype = Tok.nextElement().toString();
  						break;
  					case 1:
  						sort = Tok.nextElement().toString();
  						break;
  					case 2:
  						page = Tok.nextElement().toString();
  						break;
  				}
  				j++;
 			}
    		
        	surl = "http://mybid.ruten.com.tw/secret/secret.php?act=&sec_uid=&dtype=" + 
        					dtype + "&sort=" + sort + "&page=" + page + "&secret_read_type=all&seldate=7&secret_type=receive&listno="; 
    	
        	getSecretlist(cookies, surl, false);    		
    	}
    	
    	
    	
    	for (int i=0; i < secretlist.size(); i++)
    	{
  			StringTokenizer Tok = new StringTokenizer(secretlist.get(i), ",");
  			
  			int j=0;
  			while (Tok.hasMoreElements())
  			{
  				switch (j)
  				{
  					case 0:
  						sec_uid = Tok.nextElement().toString();
  						break;
  					case 1:
  						secret_read_type = Tok.nextElement().toString();
  						break;
  					case 2:
  						listno = Tok.nextElement().toString();
  						break;
  				}
  				j++;
 			}
    		
  	    	surl = "http://mybid.ruten.com.tw/secret/secret.php?act=&sec_uid=" + sec_uid + "&dtype=sd&sort=desc&page=&secret_read_type=" + secret_read_type 
  	    	                                                 + "&seldate=7&secret_type=receive&listno=" + listno;
    		//System.out.println(surl);
    	   getSecretPageDetailOrder(cookies, surl, sec_uid);
    		
    	}
    	/*
    	for (int i=0; i < secretinfo.size(); i++)
    	{
    		SecretItem info = secretinfo.get(i);
    		
    		System.out.println("");
    		System.out.println("<new...>");
    		System.out.println("sec_uid: " + info.sec_uid);
    		System.out.println("stime: " + info.stime);
    		System.out.println("sendername: " + info.sendername);
    		System.out.println("recievername: " + info.recievername);
    		System.out.println("title: " + info.title);
    		System.out.println("context: " + info.context);
    	}*/
    	return true;
    }
    
    public boolean getSecretlist(String cookies, String surl, boolean findnextpage)
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
 	     
 	    if (findnextpage == true)
 	    	 loadtable_item((loadtable(htmlfile, 28)), 0,  0);
 	     
        System.out.println("new...");
        //loading list
 	    loadtable_item((loadtable(htmlfile, 25)), 6, 1);
 	     
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

    public void getSecretPageDetailOrder(String cookies, String surl, String sec_uid)
    {
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
 	     
 	     my = new SecretItem();
 	     my.sec_uid = sec_uid;
 	     
  	    loadtable_secretitem(loadtable(htmlfile, 25), 1, 0);
 	    loadtable_secretitem(loadtable(htmlfile, 26), 1, 1);
 	    loadtable_secretitem(loadtable(htmlfile, 27), 1, 2);
 	    loadtable_secretitem(loadtable(htmlfile, 28), 1, 3);
 	    loadtable_secretitem(loadtable(htmlfile, 35), 0, 4);
 	    
 	    secretinfo.add(my);

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

     //loading secret list for html file
     public void loadtable_item(String html, int start_row, int func) 
     { 
    		NodeList nodeList = null;
    		Parser myParser = Parser.createParser(html,"Big5");
    		NodeFilter linkFilter = new NodeClassFilter(LinkTag.class);
    	    OrFilter lastFilter = new OrFilter();
    	    lastFilter.setPredicates(new NodeFilter[] {linkFilter});
    	  
    	    try {
    	        nodeList = myParser.parse(lastFilter);
    	        for (int i = start_row; i <= nodeList.size(); i++) 
    	        {
    	            if ((nodeList.elementAt(i) instanceof LinkTag))
    	            {
    	            	    LinkTag linknode = (LinkTag) nodeList.elementAt(i);
    	            	    String line = linknode.getLink();
    	            	    try
    	            	    {
    	            	    	String filter = line.replaceAll("['();]", ""); 
    	            	    	if (func == 0)
    	            	    	{
    	            	    		String token = filter.substring(11, filter.length());
    	            	    		nextsecretlist.add(token);
    	            	    	}
    	            	    	else if (func == 1)
    	            	    	{
    	            	    		String token = filter.substring(18, filter.length());
    	            	    		secretlist.add(token);
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
     }
     
     public boolean loadtable_secretitem(String html, int start_row, int func) 
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
                         for (int k = start_row; k <= start_row; k++) 
                         	{
                     	 	if (func == 0)
                     	 		{
                     	 		my.stime = td[k].toPlainTextString().trim();
                     	 		}
                     	 	else if (func == 1)
             	 				{
             	 					my.sendername = td[k].toPlainTextString().trim();
             	 				}
                     	 	else if (func == 2)
                 	 			{
                 	 				my.recievername = td[k].toPlainTextString().trim();
                 	 			}
                     	 	else if (func == 3)
                 	 			{
                 	 				my.title = td[k].toPlainTextString().trim();
                 	 			}
                     	 	else if (func == 4)
                 	 			{
                 	 				my.context = td[k].toPlainTextString().trim();
                 	 			}
                    	  	}
                     }
                 }
             }

         } catch (ParserException e) {
             e.printStackTrace();
         }    

         
  		return false;
}


}