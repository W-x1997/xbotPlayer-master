package cn.iscas.xlab.xbotplayer.mvp.descrip;

import android.graphics.Bitmap;
import android.os.Binder;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Size;

import org.json.JSONException;
import org.json.JSONObject;

import cn.iscas.xlab.xbotplayer.Constant;
import cn.iscas.xlab.xbotplayer.RosConnectionService;
import cn.iscas.xlab.xbotplayer.mvp.rvizmap.MapContract;
import cn.iscas.xlab.xbotplayer.util.ImageUtils;
import de.greenrobot.event.EventBus;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wx on 2018/7/25.
 */

public class DesPresenter implements MapContract.Presenter{
    private static final String TAG = "MapPresenter";
    private MapContract.View view ;
    private RosConnectionService.ServiceBinder serviceProxy;
    private CompositeDisposable compositeDisposable;
    private Size mapSize;

    public DesPresenter(MapContract.View view) {
        this.view = view;
    }

    @Override
    public void start() {
        compositeDisposable = new CompositeDisposable();

        EventBus.getDefault().register(this);

    }

    @Override
    public void setServiceProxy(@NonNull Binder binder) {
        serviceProxy = (RosConnectionService.ServiceBinder) binder;

    }



    public void onEvent(final String jsonString) throws JSONException {

        mapSize = view.getMapRealSize();

        JSONObject json = new JSONObject(jsonString);
        if (!json.get("topicName").equals(Constant.SUBSCRIBE_TOPIC_MAP)) {
            return;
        }
        final String base64Map = json.getString("data");

        Disposable disposable = Observable.just(base64Map)
                .subscribeOn(Schedulers.computation())        //子线程computation发射
                .map(new Function<String, Bitmap>() {      // map 操作符，就是转换输入、输出 的类型；本例中输入是 String, 输出是 Bitmap 类型
                    @Override
                    public Bitmap apply(@io.reactivex.annotations.NonNull String mapInfo) throws Exception {
                        return ImageUtils.decodeBase64ToBitmap2(base64Map,1,mapSize);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())       //主线程接受
                .subscribeWith(new DisposableObserver<Bitmap>() {
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Bitmap bitmap) {

                        log("onNext()");
                        if (!isDisposed()) {
                            view.updateMap(bitmap);
                        }
                        view.hideLoading();
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        log("onError()");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                        log("onComplete() -- map loaded successfully");
                    }
                });
        compositeDisposable.add(disposable);
    }

    @Override
    public void destroy() {
        EventBus.getDefault().unregister(this);
        if (compositeDisposable != null) {
            compositeDisposable.clear();
        }
    }

    @Override
    public void abortLoadMap() {
        compositeDisposable.clear();
    }

    @Override
    public void subscribeMapData() {
        if (serviceProxy != null) {
            serviceProxy.manipulateTopic(Constant.SUBSCRIBE_TOPIC_MAP,true);
        } else {
            Log.e(TAG, "serviceProxy is null");
        }
    }

    @Override
    public void unSubscribeMapData() {
        if (serviceProxy != null) {
            serviceProxy.manipulateTopic(Constant.SUBSCRIBE_TOPIC_MAP,false);
        } else {
            Log.e(TAG, "serviceProxy is null");
        }
    }

    private void log(String s) {
        Log.i(TAG, TAG + " -- " + s);
    }

}
