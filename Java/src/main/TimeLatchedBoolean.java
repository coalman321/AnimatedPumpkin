package main;

public class TimeLatchedBoolean {

    private boolean aBoolean;
    private boolean defState;
    private long t_start;
    private int duration;

    public TimeLatchedBoolean(boolean defState, int timeout){
        aBoolean = this.defState = defState;
        duration = timeout;
    }

    public TimeLatchedBoolean(int timeout){
        this(false, timeout);
    }

    public void set(boolean state){
        if(aBoolean != state){
            t_start = System.currentTimeMillis();
            aBoolean = state;
        }
    }

    public boolean get(){
        if(t_start + duration < System.currentTimeMillis()) aBoolean = defState;
        return aBoolean;
    }
}
