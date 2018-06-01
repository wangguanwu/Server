import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PooledDayTimeServer {
    public static final  int PORT = 4556;
    public static void main(String args[]) {
        ExecutorService pool = Executors.newFixedThreadPool(50);
        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                try {
                    Socket socket = server.accept();
                    Callable<Void> task = new DaytimeTask( socket ) ;
                    pool.submit( task );
                }catch (IOException e ){

                }
            }
        }catch (IOException ex){
            System.out.println( " Could not start the server "+ex ) ;
        }
    }
    private static class DaytimeTask implements Callable<Void> {
        Socket socket = null ;
        public DaytimeTask( Socket socket ){
            this.socket = socket ;
        }
        @Override
        public Void call() throws Exception {
            try{
                Writer out = new OutputStreamWriter( socket.getOutputStream() ) ;
                Date now = new Date() ;
                out.write(now.toString()+"\r\n");
                out.flush();
            }catch (IOException e){
                System.err.println(e);
            }finally {
                try{
                    socket.close();
                }catch (IOException e){

                }
            }
            return null;
        }
    }

}
