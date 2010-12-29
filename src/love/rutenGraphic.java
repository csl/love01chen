package love;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;

import javax.swing.*;

public class rutenGraphic extends JFrame
{
	protected JMenuBar jJMenuBar = null;
	protected JMenu jMenuFile = null;
	protected JMenuItem jMenuItemUpdate = null;
	protected JMenuItem jMenuItemMatch = null;
	protected JMenuItem jMenuItemBuy = null;
	protected JMenuItem jMenuItemBuytogether = null;
	protected JMenuItem jMenuItemExcel = null;
	protected JMenuItem jMenuItemExit = null;
	
	private Loginruten myruten;
	private LoadExeclfile myexcel;
	private RutenInfo ri;	
	private LoadQuestionAndAnswerHtml qna;
	private LoadSecretHtml sh;
	private JPanel getjpanel;
	private JPanel qnapanel;
	private JPanel secretpanel;

	//QnA
	private JLabel jLabelQna = null;
	private JButton jButtonBackQna = null;
	private JButton jButtonNextQna = null;
	private JTextArea jTextAreaQna = null;
	private JScrollPane jspane = null;

	//Secret
	private JLabel jLabelSecret = null;
	private JButton jButtonBackSecret = null;
	private JButton jButtonNextSecret = null;
	private JTextArea jTextAreaSecret = null;
	private JScrollPane jsecretspane = null;
	
	//Search
	private JButton jButtonSearch = null;
	private JTextField jTextSearchContext = null;
	
	private int cnapage;
	private int csecretpage;
	
    public rutenGraphic() 
    {
        super();
        
        cnapage = 0;
        csecretpage = 0;
        
        //Create Instance
        myexcel = new LoadExeclfile(); 
        ri = new RutenInfo(365);
        qna = new LoadQuestionAndAnswerHtml(6);
        sh = new LoadSecretHtml(6);
        myruten = new Loginruten(ri, qna, sh, this);
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Rectangle bounds = ge.getMaximumWindowBounds();
        this.setBounds(bounds);
        this.setExtendedState(Frame.MAXIMIZED_BOTH);  
		this.setTitle("Love01chen");
		this.setJMenuBar(getJMenuBar());
		this.setContentPane(getjpanel());

    }
    
    public void reloadinfo()
    {
    	jButtonBackQna.setEnabled(false);
    	jButtonNextQna.setEnabled(true);
    	jButtonBackSecret.setEnabled(false);
    	jButtonNextSecret.setEnabled(true);

		//loading QnA data
		QuestionAndAnswerItem info = qna.getQnaitem(cnapage);
		String context = info.context;
		context = context + "\n\n" + info.repcontext; 
		jTextAreaQna.setText(context);

		//loading secret data
		SecretItem csi = sh.getSecretitem(csecretpage);
		String context_info = csi.sendername + "/" + csi.recievername;
		context_info = context_info + "\n\n" + csi.stime; 
		context_info = context_info + "\n\n" + "title: "  + csi.title; 

		context_info = context_info + "\n\n";
		
		StringTokenizer Tok = new StringTokenizer(csi.context.trim(),"\t");
		int qnatotalsize = qna.getQuestionAndAnswerLength()-1;
		int shtotalsize = sh.getSecretitemlength()-1;
		while (Tok.hasMoreElements())
  		{
			context_info = context_info+ Tok.nextElement().toString() + "\n\n"; 
		}
		
		jTextAreaSecret.setText(context_info);
		jLabelQna.setText("page: " + cnapage + "/" + qnatotalsize);
		jLabelSecret.setText("page: " + csecretpage + "/" + shtotalsize);
    }
    
	public JPanel getjpanel() 
	{
		if (getjpanel == null) {
			getjpanel = new JPanel();
			GridLayout myall = new GridLayout(2, 2);
			getjpanel.setLayout(myall);
			getjpanel.add(getjQNApanel(), null);
			getjpanel.add(getSecretPane(), null);
		}
		return getjpanel;
	}   
	
	public JPanel getjQNApanel() 
	{
		if (qnapanel == null) {
			qnapanel = new JPanel();
			qnapanel.setLayout(null);
			qnapanel.add(getBackQnA());
			qnapanel.add(getNextQnA());
			qnapanel.add(getLabelQna());
			qnapanel.add(getJQnaTextArea());
			qnapanel.add(getSearchContext());
			qnapanel.add(getSearchResult());
		}
		return qnapanel;
	}
	
