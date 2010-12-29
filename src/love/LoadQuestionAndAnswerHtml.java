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
import org.htmlparser.tags.Div;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class LoadQuestionAndAnswerHtml
{
    static final String LOGON_SITE = "member.ruten.com.tw";
    static final int    LOGON_PORT = 443;
    
    private List<QuestionAndAnswerItem> qnainfo;
    private QuestionAndAnswerItem my;
    private int allpage;
    
    public LoadQuestionAndAnswerHtml(int page) 
    {
        super();
        allpage = page;
        qnainfo = new ArrayList<QuestionAndAnswerItem>();
    }

    public int getQuestionAndAnswerLength()
    {
    	return qnainfo.size();
    }
    
    public QuestionAndAnswerItem getQnaitem(int item)
    {
    	return qnainfo.get(item);
    }
    
    public boolean getAllQNAMessage(String cookies, String productid) 
    {
       int current_page=1;
       boolean nodata=false;
       String surl = "";
       
       
   	  //get one page for last 7 days
    	 
    	while (nodata == false)
    	{
    		if (current_page > allpage) break;
    		
    		surl = "http://goods.ruten.com.tw/item/qa_full?" + productid + "&page=" +  current_page;
    		nodata = getQNAlist(cookies, surl);    		
    		current_page++;
    	}
/*
    	for (int i = 0; i<qnainfo.size(); i++)
    	{
    		QuestionAndAnswerItem qna = qnainfo.get(i);
    		System.out.println("");
    		System.out.println("<new...>");
    		System.out.println("context: " + qna.context);
    		System.out.println("repcontext: " + qna.repcontext); 		
    	}
*/
    	return true;
    }
    
    public boolean getQNAlist(String cookies, String surl)
    {
 	    BufferedReader br = null;
 	    boolean nodata = false;
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
 	     
 	    nodata = loadtable_item(loadtable(htmlfile, 7),0,1);
        
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
 	    
 	    return nodata;
    }

    public String loadtable(String html, int item) 
    { 
         String result ="";
         
         //get sale table
         try { 
               //Parser parser = new Parser ("test.html");
         	  Parser parser = Parser.createParser(html,"Big5");
               parser.setEncoding("Big5");

               String filterStr="div";
               NodeFilter filter = new TagNameFilter(filterStr);
               NodeList nodeList = parser.extractAllNodesThatMatch(filter);
           	 Div tabletag = (Div) nodeList.elementAt(item);
           	 result =  tabletag.toHtml();
         }catch (Exception e) { 

             e.printStackTrace(); 

         }
          return result;         
     }

     //loading secret list for html file
     public boolean loadtable_item(String html, int start_row, int func) 
     { 
    		NodeList nodeList = null;
    		Parser myParser = Parser.createParser(html,"Big5");
    		NodeFilter divFilter = new NodeClassFilter(Div.class);
    	    OrFilter lastFilter = new OrFilter();
    	    lastFilter.setPredicates(new NodeFilter[] {divFilter});
    	    boolean qbutrep = false;
    	  
    	    try {
    	        nodeList = myParser.parse(lastFilter);
    	        for (int i = start_row; i < nodeList.size(); i++) 
    	        {
    	            if ((nodeList.elementAt(i) instanceof Div))
    	            {
    	            	    Div divnode = (Div) nodeList.elementAt(i);
    	            	    String line = divnode.toPlainTextString().trim();
    	            	    String l="";

    	            	    //System.out.println("tag: " + line);

    	            	    if (!line.equals(""))
    	            	    {
    	            	    	l = line.substring(0, 2);
    	            	    	
    	            	    	if (l.equals("°ÝÃD"))
    	            	    	{
    	            	    		if (qbutrep == true)
    	            	    		{
    	            	    			qbutrep=false;
    	            	    			my.repcontext="";
    	            	    			qnainfo.add(my);
    	            	    		}
    	            	    		
    	            	    		qbutrep = true;
    	            	    		my = new QuestionAndAnswerItem();
    	            	    		my.context = line;
                    	  			
    	            	    	}
    	            	    	else if (l.equals("µªÂÐ"))
    	            	    	{
    	            	    		if (qbutrep = true)
    	            	    		{
    	            	    			qbutrep=false;
    	            	    			my.repcontext=line;
    	            	    			qnainfo.add(my);    	            	    			
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