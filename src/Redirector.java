import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Redirector {
    private static final Logger logger = Logger.getLogger("RedirectorServer");
    private final int port ;
    private final String theSite ;

    public Redirector(int port, String theSite) {
        this.port = port;
        this.theSite = theSite;
    }

    public void start(){
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            logger.info("Redirecting connections on port "+serverSocket.getLocalPort()+" to "+this.theSite);
            logger.info("Date to be sent:");
            while(true){
                try{
                    Socket connection = serverSocket.accept() ;
                    pool.submit(new HttpHandler(connection));
                }catch(IOException e){
                    logger.log(Level.WARNING,"Exception Redirecting connection",e);
                }catch(RuntimeException ex){
                    logger.log(Level.SEVERE,"Unexpected Exception",ex);
                }
            }
        }catch(IOException ex){
            logger.log(Level.SEVERE,"Unexpected error");
        }
    }
    private class HttpHandler implements Callable<Void>{
        private Socket connection;

        public HttpHandler(Socket connection) {
            this.connection = connection;
        }

        @Override
        public Void call() throws IOException {
            try{
              Writer out = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(),"US-ASCII"));
              Reader in = new InputStreamReader(new BufferedInputStream(connection.getInputStream()));
              StringBuilder request = new StringBuilder(80);
              while(true){
                  int c = in.read();
                  if( c == '\r'|| c== '\n'|| c==-1) break;
                  request.append((char)c) ;
              }
              String get = request.toString();
              String pieces[] = get.split("\\w*");
              String theFile = pieces[1];
              if( get.indexOf("HTTP")!=-1){
                  out.write("HTTP://1.0 302 FOUND \r\n");
                  Date date = new Date();
                  out.write("Date: "+date+"\r\n");
                  out.write("Server: Redirector 1.1\r\n");
                  out.write("Location: "+theSite+theFile+"\r\n");
                  out.write("Content-type: text/html\r\n\r\n");
                  out.flush();
              }
                out.write("<HTML><HEAD><TITLE>Document moved</TITLE></HEAD>\r\n");
                out.write("<BODY><H1>Document moved</H1>\r\n");
                out.write("The document " + theFile
                        + " has moved to\r\n<A HREF=\"" + theSite + theFile + "\">"
                        + theSite + theFile
                        + "</A>.\r\n Please update your bookmarks<P>");
                out.write("</BODY></HTML>\r\n");
                out.flush();
                logger.log(Level.INFO,
                        "Redirected " + connection.getRemoteSocketAddress());
            } catch(IOException ex) {
                logger.log(Level.WARNING,
                        "Error talking to " + connection.getRemoteSocketAddress(), ex);
            }finally {
                connection.close();
            }
            return null;
        }
    }

    public static void main(String args[]){
        int port = 45679;
        String encoding = "UTF-8";
        String theSite= "http://www.baidu.com";

        try{
            Redirector server = new Redirector(port ,theSite);
            server.start();
        }catch ( RuntimeException e ){
            logger.severe(e.getMessage());
        }
    }
}
