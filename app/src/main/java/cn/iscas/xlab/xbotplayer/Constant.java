package cn.iscas.xlab.xbotplayer;

/**
 * Created by lisongting on 2017/10/9.
 */

public class Constant {

    public static final String KEY_BROADCAST_ROS_CONN = "ros_conn_status";     // //广播Intent中用于存放ros连接状态的key

    public static final String SUBSCRIBE_TOPIC_MAP = "/base64_img/map_img";     //订阅: base64地图数据的topic

    public static final String PUBLISH_TOPIC_CMD_MOVE = "/cmd_vel_mux/input/teleop";  //发布：用于控制Xbot移动的topic

    public static final String PUBLISH_TOPIC_CMD_LIFT = "/mobile_base/commands/lift";

    public static final String PUBLISH_TOPIC_CMD_CLOUD_CAMERA = "/mobile_base/commands/cloud_camera"; //发布：用于控制摄像头角度和云台角度
                                                                                                     // 的topic

    public static final String PUBLISH_TOPIC_CMD_MACHINERY_POWER = "/mobile_base/commands/power";  //发布：用于控制电机电源的topic

    public static final String SUBSCRIBE_TOPIC_RGB_IMAGE = "/base64_img/camera_rgb";

    public static final String SUBSCRIBE_TOPIC_DEPTH_IMAGE = "/base64_img/camera_depth";

    public static final String SUBSCRIBE_TOPIC_ROBOT_STATE = "/mobile_base/xbot/state"; //订阅:Xbot状态的topic



    //用来表示Ros服务器的连接状态
    public static final int CONN_ROS_SERVER_SUCCESS = 0x11; //success

    public static final int CONN_ROS_SERVER_ERROR = 0x12;  //failed

    //广播的Intentfilter
    public static final String ROS_RECEIVER_INTENTFILTER = "xbotplayer.rosconnection.receiver";

    //rgb图像和深度图像的URL后缀
    public static final String CAMERA_RGB_RTMP_SUFFIX = "/rgb";
    public static final String CAMERA_DEPTH_RTMP_SUFFIX = "/depth";

    public static final int VIDEO_TYPE_RGB = 0;
    public static final int VIDEO_TYPE_DEPTH = 1;


}
