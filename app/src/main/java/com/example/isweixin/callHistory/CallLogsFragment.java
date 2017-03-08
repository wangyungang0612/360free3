package com.example.isweixin.callHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.M360.R;
import com.example.isweixin.callHistory.Utils.CallLogs;

public class CallLogsFragment extends Fragment{
	//ͨ����¼�Ľ���
	private View CallLogView;
	//ͨ����¼���б�
	private ListView m_calllogslist;
	//ͨ����¼�б��������
	public static CallHistoryAdapter m_calllogsadapter;
	//ͨ����¼���ݹ۲���
	private CallHistoryContentObserver CallLogsCO;  
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//����ͨ����¼��ҳ��
		LayoutInflater inflater = getActivity().getLayoutInflater();
		CallLogView = inflater.inflate(R.layout.a, null);
		
		// ���͹㲥 ����ϵ���б����ݷ����仯ʱ,�������б�
        IntentFilter filter = new IntentFilter();
        filter.addAction("huahua.action.UpdataCallLogsView");
        getActivity().registerReceiver(mReceiver , filter); 
        
        CallLogsCO = new CallHistoryContentObserver(new Handler());  
        getActivity().getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI , false, CallLogsCO); 
		
		//ͨ����¼�б�
		m_calllogslist = (ListView)CallLogView.findViewById(R.id.calllogs_list);
		m_calllogsadapter = new CallHistoryAdapter(getActivity(), Utils.calllogs);
		m_calllogslist.setAdapter(m_calllogsadapter);

	}
	
	// ���� ����ϵ���б����ݷ����仯ʱ,�������б� �Ĺ㲥
	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(intent.getAction().equals("huahua.action.UpdataCallLogsView"))
			{
				m_calllogsadapter.updateListView(Utils.calllogs);
			}
		}
    };
	
	public class CallHistoryAdapter extends BaseAdapter{
		private LayoutInflater m_inflater;
		private ArrayList<CallLogs> calllogs;
    	private Context context;
		
        public CallHistoryAdapter(Context context,
        		ArrayList<CallLogs> calllogs) {
    	    this.m_inflater = LayoutInflater.from(context);
    	    this.calllogs = calllogs;
    	    this.context = context;
        }
        
    	//����ϵ���б����ݷ����仯ʱ,�ô˷����������б�
    	public void updateListView(ArrayList<CallLogs> calllogs){
    		this.calllogs = calllogs;
    		notifyDataSetChanged();
    	}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return calllogs.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return calllogs.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
			{
				convertView = m_inflater.inflate(R.layout.calllogs_list_item, null);
			}
			
			if(calllogs.isEmpty())
			{
				return convertView;
			}
			 
			 //ͨ����¼������
			 TextView name = (TextView)convertView.findViewById(R.id.calllog_name);
			 name.setText(calllogs.get(position).Name);
			 
			if(calllogs.get(position).Name == null){
				name.setText("δ֪����");
			}else{
				name.setText(calllogs.get(position).Name);
			}
			 
			 //ͨ����¼�ĵ绰״̬
			 TextView Type = (TextView)convertView.findViewById(R.id.calllog_type);
			 if(calllogs.get(position).Type == CallLog.Calls.INCOMING_TYPE)
			 {
				 Type.setText("�ѽ�����");
				 Type.setTextColor(Color.rgb(0, 0, 255));
			 }
			 else if(calllogs.get(position).Type == CallLog.Calls.OUTGOING_TYPE)
			 {
				 Type.setText("��������");
				 Type.setTextColor(Color.rgb(0, 150, 0));
			 }
			 else if(calllogs.get(position).Type == CallLog.Calls.MISSED_TYPE)
			 {
				 Type.setText("δ������");
				 Type.setTextColor(Color.rgb(255, 0, 0));
			 }
			 
			 //ͨ����¼�ĺ���
			 TextView number = (TextView)convertView.findViewById(R.id.calllog_number);
			 number.setText(calllogs.get(position).Number);
			 
			 //ͨ����¼������
			 TextView data = (TextView)convertView.findViewById(R.id.calllog_data);
			 
			 Date date2 = new Date(Long.parseLong(calllogs.get(position).Data));
			 SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			 String time = sfd.format(date2);
			 data.setText(time);
			 
			 ImageView dialBtn = (ImageView)convertView.findViewById(R.id.calllog_dial);
			dialBtn.setTag(calllogs.get(position).Number);
			dialBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
						Intent  intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel://" + (String)arg0.getTag()));
						startActivity(intent);
				}
			});
			 
			 return convertView;
		}
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		ViewGroup p = (ViewGroup) CallLogView.getParent(); 
        if (p != null) { 
            p.removeAllViewsInLayout(); 
        } 
        
		return CallLogView;
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

}
