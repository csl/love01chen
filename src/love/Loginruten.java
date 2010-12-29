package love;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Date;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;

public class Loginruten
{
    static final String LOGON_SITE = "member.ruten.com.tw";
    static final int    LOGON_PORT = 443;
    
    //cookie
    private String cookies;
    
    //mouse
    private int m_x=0;
    private int m_y=0;
    private long timestamp;
    
    //show picture
    private JFrame f;
    
	private rutenGraphic rtgraphic;	

    private RutenInfo ri;	
	private LoadQuestionAndAnswerHtml qna;
	private LoadSecretHtml sh;
	private int wiretoExcel;
    
    
    public  Loginruten(RutenInfo sri, LoadQuestionAndAnswerHtml sqna, LoadSecretHtml ssh, rutenGraphic rg) 
    {
    	rtgraphic = rg;
    	ri = sri;
    	qna = sqna;
    	sh = ssh;
    }

    public String getCookies()
   {
	   return cookies;
   }
    
   public void getAuthPicture(int code)
   {
	   	wiretoExcel = code;
	   	
	    //get timestamp
	    timestamp = (new Date()).getTime();
    	String surl = "https://member.ruten.com.tw/user/image.php?key="+timestamp;
    	BufferedReader br = null;
    	
    	try{
      	    X509TrustManager xtm = new Java2000TrustManager();      
      	    TrustManager mytm[] = { xtm };      
        	SSLContext ctx = SSLContext.getInstance("SSL");      
        	ctx.init(null, mytm, new java.security.SecureRandom());
    	    HttpsURLConnection.setDefaultSSLSocketFactory((SSLSocketFactory) ctx.getSocketFactory());

    	    URL url = new URL( surl );
    	    HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    	    connection.setDoInput( true );
    	    connection.setDoOutput(true);
    	    connection.setUseCaches(false);  
    	    connection.setRequestMethod( "GET" );
    	    connection.setRequestProperty( "Host", url.getHost() );
    	    connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20100316 Firefox/3.6.2" );
    	    connection.setRequestProperty( "Accept", "*/*" );
    	    connection.setRequestProperty( "Accept-Encoding", "gzip, deflate" );
    	    connection.setRequestProperty( "Connection", "close" );
    	    connection.connect();  
    	    
    	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "big5"));  
    	    out.flush();  
     	    out.close();  
    	    
    	    if( "gzip".equalsIgnoreCase( connection.getContentEncoding() ) )
    	    {
    	    	System.out.println("gzip");  
    	        br = new BufferedReader( new InputStreamReader( new GZIPInputStream( connection.getInputStream() ), "Big5" ) );
    	    }
    	    else
    	    {
    	    	System.out.println("no gzip");  
    	    }

    	    BufferedInputStream in = new BufferedInputStream(connection.getInputStream());
    	    ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
    	    int c;
    	    while ((c = in.read()) != -1) {
    	         byteArrayOut.write(c);
    	     }
    	    
    	    //show auth for user
    	    f = new JFrame();
    	    JPanel panel = new JPanel();
    	    ImageIcon icon = new ImageIcon(byteArrayOut.toByteArray());
    	    JLabel label = new JLabel();
    	    label.setIcon(icon);
    	    panel.add(label);
    	    f.getContentPane().add(panel);
    	    
            f.setVisible(true);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.setSize(220,180);
            f.setLocation(200,100);
            f.setVisible(true);

            //mouse handler
            MouseAdapter ml = new MouseAdapter()
            {

               @Override
               public void mouseClicked(MouseEvent me)
               {
            	   f.dispose();
            	   m_x = me.getX();
            	   m_y = me.getY();
            	   PostDataRuten(timestamp);
            	   RutenInfoOutput rio = new RutenInfoOutput(37);
            	   rio.getCurrentOrderTNO(cookies);
       	    	   WriteExcelfile wexcel = new WriteExcelfile();
    	    	   wexcel.WriteExcel(rio);
            	   /*
            	   //fetch order
            	   ri.getCurrentOrderTNO(cookies);
            	   
            	   if (wiretoExcel == 0)
            	   {
            		   qna.getAllQNAMessage(cookies, "21008219374030");
            		   sh.getAllSecretMessage(cookies);
            	   }
            	   else
            	   {
            	    	WriteExcelfile wexcel = new WriteExcelfile();
            	    	wexcel.WriteExcel(ri);
            	   }
            	   */
				   //JOptionPane.showMessageDialog((Component) null, "§¹¦¨¡I", "°T®§",
					//		JOptionPane.WARNING_MESSAGE);

			       //rtgraphic.reloadinfo();
               }

            };
            f.getContentPane().addMouseListener(ml);
            f.getContentPane().addMouseMotionListener(ml);
    	    connection.disconnect();
    	    br.close();
    	    br = null;
    }
   	catch( Exception e ){
    	    try{
    	        if( br != null ){
    	            br.close();
    	        }
    	    }
    	    catch( IOException ioe ){}
    }
	   
	   
   }
//
   private void PostDataRuten(long timestamp)
   {
	     String surl = "https://member.ruten.com.tw/user/login.php?refer=http%3A%2F%2Fwww.ruten.com.tw%2F&btn_login_x=" + m_x + "&btn_login_y=" + m_y;
	     System.out.println(surl);
	     BufferedReader br = null;
	    
	     try{
	  	     X509TrustManager xtm = new Java2000TrustManager();
		     TrustManager mytm[] = { xtm };
		     SSLContext ctx = SSLContext.getInstance("SSL");
		     ctx.init(null, mytm, new java.security.SecureRandom());
		     HttpsURLConnection.setDefaultSSLSocketFactory((SSLSocketFactory) ctx.getSocketFactory());
	     URL url = new URL( surl );
	     HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
	     connection.setDoInput( true );
	     connection.setDoOutput(true);
	     connection.setUseCaches(true);
	     connection.setRequestMethod( "POST" );
	     connection.setRequestProperty( "Host", url.getHost() );
	     connection.setRequestProperty( "User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1) Gecko/20100316 Firefox/3.6.2" );
	     connection.setRequestProperty( "Accept", "*/*" );
	     connection.setRequestProperty( "Accept-Encoding", "gzip, deflate" );
	     connection.setRequestProperty( "Connection", "Keep-Alive" );
	     connection.connect();
	    
	     BufferedWriter out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "big5"));
	     //user && password
	     out.write("captcha=" + timestamp + "&userid=&userpass=");
	     out.flush();
	     out.close();
	    
	     if( "gzip".equalsIgnoreCase( connection.getContentEncoding() ) )
	    	 br = new BufferedReader( new InputStreamReader( new GZIPInputStream( connection.getInputStream() ), "Big5" ) );
	     else
	    	 br = new BufferedReader( new InputStreamReader( connection.getInputStream(), "Big5" ) );

	     String inputLine = "";
	     String htmlfile="";
	     
	     //Print html file
	     while( ( inputLine = br.readLine() ) != null )
	     {
	    	 htmlfile = htmlfile + inputLine;
	     }
	     System.out.println(htmlfile);

	     //Print Header
	     String headName="", head="";
         for (int i = 0; (headName = connection.getHeaderField(i)) != null; i++) 
         {  
        	 head = head +connection.getHeaderFieldKey(i) + " " + headName + "\n";   
         } 
         
         //cookie
         CookieManager cm = new CookieManager();
         cm.getCookie(connection);
         cookies = cm.toString();

         br.close();
	      br = null;
	    }
	    catch( Exception e ){
	     try{
	     if( br != null ){
	     br.close();
	     }
	     }
	     catch( IOException ioe ){}
	     }	   
   
   }
 }