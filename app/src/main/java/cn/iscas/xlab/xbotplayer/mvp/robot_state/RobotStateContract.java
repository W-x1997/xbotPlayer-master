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

import android.os.Binder;
import android.support.annotation.NonNull;

import cn.iscas.xlab.xbotplayer.entity.RobotState;
import cn.iscas.xlab.xbotplayer.mvp.BasePresenter;
import cn.iscas.xlab.xbotplayer.mvp.BaseView;

/**
 * Created by lisongting on 2017/11/14.
 */

public interface RobotStateContract {

    interface Presenter extends BasePresenter{

        void setServiceProxy(@NonNull Binder binder);

        void subscribeRobotState();

        void unSubscribeRobotState();

        void publishLiftMsg(int heightPercent);

        void publishCloudCameraMsg(int cloudDegree,int cameraDegree);

        void publishElectricMachineryMsg(boolean activate);

    }


    interface View extends BaseView<Presenter> {
        void updateRobotState(RobotState state);

    }
}

