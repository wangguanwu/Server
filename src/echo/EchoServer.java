package echo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class EchoServer {

    public static int DEFALUT_PORT = 34567;
    public static void main(String args[]){
        int port ;
        port = 12567;
        System.out.println("    Listening on port "+port);
        ServerSocketChannel serverSocketChannel ;
        Selector selector ;
        try{
            serverSocketChannel = ServerSocketChannel.open();;
            ServerSocket ss = serverSocketChannel.socket();
            System.out.println(ss.getReuseAddress());
            InetSocketAddress address = new InetSocketAddress(port);
            ss.bind(address);
            serverSocketChannel.configureBlocking(false) ;
            selector = Selector.open();
            serverSocketChannel.register(selector , SelectionKey.OP_ACCEPT);
        }catch ( IOException e ){
            e.printStackTrace();
            return ;
        }
        while (true) {
            try{
                selector.select() ;
            }catch (IOException e){
                e.printStackTrace();
                break ;
            }
        }
        Set<SelectionKey> readyKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = readyKeys.iterator() ;
        while ( iterator.hasNext() ){
            SelectionKey key = iterator.next();
            iterator.remove();
            try{
                if(key.isAcceptable()){
                    ServerSocketChannel server = (ServerSocketChannel) key.channel() ;
                    SocketChannel client =  server.accept() ;
                    System.out.println( "Accepted connection from "+client) ;
                    client.configureBlocking(false);
                    SelectionKey clientKey = client.register(selector ,SelectionKey.OP_WRITE |SelectionKey.OP_READ);
                    ByteBuffer buffer = ByteBuffer.allocate(100);
                    clientKey.attach(buffer);
                }
                if( key.isReadable()){
                    SocketChannel client = (SocketChannel)key.channel();
                    ByteBuffer output=  (ByteBuffer)key.attachment();
                    client.read(output);
                }
                if(key.isWritable()){
                    SocketChannel client = (SocketChannel)key.channel();
                    ByteBuffer output = (ByteBuffer)key.attachment();
                    output.flip();
                    client.write(output);
                    output.compact();
                }
            }catch( IOException e ){
                key.cancel();
                try{
                    key.channel().close();
                }catch (IOException ex){

                }
            }
        }
    }
}
