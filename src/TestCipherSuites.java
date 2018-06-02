import javax.net.ssl.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class TestCipherSuites {
    public static void main(String args[])throws IOException,SSLException {
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        String allSuites[] = factory.getSupportedCipherSuites();
        SSLSocket sslSocket =(SSLSocket)factory.createSocket();
        sslSocket.bind(new InetSocketAddress(45543));
        sslSocket.getEnabledCipherSuites();
        SSLSession sslSession =sslSocket.getSession();
        sslSocket.startHandshake();
        sslSocket.setUseClientMode(true);
        SSLServerSocket sslServerSocket =(SSLServerSocket) SSLServerSocketFactory.getDefault().createServerSocket();
        for(String s : allSuites)
          System.out.println(s);
        System.out.println("-------------------------------------");
        String allEnabledSuites [] = sslSocket.getEnabledCipherSuites();
        for(String s : allEnabledSuites)
            System.out.println(s);
    }
}
