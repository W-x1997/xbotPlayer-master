# MAPVIEW模块2.0

写作者  ： weixin

修改日期： 2018 年 8月 8 日




## 关键代码说明 

下面对一些关键代码说明，下面贴出的是部分代码片段，阅读下面的内容需要结合[源码]查看。

### 先原理进行说明


   mapview2为自定义的控件  
   原理介绍：
1.View表示的的屏幕上的某一块矩形的区域，而且所有的View都是矩形的；
2.View是不能添加子View的，而ViewGroup是可以添加子View的。ViewGroup之所以能够添加子View，是因为它实现了两个接口：ViewParent ViewManager；
3.Activity之所以能加载并且控制View，是因为它包含了一个Window，所有的图形化界面都是由View显示的而Service之所以称之为没有界面的activity是因为它不包含有Window,不能够加载View；
4.一个View有且只能有一个父View;
5.在Android中Window对象通常由PhoneWindow来实现的，PhoneWindow将一个DecorView设置为整个应用窗口的根View,即DecorView为整个Window界面的最顶层View。也可说DecorView将要显示的具体内容呈现在了PhoneWindow上；
6.DecorView是FrameLayout的子类,它继承了FrameLayout，即顶层的FrameLayout的实现类是Decorview，它是在phoneWindow里面创建的；
7.顶层的FrameLayout的父view是Handler，Handler的作用除了线程之间的通讯以外，还可以跟WindowManagerService进行通讯;
8.windowManagerService是后台的一个服务，它控制并且管理者屏幕;
9.一个应用可以有很多个window，其由windowManager来管理，而windowManager又由windowManagerService来管理;
10.如果想要显示一个view那么他所要经历三个方法：1.测量measure, 2.布局layout, 3.绘制draw。


view的测量/布局/绘制过程
显示一个View主要进过以下三个步骤：

1、Measure测量一个View的大小
2、Layout摆放一个View的位置
3、Draw画出View的显示内容
View的测量
Android系统在绘制View之前，必须对View进行测量，即告诉系统该画一个多大的View，这个过程在onMeasure()方法中进行。


MeasureSpec类
Android系统给我们提供了一个设计小而强的工具类———MeasureSpec类
1、MeasureSpe描述了父View对子View大小的期望。里面包含了测量模式和大小。
2、MeasureSpe类把测量模式和大小组合到一个32位的int型的数值中，其中高2位表示模式，低30位表示大小而在计算中使用位运算的原因是为了提高并优化效率。
3、我们可以通过以下方式从MeasureSpec中提取模式和大小，该方法内部是采用位移计算。

int specMode = MeasureSpec.getMode(measureSpec);
int specSize = MeasureSpec.getSize(measureSpec);
也可以通过MeasureSpec的静态方法把大小和模式合成，该方法内部只是简单的相加。

MeasureSpec.makeMeasureSpec(specSize,specMode);

在对View进行测量时，Android提供了三种测量模式：

1. EXACTLY
即精确值模式，当控件的layout_width属性或layout_height属性指定为具体数值时，例如android:layout_width="100dp"，或者指定为match_parent属性时，系统使用的是EXACTLY 模式。
2. AT_MOST
即最大值模式，当控件的layout_width属性或layout_height属性指定为warp_content时，控件大小一般随着控件的子控件或者内容的变化而变化，此时控件的尺寸只要不超过父控件允许的最大尺寸即可。
3.UNSPECIFIED
这个属性很奇怪，因为它不指定其大小测量的模式，View想多大就多大，通常情况下在绘制自定义View时才会使用。





MotionEvent对象是与用户触摸相关的时间序列，该序列从用户首次触摸屏幕开始，经历手指在屏幕表面的任何移动，直到手指离开屏幕时结束。手指的初次触摸(ACTION_DOWN操作)，滑动(ACTION_MOVE操作)和抬起(ACTION_UP)都会创建MotionEvent对象，每次触摸时候这三个操作是肯定发生的。移动过程中也会产生大量事件，每个事件都会产生对应的MotionEvent对象记录发生的操作，触摸的位置，使用的多大压力，触摸的面积，何时发生，以及最初的ACTION_DOWN何时发生等相关的信息。

