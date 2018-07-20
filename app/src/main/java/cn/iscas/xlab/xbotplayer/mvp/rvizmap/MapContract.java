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
package cn.iscas.xlab.xbotplayer.mvp.rvizmap;

import android.graphics.Bitmap;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.util.Size;

import cn.iscas.xlab.xbotplayer.mvp.BasePresenter;
import cn.iscas.xlab.xbotplayer.mvp.BaseView;

/**
 * Created by lisongting on 2017/10/9.
 */


public interface MapContract {

    interface Presenter extends BasePresenter{
        void setServiceProxy(@NonNull Binder binder);

        void abortLoadMap();

        void destroy();

        void subscribeMapData();

        void unSubscribeMapData();

    }

    interface View extends BaseView<Presenter> {

        void showLoading();

        void hideLoading();

        void updateMap(Bitmap mapInfo);

        Size getMapRealSize();

    }
}
