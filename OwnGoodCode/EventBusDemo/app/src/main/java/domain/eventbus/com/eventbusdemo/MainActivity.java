package domain.eventbus.com.eventbusdemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import de.greenrobot.event.EventBus;
import domain.eventbus.com.eventbusdemo.control.MyEvent;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "EventBusTAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt = (Button) findViewById(R.id.start_activity_bt);
        bt.setOnClickListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /*方法一*/

    /**
     * 如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，onEventMainThread
     * 都会在UI线程中执行，接收事件就会在UI线程中运行，这个在Android中是非常有用的，因为在Android中只能
     * 在UI线程中跟新UI，所以在onEvnetMainThread方法中是不能执行耗时操作的。
     * @param event
     */
    public void onEventMainThread(MyEvent event){
        Log.d(TAG,"onEventMainThread event:"+event.getMsg()+"  thread id:"+Thread.currentThread().getId());
    }
    /*方法二*/

    /**
     * 如果使用onEvent作为订阅函数，那么该事件在哪个线程发布出来的
     * ，onEvent就会在这个线程中运行，也就是说发布事件和接收事件线程在同
     * 一个线程。使用这个方法时，在onEvent方法中不能执行耗时操作，如果执
     * 行耗时操作容易导致事件分发延迟。
     * @param event
     */
    public void onEvent(MyEvent event){
        Log.d(TAG,"onEvent event:"+event.getMsg()+"  thread id:"+Thread.currentThread().getId());
    }
    /*方法三 不能放在主线程里 ，当前这个位置就会报错*/

    /**
     * 如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，
     * 那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，
     * 那么onEventBackground函数直接在该子线程中执行。
     * @param event
     */
//    public void onEventBackground(MyEvent event){
//        Log.d(TAG,"onEventBackground event:"+event.getMsg()+"  thread id:"+Thread.currentThread().getId());
//    }

    /**
     * 使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行onEventAsync.
     * @param event
     */
    public void onEventAsync(MyEvent event){
        Log.d(TAG,"onEventAsync event:"+event.getMsg()+"  thread id:"+Thread.currentThread().getId());
    }

    @Override
    public void onClick(View v) {
        startActivity(new Intent(MainActivity.this,SecondActivity.class));
    }
}
