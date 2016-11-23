package domain.eventbus.com.eventbusdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import de.greenrobot.event.EventBus;
import domain.eventbus.com.eventbusdemo.control.MyEvent;

/**
 * Created by zjx on 16-11-22.
 */

public class SecondActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_main);
        Button bt = (Button) findViewById(R.id.second_activity_bt);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.second_activity_bt:
                EventBus.getDefault().post(new MyEvent("test event content !"));
                break;
        }
    }
}
