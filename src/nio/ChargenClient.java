package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;

public class ChargenClient {
    private static int DEFUALT_PORT =   19 ;
    public static void main(String args[]){
        int port = 45678;
        try{
            SocketAddress address = new InetSocketAddress("www.baidu.com",port);
            SocketChannel client = SocketChannel.open( address ) ;
            ByteBuffer buffer = ByteBuffer.allocate(74);
            WritableByteChannel out = Channels.newChannel(System.out);
            client.configureBlocking(false);
            while ( client.read(buffer)!=-1){
                buffer.flip();
                out.write(buffer);
                buffer.clear();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
