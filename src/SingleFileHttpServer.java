import javax.xml.ws.spi.http.HttpHandler;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SingleFileHttpServer {
    private static final Logger logger = Logger.getLogger("SingleFileHttpServer");
    private final byte[] content ;
    private final byte[] header ;
    private final int port ;
    private final String encoding;
    public SingleFileHttpServer(String data,String encoding ,String mimeType,int port)
            throws UnsupportedEncodingException{
        this(data.getBytes(encoding),encoding,mimeType,port) ;
    }
    public SingleFileHttpServer(byte []data ,String encoding,String mimeType,int port){
        this.content = data ;
        this.port = port ;
        this.encoding = encoding ;
        String header =  "HTTP/1.1 200 OK\r\n"
                +"Server:OneFile 2.0\r\n"
                +"Content-length: "+this.content.length+"\r\n"
                +"Content-Type: "+mimeType+" ;charset="+encoding+"\r\n\r\n";
        this.header = header.getBytes(Charset.forName("US-ASCII"));
    }
    public void start(){
        ExecutorService pool = Executors.newFixedThreadPool(100);
        try(ServerSocket serverSocket = new ServerSocket(this.port)){
            logger.info("Accepting connections on port "+serverSocket.getLocalPort());
            logger.info("Date to be sent:");
            logger.info(new String (this.content,encoding));
            while(true){
                try{
                    Socket connection = serverSocket.accept() ;
                    pool.submit(new HttpHandler(connection));
                }catch(IOException e){
                    logger.log(Level.WARNING,"Exception accepting connection",e);
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
                OutputStream out = new BufferedOutputStream(connection.getOutputStream());
                InputStream in = new BufferedInputStream(connection.getInputStream());
                StringBuilder request = new StringBuilder(80);
                while( true ){
                    int c = in.read();
                    if( c == '\r'||c=='\n'||c==-1) break ;
                    request.append((char)c);
                }
                if(request.toString().indexOf("HTTP/")!=-1){
                    out.write(header);
                }
                out.write(content);
                out.flush();
            }catch( IOException ex ){
                logger.log(Level.WARNING,"Error to  client ",ex);
            }finally {
                connection.close();
            }
            return null;
        }
    }

    public static void main(String args[]){
        int port = 45678;
        String encoding = "UTF-8";
        String uri = "D:\\htmlcode\\banana.html";

        try{
            Path path = Paths.get(uri);
            byte data[] = Files.readAllBytes(path);
            String contentType = URLConnection.getFileNameMap().getContentTypeFor(uri);
            SingleFileHttpServer server = new SingleFileHttpServer(data,encoding,contentType,port);
            server.start();
        }catch ( IOException e ){
            logger.severe(e.getMessage());
        }
    }
}