	public JPanel getSecretPane() 
	{
		if (secretpanel == null) {
			secretpanel = new JPanel();
			secretpanel.setLayout(null);
			secretpanel.add(getBackSecret());
			secretpanel.add(getNextSecret());
			secretpanel.add(getLabelSecret());
			secretpanel.add(getJSecretTextArea());
		}
		return secretpanel;
	}
	
	private JButton getSearchResult() {
		if (jButtonSearch == null) {
			jButtonSearch = new JButton();
			jButtonSearch.setBounds(new Rectangle(340, 280, 90, 30));
			jButtonSearch.setMnemonic(KeyEvent.VK_R);
			jButtonSearch.setText("Search");
			jButtonSearch
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
							} catch (Exception ec) {
							}
						}
					});
			jButtonSearch.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return jButtonSearch;
	}
	
	//JTextField jTextSearchContext
	private JTextField getSearchContext() {
		if (jTextSearchContext == null) {
			jTextSearchContext = new JTextField();
			jTextSearchContext.setBounds(new Rectangle(100, 280, 200, 30));
		}
		return jTextSearchContext;
	}	
	
	private JButton getBackQnA() {
		if (jButtonBackQna == null) {
			jButtonBackQna = new JButton();
			jButtonBackQna.setEnabled(false);
			jButtonBackQna.setBounds(new Rectangle(330, 10, 60, 30));
			jButtonBackQna.setMnemonic(KeyEvent.VK_R);
			jButtonBackQna.setText("<<");
			jButtonBackQna
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								int totalsize = qna.getQuestionAndAnswerLength();
								cnapage--;
								if (cnapage==0)
								{
									jButtonBackQna.setEnabled(false);
								}
								else if (cnapage == totalsize - 2)
								{
									jButtonNextQna.setEnabled(true);									
								}
								
								//loading data
								QuestionAndAnswerItem info = qna.getQnaitem(cnapage);
								String context = info.context;
								
								context = context + "\n\n" + info.repcontext; 
								
								jTextAreaQna.setText(context);
								jLabelQna.setText("page: " + cnapage + "/" + (totalsize-1));
								
							} catch (Exception ec) {
							}
						}
					});
			jButtonBackQna.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return jButtonBackQna;
	}

	private JButton getNextQnA() {
		if (jButtonNextQna == null) {
			jButtonNextQna = new JButton();
			jButtonNextQna.setEnabled(false);
			jButtonNextQna.setBounds(new Rectangle(390, 10, 60, 30));
			jButtonNextQna.setMnemonic(KeyEvent.VK_R);
			jButtonNextQna.setText(">>");
			jButtonNextQna
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								int totalsize = qna.getQuestionAndAnswerLength();

								cnapage++;
								if (cnapage == totalsize - 1)
								{
									jButtonNextQna.setEnabled(false);
								}
								else if (cnapage==1)
								{
									jButtonBackQna.setEnabled(true);									
								}
								
								//loading data
								QuestionAndAnswerItem info = qna.getQnaitem(cnapage);
								String context = info.context;
								
								context = context + "\n\n" + info.repcontext; 
								
								jTextAreaQna.setText(context);
								jLabelQna.setText("page: " + cnapage + "/" + (totalsize-1));
								
							} catch (Exception ec) 
							{
							}
						}
					});
			jButtonNextQna.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return jButtonNextQna;
	}
	
	private JLabel getLabelQna() {
		if (jLabelQna == null) {
			jLabelQna = new JLabel();
			jLabelQna.setBounds(new Rectangle(10, 10, 200, 30));
		}
		return jLabelQna;
	}	

	private JButton getBackSecret() {
		if (jButtonBackSecret == null) {
			jButtonBackSecret = new JButton();
			jButtonBackSecret.setEnabled(false);
			jButtonBackSecret.setBounds(new Rectangle(330, 10, 60, 30));
			jButtonBackSecret.setMnemonic(KeyEvent.VK_R);
			jButtonBackSecret.setText("<<");
			jButtonBackSecret
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								int totalsize = sh.getSecretitemlength();

								csecretpage--;
								if (csecretpage==0)
								{
									jButtonBackSecret.setEnabled(false);
								}
								else if (csecretpage == totalsize - 2)
								{
									jButtonNextSecret.setEnabled(true);
								}
									
								SecretItem csi = sh.getSecretitem(csecretpage);
								
								String context_info = csi.sendername;
								context_info = context_info + "\n\n" + csi.stime; 
								context_info = context_info + "\n\n" + "title: "  + csi.title; 

								context_info = context_info + "\n\n";
								
								StringTokenizer Tok = new StringTokenizer(csi.context.trim(),"\t");
								while (Tok.hasMoreElements())
						  		{
									context_info = context_info+ Tok.nextElement().toString() + "\n\n"; 
								}
								
								
								
								jTextAreaSecret.setText(context_info);
								
								jLabelSecret.setText("page: " + csecretpage + "/" + (totalsize-1));
								} catch (Exception ec) {
							}
						}
					});
			jButtonBackSecret.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return jButtonBackSecret;
	}

	private JButton getNextSecret() {
		if (jButtonNextSecret == null) {
			jButtonNextSecret = new JButton();
			jButtonNextSecret.setEnabled(false);
			jButtonNextSecret.setBounds(new Rectangle(390, 10, 60, 30));
			jButtonNextSecret.setMnemonic(KeyEvent.VK_R);
			jButtonNextSecret.setText(">>");
			jButtonNextSecret
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							try {
								int totalsize = sh.getSecretitemlength();
								csecretpage++;
								if (csecretpage == totalsize - 1)
								{
									jButtonNextSecret.setEnabled(false);
								}
								else if (csecretpage == 1)
								{
									jButtonBackSecret.setEnabled(true);
								}
								
								//loading data
								SecretItem csi = sh.getSecretitem(csecretpage);
								String context_info = csi.sendername;
								context_info = context_info + "\n\n" + csi.stime; 
								context_info = context_info + "\n\n" + "title: "  + csi.title; 
								context_info = context_info + "\n\n";
								
								StringTokenizer Tok = new StringTokenizer(csi.context.trim(),"\t");
								while (Tok.hasMoreElements())
						  		{
									context_info = context_info+ Tok.nextElement().toString() + "\n\n"; 
								}
								jTextAreaSecret.setText(context_info);
								
								jLabelSecret.setText("page: " + csecretpage + "/" + (totalsize-1));
							} catch (Exception ec) {
							}
						}
					});
			jButtonNextSecret.setMnemonic(java.awt.event.KeyEvent.VK_R);
		}
		return jButtonNextSecret;
	}
	
	private JLabel getLabelSecret() {
		if (jLabelSecret == null) {
			jLabelSecret = new JLabel();
			jLabelSecret.setBounds(new Rectangle(10, 10, 200, 30));
		}
		return jLabelSecret;
	}	

	
	private JScrollPane getJQnaTextArea() {
		if (jspane == null) {
			jTextAreaQna = new JTextArea("");
			jTextAreaQna.setLineWrap(true);
			jspane = new JScrollPane(jTextAreaQna,
			        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jspane.setBounds(new Rectangle(10, 60, 450, 200));
		}
		return jspane;
	}
	
	private JScrollPane getJSecretTextArea() {
		if (jsecretspane == null) {
			jTextAreaSecret = new JTextArea("");
			jTextAreaSecret.setLineWrap(true);
			jsecretspane = new JScrollPane(jTextAreaSecret,
			        ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			        ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			jsecretspane.setBounds(new Rectangle(10, 60, 450, 260));
		}
		return jsecretspane;
	}
	
	//MenuBar
	public JMenuBar getJMenuBar() {
		if (jJMenuBar == null) {
			jJMenuBar = new JMenuBar();
			jJMenuBar.add(getJMenuFile());
			//jJMenuBar.add(getJMenuHandler());
		}
		return jJMenuBar;
	}
	
	public JMenu getJMenuFile() {
		if (jMenuFile == null) {
			jMenuFile = new JMenu();
			jMenuFile.add(getJMenuItemUpdate());
			jMenuFile.add(getJMenuItemMatch());
			jMenuFile.add(getJMenuItemBuy());
			jMenuFile.add(getJMenuItemBuytogether());
			jMenuFile.add(getJMenuIteOutputToExcel());
			jMenuFile.add(getJMenuItemExit());
			jMenuFile.setText("File");
			jMenuFile.setMnemonic(java.awt.event.KeyEvent.VK_F);
		}
		return jMenuFile;
	}
	
	public JMenuItem getJMenuItemUpdate() {
		if (jMenuItemUpdate == null) {
			jMenuItemUpdate = new JMenuItem();
			jMenuItemUpdate.setText("Load ruten");
			jMenuItemUpdate.setMnemonic(java.awt.event.KeyEvent.VK_U);
			jMenuItemUpdate.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_U, java.awt.Event.ALT_MASK,
					false));
			jMenuItemUpdate
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) 
						{
							myruten.getAuthPicture(0);							
						}
					});
		}
		return jMenuItemUpdate;
	}
	
	public JMenuItem getJMenuItemMatch() {
		if (jMenuItemMatch == null) {
			jMenuItemMatch = new JMenuItem();
			jMenuItemMatch.setText("Account Match");
			jMenuItemMatch.setMnemonic(java.awt.event.KeyEvent.VK_M);
			jMenuItemMatch.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_M, java.awt.Event.ALT_MASK,
					false));
			jMenuItemMatch
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) 
						{
							FileDialog fd=new FileDialog(new Frame(),"InputData",FileDialog.LOAD);
							 FilenameFilter ff=new FilenameFilter(){
							   public boolean accept(File dir, String name) 
							   {
							    if (name.endsWith("xls")){
							     return true;
							    }
							    return false;
							   }
							  };
							  
							 //Loading Excel
							 fd.setFilenameFilter(ff);
							 fd.setVisible(true);
							 myexcel.Loadexcel(fd.getDirectory()+fd.getFile());
							 
							 Order info;
							 for (int i=0;i<ri.getOrderlength(); i++)
							 {
								 info = ri.getOrderiinfo(i);
								 if (info.no_check == false)
								 {
									 continue;
								 }
								 else
								 {
									 int money = 0;
									 try
									 {
									    money = Integer.parseInt(info.account_cost);
									 	
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
									 }
									 catch (Exception error)
									 {
										 error.printStackTrace();
									 }
									 
									 int re = myexcel.matchAccount(info.account_bankno, money);
								 }
							 }
							 
							 
							 //myexcel.printfData();
						}
					});
		}
		return jMenuItemMatch;
	}

	public JMenuItem getJMenuItemBuy() {
		if (jMenuItemBuy == null) {
			jMenuItemBuy = new JMenuItem();
			jMenuItemBuy.setText("BuyHandler");
			jMenuItemBuy.setMnemonic(java.awt.event.KeyEvent.VK_B);
			jMenuItemBuy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_B, java.awt.Event.ALT_MASK,
					false));
			jMenuItemBuy
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) 
						{
							
						}
					});
		}
		return jMenuItemBuy;
	}
	
	public JMenuItem getJMenuItemBuytogether() {
		if (jMenuItemBuy == null) {
			jMenuItemBuy = new JMenuItem();
			jMenuItemBuy.setText("CreateBuytogether");
			jMenuItemBuy.setMnemonic(java.awt.event.KeyEvent.VK_U);
			jMenuItemBuy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_U, java.awt.Event.ALT_MASK,
					false));
			jMenuItemBuy
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) 
						{
							
						}
					});
		}
		return jMenuItemBuy;
	}
	
	public JMenuItem getJMenuIteOutputToExcel() {
		if (jMenuItemExcel == null) {
			jMenuItemExcel = new JMenuItem();
			jMenuItemExcel.setText("OutputToExcel");
			jMenuItemExcel.setMnemonic(java.awt.event.KeyEvent.VK_U);
			jMenuItemExcel.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_U, java.awt.Event.ALT_MASK,
					false));
			jMenuItemExcel
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) 
						{
							myruten.getAuthPicture(1);
						}
					});
		}
		return jMenuItemExcel;
	}
	

	public JMenuItem getJMenuItemExit() {
		if (jMenuItemExit == null) {
			jMenuItemExit = new JMenuItem();
			jMenuItemExit.setText("Exit");
			jMenuItemExit.setMnemonic(java.awt.event.KeyEvent.VK_X);
			jMenuItemExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
					java.awt.event.KeyEvent.VK_X, java.awt.Event.ALT_MASK,
					false));
			jMenuItemExit
					.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							System.exit(0);
						}
					});
		}
		return jMenuItemExit;
	}

    
    public static void main(String[] args) throws Exception 
    {
    	Loginruten myruten = new Loginruten(null, null, null, null);
    	myruten.getAuthPicture(1);		
    	/*
    	rutenGraphic app = new rutenGraphic();
		app.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent evt) {
				System.exit(0);
			}
		});
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = app.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		app.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		app.setVisible(true);
		*/
    }
}