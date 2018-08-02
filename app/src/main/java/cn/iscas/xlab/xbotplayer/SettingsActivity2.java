package cn.iscas.xlab.xbotplayer;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by wx on 2018/8/2.
 *
 */
public class SettingsActivity2 extends AppCompatActivity {

    public ListView listView;
    public String[] data;
    public ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set2);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("设置");

      data = new String[]{"机器人设置","定时清扫","清扫模式","消息提醒开关","清扫记录","耗材&维护","遥控器","产品指南&客服","通用设置","定位我的机器人"};

            adapter = new ArrayAdapter<String>(SettingsActivity2.this, android.R.layout.simple_list_item_1, data);
            listView = (ListView) findViewById(R.id.list_view);
            listView.setAdapter(adapter);


        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }
}
