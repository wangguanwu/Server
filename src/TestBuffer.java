import java.nio.ByteBuffer;
import java.util.Arrays;

public class TestBuffer {
    public static void main(String args[]){
        ByteBuffer bt = ByteBuffer.allocate(74);
        bt.clear();
        System.out.println(bt);
        bt.position(5);
        bt.mark();
        System.out.println(bt);
        bt.position(10);
        System.out.println(bt);
        bt.reset();
        System.out.println(bt);
        bt.clear();
        System.out.println("after clear:"+bt);
        bt.put("abcd".getBytes());
        System.out.println("before compact :"+bt);
//        bt.compact();
//        System.out.println("after compact: "+bt+" ");
//   //        System.out.println(new String(bt.array()));
        bt.flip();
        System.out.println("after flip: "+bt);
        System.out.println((char)bt.get());
        System.out.println((char)bt.get());
        System.out.println((char)bt.get());
        System.out.println((char)bt.get());
        System.out.println(new String(bt.array()));
        bt.compact();
        System.out.println("after compact: "+bt+" ");
        System.out.println(new String(bt.array()));
        bt = ByteBuffer.allocate(32);
        bt.put((byte)'a').put((byte)'b').put((byte)'c')
                .put((byte)'d').put((byte)'e').put((byte)'f');
        System.out.println("before flip"+bt);
        bt.flip();
        System.out.println((char)bt.get());
        System.out.println("after flip"+bt);
        System.out.println((char)bt.get(2));
        System.out.println("after get(index)"+bt);
        byte []dst =    new byte[10];
        bt.get(dst,0,2);
        System.out.println("after get(dst,0,2)"+bt);
        System.out.println("dst "+new String(dst));
        System.out.println("buffer now is "+bt);
        System.out.println("\t"+new String(bt.array()));
        ByteBuffer bb = ByteBuffer.allocate(32);
        System.out.println("buffer put(byte) "+bb);
        System.out.println("after put "+bb.put((byte)'z'));
        System.out.println("before put (2,(byte'c') "+bb);
        System.out.println("after put (2,(byte'c')      "+bb.put(2,(byte)'c'));//不改变position的位置
        bb.put(bt);
        System.out.println("after put (buffer) "+bb);
       // bb.flip();
        System.out.println(new String(bb.array()));








    }
}