动作常量： 
MotionEvent.ACTION_DOWN：当屏幕检测到第一个触点按下之后就会触发到这个事件。 
MotionEvent.ACTION_MOVE：当触点在屏幕上移动时触发，触点在屏幕上停留也是会触发的，主要是由于它的灵敏度很高，而我们的手指又不可能完全静止（即使我们感觉不到移动，但其实我们的手指也在不停地抖动）。 
MotionEvent.ACTION_POINTER_DOWN：当屏幕上已经有触点处于按下的状态的时候，再有新的触点被按下时触发。 
MotionEvent.ACTION_POINTER_UP：当屏幕上有多个点被按住，松开其中一个点时触发（即非最后一个点被放开时）触发。 
MotionEvent.ACTION_UP：当触点松开时被触发。 
MotionEvent.ACTION_OUTSIDE: 表示用户触碰超出了正常的UI边界. 
MotionEvent.ACTION_SCROLL：android3.1引入，非触摸滚动，主要是由鼠标、滚轮、轨迹球触发。 
MotionEvent.ACTION_CANCEL：不是由用户直接触发，由系统在需要的时候触发，例如当父view通过使函数onInterceptTouchEvent()返回true,从子view拿回处理事件的控制权时，就会给子view发一个ACTION_CANCEL事件，子view就再也不会收到后续事件了。
方法： 
getAction()：返回动作类型 
getX()/getY()：获得事件发生时,触摸的中间区域的X/Y坐标，由这两个函数获得的X/Y值是相对坐标，相对于消费这个事件的视图的左上角的坐标。 
getRawX()/getRawY()：由这两个函数获得的X/Y值是绝对坐标，是相对于屏幕的。 
getSize()：指压范围 
getPressure()： 压力值，0-1之间，看情况，很可能始终返回1，具体的级别由驱动和物理硬件决定的 
getEdgeFlags()：当事件类型是ActionDown时可以通过此方法获得边缘标记（EDGE_LEFT,EDGE_TOP,EDGE_RIGHT,EDGE_BOTTOM），但是看设备情况,很可能始终返回0 
getDownTime() ：按下开始时间 
getEventTime() ： 事件结束时间 
getActionMasked()：多点触控获取经过掩码后的动作类型 
getActionIndex()：多点触控获取经过掩码和平移后的索引 
getPointerCount()：获取触控点的数量，比如2则可能是两个手指同时按压屏幕 
getPointerId(nID)：对于每个触控的点的细节，我们可以通过一个循环执行getPointerId方法获取索引 
getX(nID)：获取第nID个触控点的x位置 
getY(nID)：获取第nID个触控点的y位置 
getPressure(nID)：获取第nID个触控点的压力

 关键代码如下：

**MAPVIEW2.java** :  

```java
public boolean onTouch(View v, MotionEvent event) {
int action = event.getActionMasked();
switch (action) {
case MotionEvent.ACTION_DOWN:   //当屏幕检测到第一个触点按下之后就会触发到这个事件
mCanTranslate=true;
getParent().requestDisallowInterceptTouchEvent(true);
mode = MODE_NONE;
mLastSinglePoint.x=event.getX();
mLastSinglePoint.y=event.getY();
break;

case MotionEvent.ACTION_POINTER_DOWN://当屏幕上已经有触点处于按下的状态的时候，再有新的触点被按下时触发
mCanTranslate=false;
mode = MODE_NONE;
oldDistance = getMoveDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
oldAngle = getAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
gestureCenterX = (event.getX(0) + event.getX(1)) * 0.5F;
gestureCenterY = (event.getY(0) + event.getY(1)) * 0.5F;
break;
case MotionEvent.ACTION_MOVE://当触点在屏幕上移动时触发

//判断能否平移操作
if (mCanTranslate) {
mode = MODE_DRAG;
float dx = event.getX() - mLastSinglePoint.x;
float dy = event.getY() - mLastSinglePoint.y;

translationX = dx/20;
translationY = dy/20;
//    invalidate()
//  matrix.postTranslate(translationX, translationY);
// matrix.postTranslate(dx, dy);
//   mLastSinglePoint.x=event.getX();
// mLastSinglePoint.x=event.getY();

invalidate();
break;

}

int pointerCount = event.getPointerCount();
if (pointerCount == 2) {
double newDistance = getMoveDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
newAngle = getAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
float newGestureCenterX = (event.getX(0) + event.getX(1)) * 0.5F;
float newGestureCenterY = (event.getY(0) + event.getY(1)) * 0.5F;

//                    log("newDistance:" + newDistance + ",oldDistance:" + oldDistance);
//                    log("newAngle:" + newAngle + ",oldAngle:" + oldAngle);
if (Math.abs(newAngle - oldAngle) > 10) {
rotateAngle = (float) (newAngle - oldAngle);
if (rotateAngle < 0 || rotateAngle > 270) {
rotateAngle = 2;
} else {
rotateAngle = -2;
}
mode = MODE_ROTATE;
//                        log("-------rotate:" + rotateAngle);
invalidate();
} else if (Math.abs(newDistance - oldDistance) > 70 && oldDistance > 0) {

double delta = newDistance - oldDistance;
if (delta > 0) {
scaleX = 1.03F;
scaleY = 1.03F;
} else {
scaleX = 0.97F;
scaleY = 0.97F;

}
mode = MODE_SCALE;
//                        log("-------scale:" + scaleX);
invalidate();
} else if (getMoveDistance(newGestureCenterX, newGestureCenterY, gestureCenterX, gestureCenterY) > 100) {
mode = MODE_DRAG;
translationX = (newGestureCenterX - gestureCenterX) / 10;
translationY = (newGestureCenterY - gestureCenterY) / 10;
invalidate();
}
}

break;

case MotionEvent.ACTION_POINTER_UP:  //当屏幕上有多个点被按住，松开其中一个点时触发
oldAngle = newAngle;
mode = MODE_NONE;

break;
case MotionEvent.ACTION_UP:  //当触点松开时被触发
mode = MODE_NONE;
oldAngle = 0;
newAngle = 0;
oldDistance = 0;
rotateAngle = 0;
scaleX=1.0F;
scaleY = 1.0F;
translationX = 0;
translationY = 0;
getParent().requestDisallowInterceptTouchEvent(false);
break;
default:
break;
}
return true;
}
```
下面对自定义控件的平移，旋转，放大进行说明：

