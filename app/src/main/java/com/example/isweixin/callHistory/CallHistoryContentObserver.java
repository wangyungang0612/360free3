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
		Log.i("huahua", "ͨ����¼���ݿⷢ���˱仯");
		Utils.getCallLogs();//��util�ж�ȡͨ����¼ 
	}

}
