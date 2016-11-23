package domain.eventbus.com.eventbusdemo.control;

/**
 * Created by zjx on 16-11-22.
 */

public class MyEvent {
    private String msg;

    public MyEvent(String msg) {
        this.msg = msg;
    }
    public String getMsg(){
        return this.msg;
    }
}
