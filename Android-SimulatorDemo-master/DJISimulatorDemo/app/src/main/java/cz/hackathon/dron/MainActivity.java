package cz.hackathon.dron;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import dji.common.error.DJIError;
import dji.common.flightcontroller.DJIFlightControllerControlMode;
import dji.common.flightcontroller.DJIFlightControllerDataType;
import dji.common.flightcontroller.DJIFlightOrientationMode;
import dji.common.flightcontroller.DJISimulatorInitializationData;
import dji.common.flightcontroller.DJISimulatorStateData;
import dji.common.flightcontroller.DJIVirtualStickFlightControlData;
import dji.common.flightcontroller.DJIVirtualStickRollPitchControlMode;
import dji.common.flightcontroller.DJIVirtualStickVerticalControlMode;
import dji.common.flightcontroller.DJIVirtualStickYawControlMode;
import dji.common.remotecontroller.DJIRCControlMode;
import dji.common.remotecontroller.DJIRCControlStyle;
import dji.common.util.DJICommonCallbacks;
import dji.sdk.base.DJIBaseProduct;
import dji.sdk.flightcontroller.DJIFlightController;
import dji.sdk.flightcontroller.DJISimulator;
import dji.sdk.products.DJIAircraft;

public class MainActivity extends Activity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getName();

    private DJIFlightController mFlightController;

    protected TextView mConnectStatusTextView;

    private Button mBtnEnableVirtualStick;
    private Button mBtnDisableVirtualStick;
    private ToggleButton mBtnSimulator;
    private Button mBtnTakeOff;
    private Button mBtnLand;
    private Button mBtnLeft;
    private Button mBtnRight;
    private Button mBtnLeftYaw;
    private Button mBtnRightYaw;
    private Button mBtnUp;
    private Button mBtnDown;
    private Button mBtnFront;
    private Button mBtnBack;

    private TextView mTextView;

    private OnScreenJoystick mScreenJoystickRight;
    private OnScreenJoystick mScreenJoystickLeft;

    private Timer mSendVirtualStickDataTimer;
    private SendVirtualStickDataTask mSendVirtualStickDataTask;
    private StopMoveTask mStopMoveTask;
    private Timer mStopMoveTimer;

    private float mPitch;
    private float mRoll;
    private float mYaw;
    private float mThrottle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // When the compile and target version is higher than 22, please request the
        // following permissions at runtime to ensure the
        // SDK work well.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.VIBRATE,
                            Manifest.permission.INTERNET, Manifest.permission.ACCESS_WIFI_STATE,
                            Manifest.permission.WAKE_LOCK, Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
                            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.SYSTEM_ALERT_WINDOW,
                            Manifest.permission.READ_PHONE_STATE,
                    }
                    , 1);
        }

        setContentView(R.layout.activity_main);

        initUI();

        // Register the broadcast receiver for receiving the device connection's changes.
        IntentFilter filter = new IntentFilter();
        filter.addAction(DJISimulatorApplication.FLAG_CONNECTION_CHANGE);
        registerReceiver(mReceiver, filter);
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateTitleBar();
        }
    };

    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTitleBar() {
        if(mConnectStatusTextView == null) return;
        boolean ret = false;
        DJIBaseProduct product = DJISimulatorApplication.getProductInstance();
        if (product != null) {
            if(product.isConnected()) {
                //The product is connected
                mConnectStatusTextView.setText(DJISimulatorApplication.getProductInstance().getModel() + " Connected");
                ret = true;
            } else {
                if(product instanceof DJIAircraft) {
                    DJIAircraft aircraft = (DJIAircraft)product;
                    if(aircraft.getRemoteController() != null && aircraft.getRemoteController().isConnected()) {
                        // The product is not connected, but the remote controller is connected
                        mConnectStatusTextView.setText("only RC Connected");
                        ret = true;
                    }
                }
            }
        }

        if(!ret) {
            // The product or the remote controller are not connected.
            mConnectStatusTextView.setText("Disconnected");
        }
    }

    @Override
    public void onResume() {
        Log.e(TAG, "onResume");
        super.onResume();
        updateTitleBar();
        initFlightController();
    }

    @Override
    public void onPause() {
        Log.e(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.e(TAG, "onStop");
        super.onStop();
    }

    public void onReturn(View view){
        Log.e(TAG, "onReturn");
        this.finish();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        unregisterReceiver(mReceiver);
        if (null != mSendVirtualStickDataTimer) {
            mSendVirtualStickDataTask.cancel();
            mSendVirtualStickDataTask = null;
            mSendVirtualStickDataTimer.cancel();
            mSendVirtualStickDataTimer.purge();
            mSendVirtualStickDataTimer = null;
        }
        super.onDestroy();
    }

    private void sendCommand(float pPitch, float pRoll, float pYaw, float pThrottle, int seconds) {
        mPitch = pPitch;
        mRoll = pRoll;
        mYaw = pYaw;
        mThrottle = pThrottle;

        if (null == mSendVirtualStickDataTimer) {
            mSendVirtualStickDataTask = new SendVirtualStickDataTask();
            mSendVirtualStickDataTimer = new Timer();
            mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
        }

        if (null != mStopMoveTimer) {
            mStopMoveTask.cancel();
            mStopMoveTask = null;
            mStopMoveTimer.cancel();
            mStopMoveTimer.purge();
            mStopMoveTimer = null;
        }

        mStopMoveTask = new StopMoveTask();
        mStopMoveTimer = new Timer();
        mStopMoveTimer.schedule(mStopMoveTask, seconds * 1000);
    }

    private void initFlightController() {

        DJIAircraft aircraft = DJISimulatorApplication.getAircraftInstance();
        if (aircraft == null || !aircraft.isConnected()) {
            showToast("Disconnected");
            mFlightController = null;
            return;
        } else {
            mFlightController = aircraft.getFlightController();

            aircraft.getRemoteController().getRCControlMode(new DJICommonCallbacks.DJICompletionCallbackWith<DJIRCControlMode>() {
                @Override
                public void onSuccess(DJIRCControlMode djircControlMode) {
                    djircControlMode.controlStyle = DJIRCControlStyle.Custom;
                    showToast("Control mode:" + djircControlMode.controlStyle);
                }
                @Override
                public void onFailure(DJIError djiError) {
                    showToast("Control mode error:" + djiError.getDescription());

                }
            });

            enableVirtualStick();
            mFlightController.getSimulator().setUpdatedSimulatorStateDataCallback(new DJISimulator.UpdatedSimulatorStateDataCallback() {
                @Override
                public void onSimulatorDataUpdated(final DJISimulatorStateData djiSimulatorStateData) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {

                            String yaw = String.format("%.2f", djiSimulatorStateData.getYaw());
                            String pitch = String.format("%.2f", djiSimulatorStateData.getPitch());
                            String roll = String.format("%.2f", djiSimulatorStateData.getRoll());
                            String positionX = String.format("%.2f", djiSimulatorStateData.getPositionX());
                            String positionY = String.format("%.2f", djiSimulatorStateData.getPositionY());
                            String positionZ = String.format("%.2f", djiSimulatorStateData.getPositionZ());

                            mTextView.setText("Yaw : " + yaw + ", Pitch : " + pitch + ", Roll : " + roll + "\n" + ", PosX : " + positionX +
                                    ", PosY : " + positionY +
                                    ", PosZ : " + positionZ);
                        }
                    });
                }
            });
        }
    }

    private void initUI() {

        mBtnEnableVirtualStick = (Button) findViewById(R.id.btn_enable_virtual_stick);
        mBtnDisableVirtualStick = (Button) findViewById(R.id.btn_disable_virtual_stick);
        mBtnTakeOff = (Button) findViewById(R.id.btn_take_off);
        mBtnLand = (Button) findViewById(R.id.btn_land);
        mBtnLeft = (Button) findViewById(R.id.btn_up);
        mBtnRight = (Button) findViewById(R.id.btn_right);
        mBtnLeftYaw = (Button) findViewById(R.id.btn_left_yaw);
        mBtnRightYaw = (Button) findViewById(R.id.btn_right_yaw);
        mBtnUp = (Button) findViewById(R.id.btn_up);
        mBtnDown= (Button) findViewById(R.id.btn_down);
        mBtnFront = (Button) findViewById(R.id.btn_front);
        mBtnBack= (Button) findViewById(R.id.btn_back);
        mBtnSimulator = (ToggleButton) findViewById(R.id.btn_start_simulator);
        mTextView = (TextView) findViewById(R.id.textview_simulator);
        mConnectStatusTextView = (TextView) findViewById(R.id.ConnectStatusTextView);
        mScreenJoystickRight = (OnScreenJoystick)findViewById(R.id.directionJoystickRight);
        mScreenJoystickLeft = (OnScreenJoystick)findViewById(R.id.directionJoystickLeft);

        mBtnEnableVirtualStick.setOnClickListener(this);
        mBtnDisableVirtualStick.setOnClickListener(this);
        mBtnTakeOff.setOnClickListener(this);
        mBtnLand.setOnClickListener(this);
        mBtnLeft.setOnClickListener(this);
        mBtnRight.setOnClickListener(this);
        mBtnLeftYaw.setOnClickListener(this);
        mBtnRightYaw.setOnClickListener(this);
        mBtnUp.setOnClickListener(this);
        mBtnDown.setOnClickListener(this);
        mBtnFront.setOnClickListener(this);
        mBtnBack.setOnClickListener(this);

        mBtnSimulator.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    mTextView.setVisibility(View.VISIBLE);

                    if (mFlightController != null) {
                        mFlightController.getSimulator()
                                .startSimulator(new DJISimulatorInitializationData(
                                        23, 113, 10, 10
                                )
                                        , new DJICommonCallbacks.DJICompletionCallback() {
                                    @Override
                                    public void onResult(DJIError djiError) {
                                        if (djiError != null) {
                                            showToast(djiError.getDescription());
                                        }else
                                        {
                                            showToast("Start Simulator Success");
                                        }
                                    }
                                });
                    }

                } else {

                    mTextView.setVisibility(View.INVISIBLE);

                    if (mFlightController != null) {
                        mFlightController.getSimulator()
                                .stopSimulator(
                                        new DJICommonCallbacks.DJICompletionCallback() {
                                            @Override
                                            public void onResult(DJIError djiError) {
                                                if (djiError != null) {
                                                    showToast(djiError.getDescription());
                                                }else
                                                {
                                                    showToast("Stop Simulator Success");
                                                }
                                            }
                                        }
                                );
                    }
                }
            }
        });

        mScreenJoystickLeft.setJoystickListener(new OnScreenJoystickListener(){

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if(Math.abs(pX) < 0.02 ){
                    pX = 0;
                }

                if(Math.abs(pY) < 0.02 ){
                    pY = 0;
                }
                float pitchJoyControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMaxVelocity;
                float rollJoyControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickRollPitchControlMaxVelocity;

                mPitch = (float)(pitchJoyControlMaxSpeed * pY / 4);

                mRoll = (float)(rollJoyControlMaxSpeed * pX / 4);

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
                }

            }

        });

        mScreenJoystickRight.setJoystickListener(new OnScreenJoystickListener() {

            @Override
            public void onTouch(OnScreenJoystick joystick, float pX, float pY) {
                if(Math.abs(pX) < 0.02 ){
                    pX = 0;
                }

                if(Math.abs(pY) < 0.02 ){
                    pY = 0;
                }
                float verticalJoyStickControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickVerticalControlMaxVelocity;
                float yawJoyStickControlMaxSpeed = DJIFlightControllerDataType.DJIVirtualStickYawControlMaxAngularVelocity;

                mYaw = (float)(yawJoyStickControlMaxSpeed * pX);
                mThrottle = (float)(yawJoyStickControlMaxSpeed * pY);

                if (null == mSendVirtualStickDataTimer) {
                    mSendVirtualStickDataTask = new SendVirtualStickDataTask();
                    mSendVirtualStickDataTimer = new Timer();
                    mSendVirtualStickDataTimer.schedule(mSendVirtualStickDataTask, 0, 200);
                }

            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_enable_virtual_stick:
                enableVirtualStick();
                break;

            case R.id.btn_disable_virtual_stick:
                if (mFlightController != null){
                    mFlightController.disableVirtualStickControlMode(
                            new DJICommonCallbacks.DJICompletionCallback() {
                                @Override
                                public void onResult(DJIError djiError) {
                                    if (djiError != null) {
                                        showToast(djiError.getDescription());
                                    } else {
                                        showToast("Disable Virtual Stick Success");
                                    }
                                }
                            }
                    );
                }
                break;
            case R.id.btn_take_off:
                takeOff();
                break;
            case R.id.btn_land:
                autoLanding();
                break;
            case R.id.btn_left_yaw:
                sendCommandLeftYaw(3);
                break;
            case R.id.btn_right_yaw:
                sendCommandRightYaw(3);
                break;
            case R.id.btn_left:
                sendCommandLeft(3);
                break;
            case R.id.btn_right:
                sendCommandRight(3);
                break;
            case R.id.btn_up:
                sendCommandUp(3, 0.5f);
                break;
            case R.id.btn_down:
                sendCommandDown(3, 0.5f);
                break;
            case R.id.btn_front:
                sendCommandFront(3);
                break;
            case R.id.btn_back:
                sendCommandBack(3);
                break;

            default:
                break;
        }
    }

    private void autoLanding() {
        if (mFlightController != null){

            mFlightController.autoLanding(
                    new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast(djiError.getDescription());
                            } else {
                                showToast("AutoLand Started");
                            }
                        }
                    }
            );

        }
    }

    private void takeOff() {
        if (mFlightController != null){
            mFlightController.takeOff(
                    new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null) {
                                showToast(djiError.getDescription());
                            } else {
                                showToast("Take off Success");
                            }
                        }
                    }
            );
        }
    }

    private void sendCommandBack(int seconds) {
        sendCommand(0, -0.5f, 0, 0, seconds);
    }

    private void sendCommandFront(int seconds) {
        sendCommand(0, 0.5f, 0, 0, seconds);
    }

    private void sendCommandDown(int seconds, float metersPerSecond) {
        sendCommand(0, 0, 0, -metersPerSecond, seconds);
    }

    private void sendCommandUp(int seconds, float metersPerSecond) {
        sendCommand(0, 0, 0, metersPerSecond, seconds);
    }

    private void sendCommandRight(int seconds) {
        sendCommand(0.5f, 0, 0, 0.1f, seconds);
    }

    private void sendCommandLeft(int seconds) {
        sendCommand(-0.5f, 0, 0, 0.1f, seconds);
    }

    private void sendCommandRightYaw(int seconds) {
        sendCommand(0, 0, mYaw+5, 0, seconds);
    }

    private void sendCommandLeftYaw(int seconds) {
        sendCommand(0, 0, mYaw-5, 0, seconds);
    }

    private void enableVirtualStick() {
        if (mFlightController != null){
            mFlightController.enableVirtualStickControlMode(
                    new DJICommonCallbacks.DJICompletionCallback() {
                        @Override
                        public void onResult(DJIError djiError) {
                            if (djiError != null){
                                showToast(djiError.getDescription());
                            }else
                            {
                                showToast("Enable Virtual Stick Success");
                            }
                        }
                    }
            );
        }
    }

    class SendVirtualStickDataTask extends TimerTask {

        @Override
        public void run() {

            if (mFlightController != null) {
                mFlightController.setFlightOrientationMode(DJIFlightOrientationMode.DefaultAircraftHeading, new DJICommonCallbacks.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {
                        showToast("Can not set AircraftHeading mode: " + djiError.getDescription());
                    }
                });
                mFlightController.setRollPitchControlMode(DJIVirtualStickRollPitchControlMode.Velocity);
                mFlightController.setYawControlMode(DJIVirtualStickYawControlMode.Angle);
                mFlightController.setVerticalControlMode(DJIVirtualStickVerticalControlMode.Velocity);
                mFlightController.setVirtualStickAdvancedModeEnabled(true);
                mFlightController.setControlMode(DJIFlightControllerControlMode.Manual, new DJICommonCallbacks.DJICompletionCallback() {
                    @Override
                    public void onResult(DJIError djiError) {

                    }
                });

                mFlightController.sendVirtualStickFlightControlData(
                        new DJIVirtualStickFlightControlData(
                                mPitch, mRoll, mYaw, mThrottle
                        ), new DJICommonCallbacks.DJICompletionCallback() {
                            @Override
                            public void onResult(DJIError djiError) {

                            }
                        }
                );
            }
        }
    }

    private class StopMoveTask extends TimerTask {

        @Override
        public void run() {
            mPitch = 0;
            mRoll = 0;
            mYaw = 0;
            mThrottle = 0;
            if (null != mSendVirtualStickDataTimer) {
                mSendVirtualStickDataTask.cancel();
                mSendVirtualStickDataTask = null;
                mSendVirtualStickDataTimer.cancel();
                mSendVirtualStickDataTimer.purge();
                mSendVirtualStickDataTimer = null;
            }
        }
    }
}