平移就是当单点时，boolean型变量mCanTranslate为true，当第二个点触发时改为false，保证单点触控平移。
   ```java
    if (mCanTranslate) {
                    mode = MODE_DRAG;
                    float dx = event.getX() - mLastSinglePoint.x;
                    float dy = event.getY() - mLastSinglePoint.y;

                    translationX = dx/20;
                    translationY = dy/20;
                //    invalidate()
                  //  matrix.postTranslate(translationX, translationY);
                   // matrix.postTranslate(dx, dy);
                 //   mLastSinglePoint.x=event.getX();
                   // mLastSinglePoint.x=event.getY();

                    invalidate();
                    break;

                }
 ```
当检测到两个触控点的时候，两个点move距离像素超过70，则根据向量方向进行缩放。
  ```java
if (Math.abs(newDistance - oldDistance) > 70 && oldDistance > 0) {

                            double delta = newDistance - oldDistance;
                            if (delta > 0) {
                                scaleX = 1.03F;
                                scaleY = 1.03F;
                            } else {
                                scaleX = 0.97F;
                                scaleY = 0.97F;

                            }
                            mode = MODE_SCALE;
//                        log("-------scale:" + scaleX);
                            invalidate();
   ```



 角度计算，获取两个触碰点的角度，该角度系为：以第一个触碰点为原点，逆时针方向走,角度变化[0,360)

   ```java
    public double getAngle(float x1,float y1,float x2,float y2){
          float lenX = x2 - x1;
        float lenY = y2 - y1;
        float lenXY = (float) Math.sqrt((double) (lenX * lenX+ lenY * lenY));
        //如果第二个点在第一个点下方，则为正弧度，否则为负弧度
        double radian = Math.acos(lenX / lenXY) * (y2 < y1 ? 1 : -1);
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : tmp + 360;}

    
    ```

**DescripFragment.java** :  
点击事件接口：
//Linearlayout点击事件
```java
        View gowhere = view.findViewById(R.id.go_where);                //指哪到哪
        gowhere.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 具体实现：
                log("----GOWHERE-----");
            }
        });
```

**DescripPresenter.java** : 
接收到map数据后
回调方法onEvent：
```java
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
」
```
将地图在ROS的server端进行预处理，再以底层base64的加密格式发送给Android客户端，在Android端用bitmap刷新显示，实现实时的缩放，平移，拉伸，定位标点。过程中遇到数据量传输较大的问题，由于ROS本身机制不支持图片格式的传输，所以采用底层base64，因此数据量很大难以避免。也可采用差量法，以及运用游程编码带来的启发，进行优化。







 



