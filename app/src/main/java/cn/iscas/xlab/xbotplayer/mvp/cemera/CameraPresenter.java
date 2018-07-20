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
package cn.iscas.xlab.xbotplayer.mvp.cemera;

import android.content.Context;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.util.Log;

import cn.iscas.xlab.xbotplayer.RosConnectionService;
import cn.iscas.xlab.xbotplayer.entity.Twist;
import de.greenrobot.event.EventBus;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by lisongting on 2017/10/20.
 */

public class CameraPresenter implements CameraContract.Presenter {

    public static final String TAG = "CameraPresenter";
    private CameraContract.View view;
    private Context context;
    private RosConnectionService.ServiceBinder serviceProxy;
    private CompositeDisposable compositeDisposable;



    public CameraPresenter(Context context, CameraContract.View view) {
        this.context = context;
        this.view = view;
        compositeDisposable = new CompositeDisposable();
    }


    @Override
    public void start() {
    }


    @Override
    public void setServiceProxy(@NonNull Binder binder) {
        serviceProxy = (RosConnectionService.ServiceBinder) binder;
    }

    @Override
    public void destroy() {
        compositeDisposable.clear();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void publishCommand(Twist twist) {
        if (serviceProxy != null) {
            serviceProxy.publishCommand(twist);
        }else {
            Log.e("CameraPresenter", "RosConnectionService is null");
        }
    }

    private void log(String string) {
        Log.i(TAG, TAG + " -- " + string);
    }



}
