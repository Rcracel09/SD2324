package SD2324;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


public class TaggedConnection implements AutoCloseable {
    
    public static class Frame {
        
            public final int tag;
            public final byte[] data;
        
            public Frame(int tag, byte[] data) {
                this.tag = tag;
                this.data = data;
            }

            public int getTag() {
              return tag;
          }
      
          public byte[] getData() {
              return data.clone();
          }
    }

    DataInputStream inputStream;
    private ReentrantLock inputLock;
    
    DataOutputStream outputStream;
    private ReentrantLock outputLock;
    
    private final Socket s;

    public TaggedConnection(Socket socket) throws IOException {
        this.s = socket;
        
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.outputLock = new ReentrantLock();
 
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.inputLock = new ReentrantLock();

    }
    public void send(Frame frame) throws IOException {
        this.send(frame.tag, frame.data);      
    }
    public void send(int tag, byte[] data) throws IOException {
        outputLock.lock();
        try{
            outputStream.writeInt(4 + data.length);//revert o que se passa neste +4 e neste -4 que fiquei confuso
            outputStream.writeInt(tag);
            outputStream.write(data);
            outputStream.flush();

        } finally {
            outputLock.unlock();
        }
    }
    public Frame receive() throws IOException {
        byte[] data;
        Frame currentFrame;
        
        inputLock.lock();
        try{
            int size = this.inputStream.readInt(); 
            int tag = this.inputStream.readInt();
            data = new byte[size - 4];

            this.inputStream.readFully(data);
            
            currentFrame = new Frame(tag ,data);
            return currentFrame;

        } finally {
            inputLock.unlock();
        }
    }
    
    public void close() throws IOException {
        s.close();
    }
}