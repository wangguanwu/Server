import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;

public class HTTPSClient {
    public static void main(String args[]){
        int port = 443 ;
        String host = "www.usps.com";
        SSLSocketFactory factory = (SSLSocketFactory)SSLSocketFactory.getDefault();
        SSLSocket socket = null ;
        try{
            socket = (SSLSocket) factory.createSocket(host,port);
            String []supported = socket.getSupportedCipherSuites();
            socket.setEnabledCipherSuites(supported);
            Writer out = new OutputStreamWriter(socket.getOutputStream(),"UTF-8");
            out.write("GET http://"+host+"/ HTTP/1.1\r\n");
            out.write("Host: "+host+"\r\n");
            out.write("\r\n");
            out.flush();
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String s ;
            while( !(s = in.readLine()).equals("")){
                System.out.println(s);
            }
            System.out.println();
            String contentLength = in.readLine();
            int length = Integer.MAX_VALUE;
            try{
                length = Integer.parseInt(contentLength.trim()  ,16);

            }catch (NumberFormatException n){

            }
            System.out.println(contentLength);
            int c ;
            int i= 0 ;
            while( (c =  in.read())!=-1 && i++ < length){
                System.out.print((char)c);
            }
            System.out.println();

        }catch (IOException e ){
            System.err.println(e);
        }finally {
            try {
                if (socket != null)
                    socket.close();
            }catch (IOException e){

            }
        }
    }
}
