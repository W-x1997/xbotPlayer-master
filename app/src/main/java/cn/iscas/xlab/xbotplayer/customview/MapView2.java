package cn.iscas.xlab.xbotplayer.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

/**
 * Created by wx on 2018/7/31.
 */

public class MapView2 extends View implements View.OnTouchListener{

    private static final String TAG = "MapView";
    int width,height;
    Matrix matrix;
    private float scaleX = 1.0F;
    private float scaleY = 1.0F;
    private Bitmap bitmap;
    private float gestureCenterX = 0;
    private float gestureCenterY = 0;
    private double oldDistance;
    private double oldAngle =0;
    private double newAngle =0;
    private float rotateAngle=0;
    private float translationX = 0;
    private float translationY = 0;
    private final int MODE_NONE = 0;
    private final int MODE_SCALE = 1;
    private final int MODE_ROTATE = 2;
    private final int MODE_DRAG = 3;
    private int mode = MODE_NONE;

    public Point mLastSinglePoint=new Point();
    boolean mCanTranslate=false; // 单点触控平移，在ACTION_DOWN的时候置位true，ACTION_POINTER_DOWN置为false

    public MapView2(Context context) {
        this(context,null);
    }

    public MapView2(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        matrix = new Matrix();
        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);


        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);


            int screenWidth = metrics.widthPixels;
            int screenHeight = metrics.heightPixels;
            log("ScreenSize:" + screenWidth + "X" + screenHeight+",DPI:"+metrics.densityDpi);

            if (widthSize < screenWidth && heightSize < screenWidth) {

                widthSize = widthSize > heightSize ? widthSize : heightSize;
                heightSize = widthSize;
            }else{
                widthSize = screenWidth;
                heightSize = screenWidth;
            }
            width = widthSize;
            height = heightSize;
//            log("width:"+width);
//            log("height:"+height);
        }
        setMeasuredDimension(widthSize, heightSize);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (bitmap == null) {
           // canvas.drawColor(Color.DKGRAY);  //646161
            canvas.drawRGB(100,100,100);
        } else {
            if (mode == MODE_ROTATE) {
                matrix.postRotate(rotateAngle, gestureCenterX, gestureCenterY);
            } else if (mode == MODE_SCALE) {
                matrix.postScale(scaleX,scaleY,gestureCenterX,gestureCenterY);
            } else if (mode == MODE_DRAG) {
                matrix.postTranslate(translationX, translationY);
            }
            canvas.drawBitmap(bitmap, matrix, null);
            bitmap.recycle();
        }

    }

/**
    protected void onDraw2(Canvas canvas){
        super.onDraw(canvas);
        if(bitmap==null){
            canvas.drawColor(Color.DKGRAY);
        }else{
            if(mode==MODE_ROTATE){
                matrix.postRotate(rotateAngle,gestureCenterX,gestureCenterY);
            }else if(mode==MODE_SCALE){
                matrix.postScale(scaleX,scaleY);
            }else if(mode==MODE_ROTATE){
                matrix.postScale(scaleX,scaleY,gestureCenterX,gestureCenterY);

            }
            canvas.drawBitmap(bitmap,matrix,null);
            bitmap.recycle();

        }
    }
 **/

    public void log(String s) {
        Log.i(TAG, TAG + " -- "+s);
    }

    public void updateMap(Bitmap bitmap) {
        this.bitmap = bitmap;
        postInvalidate();
    }

    @Override
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
                    float dx = event.getX() - mLastSinglePoint.x;
                    float dy = event.getY() - mLastSinglePoint.y;

                    translationX = dx/15;
                    translationY = dy/15;
                    invalidate();
                    matrix.postTranslate(translationX, translationY);

                }

                int pointerCount = event.getPointerCount();
                if (pointerCount == 2) {
                    double newDistance = getMoveDistance(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    newAngle = getAngle(event.getX(0), event.getY(0), event.getX(1), event.getY(1));
                    float newGestureCenterX = (event.getX(0) + event.getX(1)) * 0.5F;
                    float newGestureCenterY = (event.getY(0) + event.getY(1)) * 0.5F;

//                    log("newDistance:" + newDistance + ",oldDistance:" + oldDistance);
//                    log("newAngle:" + newAngle + ",oldAngle:" + oldAngle);
                    if (Math.abs(newAngle - oldAngle) > 10 ) {
                        rotateAngle = (float) (newAngle - oldAngle);
                        if (rotateAngle < 0||rotateAngle>270) {
                            rotateAngle = 2;
                        } else {
                            rotateAngle = -2;
                        }
                        mode = MODE_ROTATE;
//                        log("-------rotate:" + rotateAngle);
                        invalidate();
                    } else if (Math.abs(newDistance - oldDistance) > 200 && oldDistance > 0) {

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
                    } else if(getMoveDistance(newGestureCenterX, newGestureCenterY,gestureCenterX,gestureCenterY)>100){
                        mode = MODE_DRAG;
                        translationX = (newGestureCenterX - gestureCenterX)/10;
                        translationY = (newGestureCenterY - gestureCenterY)/10;
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

    //计算两个手指的移动距离
    public double getMoveDistance(float x1,float y1,float x2,float y2) {
        float x = x1 - x2;
        float y = y1 - y2;
        return Math.sqrt(x * x + y * y);

    }


    //获取两个触碰点的角度，该角度系为：以第一个触碰点为原点，逆时针方向走,角度变化[0,360)
    public double getAngle(float x1,float y1,float x2,float y2) {

        float lenX = x2 - x1;
        float lenY = y2 - y1;
        float lenXY = (float) Math.sqrt((double) (lenX * lenX+ lenY * lenY));
        //如果第二个点在第一个点下方，则为正弧度，否则为负弧度
        double radian = Math.acos(lenX / lenXY) * (y2 < y1 ? 1 : -1);
        double tmp = Math.round(radian / Math.PI * 180);
        return tmp >= 0 ? tmp : tmp + 360;

    }

    public void reset(){
        matrix.reset();
        mode = MODE_NONE;
        postInvalidate();
    }


    class Point{
        public float x;
        public float y;
        Point(){
            this.x=0;
            this.y=0;
        }
    }

}

