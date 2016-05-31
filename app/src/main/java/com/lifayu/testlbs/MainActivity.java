package com.lifayu.testlbs;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();
    private TextView textView;
    private boolean mIsStart;
    private Button button;
    private Switch aSwitch;
    private ToggleButton toggleButton;
    /*
    * bd09ll  表示百度经纬度坐标，
    * gcj02   表示经过国测局加密的坐标，
    * wgs84   表示gps获取的坐标。
    */
    private String CoorType = "wgs84";
    // 中国移动
    private final int OPERATORS_TYPE_MOBILE = 1;
    // 中国联通
    private final int OPERATORS_TYPE_UNICOM = 2;
    // 中国电信
    private final int OPERATORS_TYPE_TELECOMU = 3;
    // 未知运营商
    private final int OPERATORS_TYPE_UNKONW = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        aSwitch = (Switch) findViewById(R.id.switch1);
        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);


        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);

        initLocation();

        //开始定位
        //mLocationClient.start();

    }

    public void getLocation(View v)
    {
        if (!mIsStart)
        {
            mLocationClient.start();
            button.setText("Stop");
            mIsStart = true;
            aSwitch.setText("停止定位");
        }
        else
        {
            mLocationClient.stop();
            mIsStart = false;
            button.setText("Start");
            textView.setText("停止定位！");
            aSwitch.setText("获取位置");
        }
    }







    //BDLocationListener接口有1个方法需要实现：
    //1.接收异步返回的定位结果，参数是BDLocation类型参数。
    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("时间 : " + bdLocation.getTime());
            sb.append("\n错误代码 : " + bdLocation.getLocType());
            sb.append("\n经度 : " + bdLocation.getLongitude());
            sb.append("\n纬度 : " + bdLocation.getLatitude());
            sb.append("\n误差半径 : " + bdLocation.getRadius());

            if(bdLocation.getLocType() == BDLocation.TypeGpsLocation)// GPS定位结果
            {
                // 单位：公里每小时
                sb.append("\n速度 : " + bdLocation.getSpeed());
                sb.append("\n卫星数量 : " + bdLocation.getSatelliteNumber());
                // 单位：米
                sb.append("\n高度 : " + bdLocation.getAltitude());
                // 单位：度
                sb.append("\n方向 : " + bdLocation.getDirection());
                sb.append("\n地址 : " + bdLocation.getAddrStr());
                sb.append("\n描述 : " + "GPS定位成功");
            }
            // 网络定位结果
            else if(bdLocation.getLocType() == BDLocation.TypeNetWorkLocation)
            {
                sb.append("\n地址 : " + bdLocation.getAddrStr());
                // 运营商信息
                switch (bdLocation.getOperators())
                {
                    case OPERATORS_TYPE_MOBILE:
                        sb.append("\n运营商 : " + "中国移动");
                        break;
                    case OPERATORS_TYPE_UNICOM:
                        sb.append("\n运营商 : " + "中国联通");
                        break;
                    case OPERATORS_TYPE_TELECOMU:
                        sb.append("\n运营商 : " + "中国电信");
                        break;
                    case OPERATORS_TYPE_UNKONW:
                        sb.append("\n运营商 : " + "未知");
                        break;
                }
                //sb.append("\noperationers : " + bdLocation.getOperators());
                sb.append("\n描述 : " + "网络定位成功");
            }
            // 离线定位结果
            else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation)
            {
                sb.append("\n描述 : " + "离线定位成功，离线定位结果也是有效的");
            }
            else if (bdLocation.getLocType() == BDLocation.TypeServerError)
            {
                sb.append("\n描述 : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            }
            else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException)
            {
                sb.append("\n描述 : ");
                sb.append("网络不通导致定位失败，请检查网络是否通畅");
            } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException)
            {
                sb.append("\n描述 : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }

            // 位置语义化信息
            sb.append("\n位置描述 : " + bdLocation.getLocationDescribe());
            // POI数据
            List<Poi> list = bdLocation.getPoiList();
            if (list != null)
            {
                sb.append("\nPOI数量 : " + list.size());
                for (Poi poi : list)
                {
                    sb.append("\n周边 : ");
                    sb.append(poi.getName() + " " + poi.getRank());
                    //sb.append(poi.getId() + " " + poi.getName() + " " + poi.getRank());
                }
            }

            Log.i("BaiduLocationApiDem", sb.toString());

            textView.setText(sb.toString());
        }
    }













    //设置定位参数包括：定位模式（高精度定位模式，低功耗定位模式和仅用设备定位模式），
    //返回坐标类型，是否打开GPS，是否返回地址信息、位置语义化信息、POI信息等等。
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系
        option.setCoorType(CoorType);
        //可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        int span = 1000;
        option.setScanSpan(span);
        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);
        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);
        //可选，默认false，设置是否需要位置语义化结果，
        // 可以在BDLocation.getLocationDescribe里得到，
        // 结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);
        //可选，默认true，定位SDK内部是一个SERVICE，
        // 并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(false);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        option.SetIgnoreCacheException(false);
        //可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        option.setEnableSimulateGps(false);


        option.setPriority(LocationClientOption.GpsFirst);
        option.setAddrType("all");




        mLocationClient.setLocOption(option);
    }


    @Override
    public void onDestroy()
    {
        mLocationClient.stop();
        super.onDestroy();
    }



}
