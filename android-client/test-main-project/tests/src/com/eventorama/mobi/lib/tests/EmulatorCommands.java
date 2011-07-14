package com.eventorama.mobi.lib.tests;

import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;

/**
 * Flip command copied from com.android.commands.monkey.MonkeyFlipEvent 
 * @author cirrus
 *
 */
public class EmulatorCommands {
	
	private static final String TAG = EmulatorCommands.class.getName();

	 // Raw keyboard flip event data
    // Works on emulator and dream

    private static final byte[] FLIP_0 = {
        0x7f, 0x06,
        0x00, 0x00,
        (byte) 0xe0, 0x39,
        0x01, 0x00,
        0x05, 0x00,
        0x00, 0x00,
        0x01, 0x00,
        0x00, 0x00 };

    private static final byte[] FLIP_1 = {
        (byte) 0x85, 0x06,
        0x00, 0x00,
        (byte) 0x9f, (byte) 0xa5,
        0x0c, 0x00,
        0x05, 0x00,
        0x00, 0x00,
        0x00, 0x00,
        0x00, 0x00 };

    /**
     * 
     * @param mKeyboardOpen false means landscape
     */
    public static void injectFlipEvent(boolean mKeyboardOpen)
    {
    	Log.v(TAG, "sending flip event: "+mKeyboardOpen +" "+new String(mKeyboardOpen ? FLIP_0 : FLIP_1));
    	   // inject flip event
    	FileOutputStream f = null;
        try {
            f = new FileOutputStream("/dev/input/event0");
            f.write(mKeyboardOpen ? FLIP_0 : FLIP_1);
            f.flush();
            f.close();
        } catch (IOException e) {
        	Log.w(TAG, e.getMessage());
        	e.printStackTrace();
        }
    }
}
