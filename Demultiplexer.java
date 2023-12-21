package SD2324;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import SD2324.TaggedConnection.Frame;

public class Demultiplexer {
    
    private TaggedConnection conn;
    private ReentrantLock lock;   
    
    private class Entry{
        Condition cond = lock.newCondition();
        Deque<byte[]> queue = new ArrayDeque<>();
        int waiters = 0;
    }
    
    private Map<Integer,Entry> map;
    IOException ioe = null;
    
    public Demultiplexer(TaggedConnection conn) {
        this.conn = conn;
        this.lock = new ReentrantLock();
        this.map = new HashMap<>(); 
    }
    
    private Entry get(int tag){
        Entry entry = map.get(tag);
        if(entry == null){
            entry = new Entry(); 
            map.put(tag, entry);
        }
        return entry;
    }
    
    public void start() {
        new Thread(() -> {
            try {
                while(true) {
                    Frame f = this.conn.receive();
                    //System.out.println("Já estabeleci conexão");
                    
                    int tag = f.getTag();
                    byte[] bytes = f.getData();
                    
                    lock.lock();
                    try{
                        Entry entry = get(tag);
                        //System.out.println("Já recebi a tag do start");
                        entry.queue.add(bytes);
                        //System.out.println("Já adicionei à queue");
                        entry.cond.signal();
                        //System.out.println("Já dei signal");
                    } finally {
                        lock.unlock();
                    }
                    
                } 
            } catch (IOException e) {
                lock.lock();
                try{
                    ioe = e;
                    for(Entry entry : map.values()) entry.cond.signalAll();
                
                } finally {
                    lock.unlock();
                }
            }
        }).start();
    }
    
    public void send(Frame frame) throws IOException {
        conn.send(frame);
    }
    
    public void send(int tag, byte[] data) throws IOException {
        //System.out.println("Estou prestes a enviar");
        conn.send(tag,data);
        //System.out.println("Já tentei enviar");
    }
    
    public byte[] receive(int tag) throws IOException, InterruptedException {
        
        lock.lock();
        try{
            
            Entry entry = get(tag);
            //System.out.println("Tag recebida");
            
            entry.waiters++;
            //System.out.println("Adicionei um à waiters");
            
            while(entry.queue.isEmpty()){ 
                //System.out.println("Estou preso na waiters");
                entry.cond.await();
                //System.out.println("Estou preso na waiters 2");
            }
            //System.out.println("Saí do await");
            entry.waiters--;

            //System.out.println("Vou tentar dar poll");
            byte[] info = entry.queue.poll();
            //System.out.println("Já dei poll");
            
            if(entry.waiters == 0 && entry.queue.isEmpty())
            //System.out.println("Vou tentar Remover o map");
            map.remove(tag);
            //System.out.println("Dei remove do map");
            
            
            return info;
            
        } finally {
            lock.unlock();
        }
        
    }
    
    public void close() throws IOException {
        conn.close();
    }
}
