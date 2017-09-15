package com.example.user.pay_it;

import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.misfit.misfitlinksdk.publish.MFLCommand;
import com.misfit.misfitlinksdk.publish.MFLGestureCommandDelegate;

/**
 * Created by User on 4/16/2016.
 */
public class DeviceMonitorDelegate implements MFLGestureCommandDelegate {
    @Override
    public void performActionByCommand(MFLCommand command, String serialNumber) {
        Looper.prepare();
        Log.i("It worked ", "performActionByCommand " + " " + command.getName() + " " + serialNumber);
        // add your code here
        switch(command.getName()){
            case "pay": new MainActivity().presentPaypal();
        }
    }
}