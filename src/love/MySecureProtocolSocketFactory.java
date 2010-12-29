package love;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

class Java2000TrustManager implements X509TrustManager {      
  Java2000TrustManager() {      
  }      
   public void checkClientTrusted(X509Certificate chain[], String authType) throws CertificateException {      
     //System.out.println("�ˬd�Ȥ�ݪ��i�H�����A...");      
   }      
   public void checkServerTrusted(X509Certificate chain[], String authType) throws CertificateException {      
     //System.out.println("�ˬd�A�Ⱦ����i�H�����A");      
   }      
   public X509Certificate[] getAcceptedIssuers() {      
     //System.out.println("����o��ӼƲ�...");      
     return null;      
   }      
 }     
   