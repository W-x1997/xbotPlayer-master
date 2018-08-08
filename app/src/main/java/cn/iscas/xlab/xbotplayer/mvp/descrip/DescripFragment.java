package cn.iscas.xlab.xbotplayer.mvp.descrip;

import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.iscas.xlab.xbotplayer.App;
import cn.iscas.xlab.xbotplayer.Config;
import cn.iscas.xlab.xbotplayer.Constant;
import cn.iscas.xlab.xbotplayer.R;
import cn.iscas.xlab.xbotplayer.RosConnectionReceiver;
import cn.iscas.xlab.xbotplayer.customview.MapView2;
import cn.iscas.xlab.xbotplayer.mvp.rvizmap.MapContract;

/**
 * Created by wx on 2018/7/25.
 */

public class DescripFragment extends Fragment implements MapContract.View{


    /**
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_desc, null);
        return v;
    }
    **/
    public static final String TAG = "DescFragment";
    private MapView2 mapView;
    private MapContract.Presenter presenter ;
    private Button toggleMap;
    private Button resetButton;
    private boolean isMapOpened;
    private SwipeRefreshLayout refreshLayout;
    private RosConnectionReceiver receiver;
    private TextView sweep_area;   //扫地面积
    private TextView power; //剩余电量
    private TextView sweep_time;//  清扫时间

    public DescripFragment(){
        log("DescripFragment() Created()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        log("onCreateView()");
        View view = inflater.inflate(R.layout.fragment_desc,container,false);
        mapView = (MapView2) view.findViewById(R.id.map_view2);
        toggleMap = (Button) view.findViewById(R.id.toggleMap);
        resetButton = (Button) view.findViewById(R.id.reset);
        refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refresh_layout);
        sweep_area=(TextView)view.findViewById(R.id.sweep_area);
        power=(TextView)view.findViewById(R.id.power_state);
        sweep_time=(TextView)view.findViewById(R.id.sweep_time);
        initView();
        initListeners();


         //Linearlayout点击事件
        View gowhere = view.findViewById(R.id.go_where);                //指哪到哪
        gowhere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 具体实现：
                log("----GOWHERE-----");
            }
        });



        View charge = view.findViewById(R.id.charge);                //回充
        charge.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 具体实现：
                log("-----CHARGE----");
            }
        });


        View sweep = view.findViewById(R.id.sweep);                //清扫
        sweep.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 具体实现：
                log("SWEEP:::");
            }
        });


        View choose_area = view.findViewById(R.id.choose_area);               //划区清扫
        choose_area.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 具体实现：
                log("CHOOSE_AREA");
            }
        });

        return view;
    }




        @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        log("onResume()");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        log("onActivityCreate()");
        initBroadcastReceiver();
    }

    @Override
    public void showLoading() {
        refreshLayout.setRefreshing(true);
        Toast.makeText(getContext(), "等待地图数据更新，请稍候", Toast.LENGTH_LONG).show();
    }

    @Override
    public void hideLoading() {
        if (refreshLayout != null) {
            refreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void updateMap(Bitmap mapInfo) {
        mapView.updateMap(mapInfo);
    }

    @Override
    public void initView() {
        refreshLayout.setEnabled(false);
        refreshLayout.setColorSchemeResources(android.R.color.holo_purple
                ,android.R.color.holo_orange_light
                ,android.R.color.holo_blue_light);
    }

    private void initListeners() {
        toggleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMapOpened) {
                    if (!Config.isRosServerConnected) {
                        Toast.makeText(getContext(), "Ros服务器未连接", Toast.LENGTH_SHORT).show();
                    } else{
                        showLoading();
                        isMapOpened = true;
                        toggleMap.setText("MAP_CONCEAL");
                        presenter.subscribeMapData();
                    }
                } else {
                    presenter.unSubscribeMapData();
                    presenter.abortLoadMap();
                    isMapOpened = false;
                    toggleMap.setText("MAP_SHOW");
                    refreshLayout.setRefreshing(false);
                    mapView.updateMap(null);
                }
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isMapOpened) {
                    mapView.reset();
                }
            }


        });
    }


    private void initBroadcastReceiver() {
        receiver = new RosConnectionReceiver(new RosConnectionReceiver.RosCallback() {
            @Override
            public void onSuccess() {
                if (!Config.isRosServerConnected) {
                    Toast.makeText(getContext(), "Ros服务端连接成功", Toast.LENGTH_SHORT).show();
                    App app = (App) (getActivity().getApplication());
                    if (presenter == null) {
                        presenter = new DesPresenter(DescripFragment.this);
                        presenter.start();
                    }
                    presenter.setServiceProxy(app.getRosServiceProxy());
                }
            }

            @Override
            public void onFailure() {
                Config.isRosServerConnected = false;
            }
        });

        IntentFilter filter = new IntentFilter(Constant.ROS_RECEIVER_INTENTFILTER);
        getActivity().registerReceiver(receiver,filter);

    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        if (presenter != null) {
            presenter.destroy();
        }
        getActivity().unregisterReceiver(receiver);
        super.onDestroy();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        log("idHidden:" + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden ) {
            if (Config.isRosServerConnected) {
                App app = (App) (getActivity().getApplication());
                if (presenter == null) {
                    presenter = new DesPresenter(this);
                    presenter.setServiceProxy(app.getRosServiceProxy());
                    presenter.start();
                }

            }

        }
    }

    @Override
    public void setPresenter(MapContract.Presenter presenter) {
        this.presenter = presenter;
    }


    @Override
    public Size getMapRealSize() {
        return new Size(mapView.getWidth(), mapView.getHeight());
    }

    private void log(String string) {
        Log.i(TAG,TAG + " -- " + string);
    }
}
