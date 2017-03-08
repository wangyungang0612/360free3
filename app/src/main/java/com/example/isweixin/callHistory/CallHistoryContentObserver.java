package com.example.isweixin.callHistory;





import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

public class CallHistoryContentObserver extends ContentObserver{

	public CallHistoryContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void onChange(boolean selfChange) {
		Log.i("huahua", "通话记录数据库发生了变化");
		Utils.getCallLogs();//从util中读取通话记录 
	}

}
