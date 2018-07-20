/*
 * Copyright 2017 lisongting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.iscas.xlab.xbotplayer.mvp.robot_state;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import cn.iscas.xlab.xbotplayer.App;
import cn.iscas.xlab.xbotplayer.Config;
import cn.iscas.xlab.xbotplayer.Constant;
import cn.iscas.xlab.xbotplayer.R;
import cn.iscas.xlab.xbotplayer.RosConnectionReceiver;
import cn.iscas.xlab.xbotplayer.customview.CustomSeekBar;
import cn.iscas.xlab.xbotplayer.customview.PercentCircleView;
import cn.iscas.xlab.xbotplayer.entity.RobotState;

/**
 * Created by lisongting on 2017/11/14.
 */

public class RobotStateFragment extends Fragment implements RobotStateContract.View {
    private static final String TAG = "RobotStateFragment";

    private PercentCircleView batteryView;
    private CustomSeekBar cloudDegreeSeekBar;
    private CustomSeekBar liftHeightSeekBar;
    private CustomSeekBar cameraDegreeSeekBar;
    private RobotStateContract.Presenter presenter;
    private BroadcastReceiver receiver;
    private Switch switcher;

    public RobotStateFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_robot_state, container, false);
        batteryView = (PercentCircleView) view.findViewById(R.id.battery_view);
        cloudDegreeSeekBar = (CustomSeekBar) view.findViewById(R.id.seekbar_cloud_degree);
        liftHeightSeekBar = (CustomSeekBar) view.findViewById(R.id.seekbar_lift_height);
        cameraDegreeSeekBar = (CustomSeekBar) view.findViewById(R.id.seekbar_camera_degree);
        switcher = (Switch) view.findViewById(R.id.switcher);

        initListeners();

        return view;
    }

    @Override
    public void initView() {

    }

    private void initListeners() {

        switcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (presenter!=null && Config.isRosServerConnected) {
                    if (switcher.isChecked()) {
                        presenter.publishElectricMachineryMsg(true);
                    } else {
                        presenter.publishElectricMachineryMsg(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "Ros服务器未连接", Toast.LENGTH_SHORT).show();
                    if (presenter == null) {
                        Log.e(TAG, "presenter is null");
                    }
                }
            }
        });
        liftHeightSeekBar.setOnSeekChangeListener(new CustomSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(int value) {
            }

            @Override
            public void onProgressChangeCompleted(int value) {
                log("liftHeightSeekBar value change complete :" + value);
                if (presenter != null && Config.isRosServerConnected) {
                    presenter.publishLiftMsg(value);
                } else {
                    Toast.makeText(getActivity(), "Ros服务器未连接", Toast.LENGTH_SHORT).show();
                    if (presenter == null) {
                        Log.e(TAG, "presenter is null");
                    }
                }
            }
        });

        cloudDegreeSeekBar.setOnSeekChangeListener(new CustomSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(int value) {
            }

            @Override
            public void onProgressChangeCompleted(int value) {
                log("cloudDegreeSeekBar value change complete :" + value);
                if (presenter != null && Config.isRosServerConnected ) {
                    presenter.publishCloudCameraMsg(value,  cameraDegreeSeekBar.getRealValue());
                }else {
                    Toast.makeText(getActivity(), "Ros服务器未连接", Toast.LENGTH_SHORT).show();
                    if (presenter == null) {
                        Log.e(TAG, "presenter is null");
                    }
                }
            }
        });

        cameraDegreeSeekBar.setOnSeekChangeListener(new CustomSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(int value) {
            }

            @Override
            public void onProgressChangeCompleted(int value) {
                log("cameraDegreeSeekBar value change complete:" + value);
                if (presenter != null && Config.isRosServerConnected ) {
                    presenter.publishCloudCameraMsg(cloudDegreeSeekBar.getRealValue(),value);
                }else {
                    Toast.makeText(getContext(), "Ros服务器未连接", Toast.LENGTH_SHORT).show();
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
                        presenter = new RobotStatePresenter(RobotStateFragment.this);
                    }
                    presenter.setServiceProxy(app.getRosServiceProxy());
                    presenter.subscribeRobotState();

                }
            }

            @Override
            public void onFailure() {
                batteryView.stopAnimation();
                Config.isRosServerConnected = false;
            }
        });

        IntentFilter filter = new IntentFilter(Constant.ROS_RECEIVER_INTENTFILTER);
        getActivity().registerReceiver(receiver,filter);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        log("onCreate()");
        super.onCreate(savedInstanceState);
        initBroadcastReceiver();
    }

    @Override
    public void onResume() {
        log("onResume()");
        super.onResume();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        log("isHidden:" + hidden);
        super.onHiddenChanged(hidden);
        if (!hidden && Config.isRosServerConnected) {
            App app = (App) (getActivity().getApplication());
            if (presenter == null) {
                presenter = new RobotStatePresenter(this);
                presenter.setServiceProxy(app.getRosServiceProxy());
                presenter.subscribeRobotState();
                presenter.start();
            }
        }
    }



    @Override
    public void setPresenter(RobotStateContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void updateRobotState( RobotState state) {
        final int percent = state.getPowerPercent();
        final int cloudDegree = state.getCloudDegree();
        final int cameraDegree = state.getCameraDegree();
        final int heightPercent = state.getHeightPercent();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                batteryView.setPercent(percent);
                cloudDegreeSeekBar.setValue(cloudDegree);
                cameraDegreeSeekBar.setValue(cameraDegree);
                liftHeightSeekBar.setValue(heightPercent);
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.unSubscribeRobotState();
        }
    }

    private void log(String s){
        Log.i(TAG,TAG+" -- "+ s);
    }
}
