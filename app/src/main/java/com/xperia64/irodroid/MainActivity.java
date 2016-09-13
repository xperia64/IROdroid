package com.xperia64.irodroid;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.lge.hardware.IRBlaster.IRBlaster;
import com.lge.hardware.IRBlaster.IRBlasterCallback;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IRBlasterCallback {

    ConsumerIrManager mIRC;
    IRBlaster mIR;

    int irmode = -1;

    boolean ready = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIRC = (ConsumerIrManager)getSystemService(Context.CONSUMER_IR_SERVICE);
        if(mIRC.hasIrEmitter())
        {
            irmode = 0;
            ready = true;
            return;
        }
        mIR = IRBlaster.getIRBlaster(this, this);

        if (mIR != null) {
            irmode = 1;
            return;
        }
        // TODO: Add HTC thing
        Log.e("IROdroid", "No IR Blaster in this device");
    }

    final int zero = 563;
    final int zero2 = 562; // Averages to 562.5
    final int one = 1687;
    ArrayList<Integer> hex2s(int a)
    {
        ArrayList<Integer> ret = new ArrayList<>();
        for(int mask = 0x80; mask > 0; mask>>=1)
        {
            if((a&mask)!=0) // Bit is 1
            {
                ret.add(zero);
                ret.add(one);
            }else{ // Bit is 0
                ret.add(zero);
                ret.add(zero2);
            }
        }
        return ret;
    }

    // Volume Up = 1 = 0x01
    // Mute = 17 = 0x11
    // Power = 59 = 0x3B
    // Home = 65 = 0x41
    // Down = 75 = 0x4B
    // Up = 83 = 0x53
    // Back = 89 = 0x59
    // OK = 115 = 0x73
    // Volume Down = 129 = 0x81
    // Right = 131 = 0x83
    // Left = 153 = 0x99
    // Menu = 163 = 0xA3

    final int BTN_VUP = 0x1;
    final int BTN_MUTE = 0x11;
    final int BTN_PWR = 0x3B;
    final int BTN_HOME = 0x41;
    final int BTN_DOWN = 0x4B;
    final int BTN_UP = 0x53;
    final int BTN_BACK = 0x59;
    final int BTN_OK = 0x73;
    final int BTN_VDWN = 0x81;
    final int BTN_RGHT = 0x83;
    final int BTN_LEFT = 0x99;
    final int BTN_MENU = 0xA3;

    int command = -1;
    public void onButt(View v)
    {
        switch(v.getId())
        {
            case R.id.backBtn:
                command = BTN_BACK;
                break;
            case R.id.volP:
                command = BTN_VUP;
                break;
            case R.id.muteBtn:
                command = BTN_MUTE;
                break;
            case R.id.homeBtn:
                command = BTN_HOME;
                break;
            case R.id.downBtn:
                command = BTN_DOWN;
                break;
            case R.id.upBtn:
                command = BTN_UP;
                break;
            case R.id.okBtn:
                command = BTN_OK;
                break;
            case R.id.volM:
                command = BTN_VDWN;
                break;
            case R.id.rightBtn:
                command = BTN_RGHT;
                break;
            case R.id.leftBtn:
                command = BTN_LEFT;
                break;
            case R.id.menuBtn:
                command = BTN_MENU;
                break;
            case R.id.pwrBtn:
                command = BTN_PWR;
                break;
        }
        if(command < 0)
        {
            return;
        }
        int device1 = 0x4D;
        int device2 = (~device1)&0xFF;

        int command1 = command;
        int command2 = (~command1)&0xFF;
        if(ready)
        {
            ArrayList<Integer> send = new ArrayList<>();
            send.add(9000);
            send.add(4500);
            send.addAll(hex2s(device1));
            send.addAll(hex2s(device2));
            send.addAll(hex2s(command1));
            send.addAll(hex2s(command2));
            send.add(zero);
            send.add(zero2);
            int[] transmit = new int[send.size()];
            for(int i = 0; i<transmit.length; i++)
            {
                transmit[i] = send.get(i);
            }
            switch(irmode)
            {
                case 0:
                    mIRC.transmit(37900, transmit);
                    break;
                case 1:
                    mIR.sendIRPattern(37900, transmit);
                    break;
                default:
                    Toast.makeText(this, "Error: No IR blaster!", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
        command = -1;
    }

    @Override
    public void IRBlasterReady() {
        ready = true;
    }

    @Override
    public void learnIRCompleted(int i) {

    }

    @Override
    public void newDeviceId(int i) {

    }
}
