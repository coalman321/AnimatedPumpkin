package main;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class CircularList<E>{

    private ConcurrentLinkedQueue<E> objects;
    private int len;
    private boolean recentModification = false;

    public CircularList(int samples){
        objects = new ConcurrentLinkedQueue<>();
        len = samples;
    }

    public synchronized void add(E obj){
        recentModification = true;
        objects.add(obj);
        if (objects.size() > len) {
            objects.poll();
        }
    }

    public E peek(){
        return objects.peek();
    }

    public int size(){
        return objects.size();
    }

    public synchronized void addAll(List<E> objects){
        objects.forEach(this::add);
    }

    public boolean hasModified(){
        if(recentModification){
            recentModification = false;
            return true;
        }
        return false;
    }

    public synchronized String toString(){
        StringBuilder builder = new StringBuilder();
        for (E obj : objects) {
            builder.append(obj.toString() + ",");
        }
        return "[" + builder.toString() + "]";
    }

    public ArrayList<E> getList(){
        return new ArrayList<E>(objects);
    }

}
