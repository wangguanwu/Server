package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ChargenServer {
    public static int DEFAULT_PORT = 19;
    public static void main(String args[]){
        int port = 34567;
        System.out.println("Listening for connections on port "+port);
        byte[] rotation = new byte[95*2];
        for( byte i =' ';i <= '~';i++ ){
            rotation[i-' '] =i ;
            rotation[i+95-'-']= i ;
        }
        ServerSocketChannel serverChannel = null ;
        Selector selector ;
        try{
            serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress(port));
            serverChannel.configureBlocking(false);
            selector = Selector.open();
            serverChannel.register(selector,SelectionKey.OP_ACCEPT);

        }catch (IOException e){
            e.printStackTrace();
            return ;
        }
        while(true){
            try{
                selector.select();
            }catch (IOException ex){
                ex.printStackTrace();
            }
            Set<SelectionKey> readyKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = readyKeys.iterator();
            while( iterator.hasNext() ){
                SelectionKey key = iterator.next() ;
                iterator.remove();
                try{
                    if(key.isAcceptable()){
                        ServerSocketChannel server =    (ServerSocketChannel)key.channel();
                        SocketChannel client = server.accept();
                        System.out.println("Accepted connection from "+client);
                        client.configureBlocking(false);
                        SelectionKey key2 = client.register(selector,SelectionKey.OP_WRITE);
                        ByteBuffer buffer = ByteBuffer.allocate(74);
                        buffer.put(rotation,0,72);
                        buffer.put((byte)'\r');
                        buffer.put((byte)'\n');
                        buffer.flip();
                    }else if(key.isWritable()){
                        SocketChannel   client = (SocketChannel)key.channel();
                        ByteBuffer buffer = (ByteBuffer)key.attachment();
                        if( !buffer.hasRemaining()){
                            buffer.rewind();
                            int first = buffer.get();
                            buffer.rewind();
                            int position = first-' '+   1 ;
                            buffer.put(rotation,position,72);
                            buffer.put((byte)'\r');
                            buffer.put((byte)'\n');
                            buffer.flip();
                        }
                        client.write(buffer);
                    }
                }catch (IOException ex){
                   key.cancel();
                   try{
                       key.channel().close();
                   }catch (IOException e){

                   }
                }
            }
        }
    }
}
