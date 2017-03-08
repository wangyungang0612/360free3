package com.example.M360;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.isweixin.ContactPerson.ABCDEFG;
import com.example.isweixin.ContactPerson.ContactsContentObserver;
import com.example.isweixin.ContactPerson.ABCDEFG.OnTouchBarListener;


import com.example.isweixin.callHistory.CallHistoryContentObserver;
import com.example.isweixin.callHistory.Utils;
import com.example.isweixin.callHistory.Utils.CallLogs;
import com.example.isweixin.callHistory.Utils.Person_Sms;
import com.example.isweixin.callHistory.Utils.Persons;
import com.example.isweixin.callHistory.Utils.Persons2;
import com.example.isweixin.information.SmssContentObserver;

import android.media.AudioManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MainActivity extends Activity implements OnViewChangeListener,
		OnClickListener {
	private MyScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;

	private TextView dialing;// 拨号
	private TextView contactPerson;// 联系人
	private TextView information;// 短信
	private TextView usecenter;// 用户中心

	private EditText EditNum; // 号码编辑框
	private LinearLayout keybordLinearLayout;// 键盘布局
	private ImageView backImageView; // 回删
	private ImageView keybordImageView;// 展开键盘
	private boolean isOpen = false;
	private Button backButton;// 清除数字

	private ListView callHistoryListView;// 通话记录
	public static CallHistoryAdapter calllogsadapter;// 通话记录适配器
	private CallHistoryContentObserver CallLogsCO;// 通话记录内容观察者

	private LinearLayout more1LinearLayout;// 第一个更多布局
	private ImageView more1ImageView;// 第一个更多图片

	private LinearLayout more2LinearLayout;// 第二个更多布局
	private ImageView more2ImageView;// 第二个更多图片

	private LinearLayout more3LinearLayout;// 第二个更多布局
	private ImageView more3ImageView;// 第二个更多图片

	private ListView m_contactslist;// 联系人的列表
	public static ContactsAdapter m_contactsadapter;// 联系人列表的适配器
	private ContactsContentObserver ContactsCO;// 联系人内容观察者
	private ImageView m_AddContactBtn;// 新增联系人
	private Button m_RemoveSameContactBtn;// 去重联系人按钮

	private EditText m_FilterEditText;// 搜索过滤联系人EditText
	private TextView m_listEmptyText;// 没有匹配联系人时显示的TextView
	private FrameLayout m_topcontactslayout;// 最上面的layout
	private String ChooseContactName;// 选中的联系人名字
	private String ChooseContactNumber;// 选中的联系人号码
	private String ChooseContactID;// 选中的联系人ID
	ProgressDialog m_dialogLoading;// 加载对话框
	private ABCDEFG m_asb;// 字母列视图View
	private TextView m_letterNotice;// 显示选中的字母
	ArrayList<Persons2> blockList = new ArrayList<Persons2>();// 黑名单
	int i = 0;// 电话模式

	// 信息的列表
	private ListView m_smsslist;
	// 信息列表的适配器
	public static SmsAdapter m_smsadapter;
	// 短信息内容观察者
	private SmssContentObserver SmssCO;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		Utils.init(this);
		Utils.getContacts();
		Utils.getCallLogs();
		Utils.getSmss();

		TelephonyManager tm = (TelephonyManager) MainActivity.this
				.getSystemService(Context.TELEPHONY_SERVICE);
		MyPhoneCallListener listener = new MyPhoneCallListener();
		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

		// 展开第一个更多
		more1LinearLayout = (LinearLayout) findViewById(R.id.more1);
		more1ImageView = (ImageView) findViewById(R.id.gengduo1);
		more1LinearLayout.setVisibility(View.INVISIBLE);// 占用布局但是不可见
		more1ImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				keybordLinearLayout.setVisibility(View.INVISIBLE);// 键盘隐藏
				if (more1LinearLayout.getVisibility() != 0) {
					more1LinearLayout.setVisibility(View.VISIBLE);// 更多可见
				} else {
					more1LinearLayout.setVisibility(View.GONE);// 更多不可见
				}
			}
		});

		// 展开第二个更多

		more2LinearLayout = (LinearLayout) findViewById(R.id.more2);
		more2ImageView = (ImageView) findViewById(R.id.gengduo2);
		more2LinearLayout.setVisibility(View.INVISIBLE);// 占用布局但是不可见
		more2ImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				if (more2LinearLayout.getVisibility() != 0) {
					more2LinearLayout.setVisibility(View.VISIBLE);// 更多可见
				} else {
					more2LinearLayout.setVisibility(View.GONE);// 更多不可见
				}
			}
		});

		// 展开第三个更多
		more3LinearLayout = (LinearLayout) findViewById(R.id.more3);
		more3ImageView = (ImageView) findViewById(R.id.gengduo3);
		more3LinearLayout.setVisibility(View.INVISIBLE);// 占用布局但是不可见
		more3ImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (more3LinearLayout.getVisibility() != 0) {
					more3LinearLayout.setVisibility(View.VISIBLE);// 更多可见
				} else {
					more3LinearLayout.setVisibility(View.GONE);// 更多不可见
				}
			}
		});

		// 展开键盘
		keybordLinearLayout = (LinearLayout) findViewById(R.id.keyboardLinearLayout);
		keybordImageView = (ImageView) findViewById(R.id.keyboardButton);
		keybordImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				more1LinearLayout.setVisibility(View.INVISIBLE);// 第一个更多
																// 占用布局但是不可见
				if (keybordLinearLayout.getVisibility() != 0) {
					keybordLinearLayout.setVisibility(View.VISIBLE);// 键盘可见
				} else {

					keybordLinearLayout.setVisibility(View.GONE);// 键盘隐藏
				}
			}
		});

		// 数字输入得到
		EditNum = (EditText) findViewById(R.id.edit_num);
		// edittext不显示软键盘,要显示光标
		if (android.os.Build.VERSION.SDK_INT <= 10) {
			EditNum.setInputType(InputType.TYPE_NULL);
		} else {
			MainActivity.this.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			try {
				Class<EditText> cls = EditText.class;
				Method setSoftInputShownOnFocus;
				setSoftInputShownOnFocus = cls.getMethod(
						"setShowSoftInputOnFocus", boolean.class);
				setSoftInputShownOnFocus.setAccessible(true);
				setSoftInputShownOnFocus.invoke(EditNum, false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 数字ID
		findViewById(R.id.number1).setOnClickListener(new BtnClick2());
		findViewById(R.id.number2).setOnClickListener(new BtnClick2());
		findViewById(R.id.number3).setOnClickListener(new BtnClick2());
		findViewById(R.id.number4).setOnClickListener(new BtnClick2());
		findViewById(R.id.number5).setOnClickListener(new BtnClick2());
		findViewById(R.id.number6).setOnClickListener(new BtnClick2());
		findViewById(R.id.number7).setOnClickListener(new BtnClick2());
		findViewById(R.id.number8).setOnClickListener(new BtnClick2());
		findViewById(R.id.number9).setOnClickListener(new BtnClick2());
		findViewById(R.id.number0).setOnClickListener(new BtnClick2());
		findViewById(R.id.number_xing).setOnClickListener(new BtnClick2());
		findViewById(R.id.number_jing).setOnClickListener(new BtnClick2());
		findViewById(R.id.dialButton).setOnClickListener(new BtnClick2());

		// 回删
		backImageView = (ImageView) findViewById(R.id.backButton);
		backImageView.setOnClickListener(new BtnClick2());
		backImageView.setOnLongClickListener(new OnLongClickListener() {// 长按清除

					@Override
					public boolean onLongClick(View arg0) {
						// TODO Auto-generated method stub
						Vibrator vib = (Vibrator) MainActivity.this
								.getSystemService(Service.VIBRATOR_SERVICE);
						vib.vibrate(50);
						EditNum.setText("");
						return false;
					}
				});
		// 清除
		backButton = (Button) findViewById(R.id.qingchu);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				EditNum.setText("");
			}
		});

		// 发送广播 更新联系人
		IntentFilter filter = new IntentFilter();
		filter.addAction("huahua.action.UpdataContactsView");
		MainActivity.this.registerReceiver(mReceiver, filter);

		// 观察者 读取联系人
		ContactsCO = new ContactsContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, false,
				ContactsCO);

		// 得到字母列的对象,并设置触摸响应监听器
		m_asb = (ABCDEFG) findViewById(R.id.alphabetscrollbar);
		m_asb.setOnTouchBarListener((OnTouchBarListener) new ScrollBarListener());
		m_letterNotice = (TextView) findViewById(R.id.pb_letter_notice);
		m_asb.setTextView(m_letterNotice);

		// 得到联系人列表,并设置适配器
		m_contactslist = (ListView) findViewById(R.id.pb_listvew);
		m_contactsadapter = new ContactsAdapter(MainActivity.this,
				Utils.persons);
		m_contactslist.setAdapter(m_contactsadapter);

		// 联系人列表长按监听
		m_contactslist
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						Vibrator vib = (Vibrator) MainActivity.this
								.getSystemService(Service.VIBRATOR_SERVICE);
						vib.vibrate(50);

						if (m_topcontactslayout.getVisibility() == View.VISIBLE) {
							ChooseContactName = Utils.persons.get(arg2).Name;
							ChooseContactNumber = Utils.persons.get(arg2).Number;
							ChooseContactID = Utils.persons.get(arg2).ID;
						} else {
							ChooseContactName = Utils.filterpersons.get(arg2).Name;
							ChooseContactNumber = Utils.filterpersons.get(arg2).Number;
							ChooseContactID = Utils.filterpersons.get(arg2).ID;
						}

						final Person_Sms personsms = new Person_Sms();
						personsms.Name = ChooseContactName;
						personsms.Number = ChooseContactNumber;
						for (int i = 0; i < Utils.persons_sms.size(); i++) {
							if (personsms.Name.equals(Utils.persons_sms.get(i).Name)) {
								personsms.person_smss = Utils.persons_sms
										.get(i).person_smss;
								break;
							}
						}

						AlertDialog ListDialog = new AlertDialog.Builder(
								MainActivity.this)
								.setTitle(
										ChooseContactName + " "
												+ ChooseContactNumber)

								.setItems(
										new String[] { "拨号", "短信", "删除联系人",
												"编辑联系人", "设为黑名单" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (which == 0) {
													Intent intent = new Intent(
															Intent.ACTION_CALL,
															Uri.parse("tel://"
																	+ ChooseContactNumber));
													startActivity(intent);
												} else if (which == 1) {
													Intent intent = new Intent(
															MainActivity.this,
															ChatActivity.class);
													Bundle mBundle = new Bundle();
													mBundle.putSerializable(
															"chatperson",
															personsms);
													intent.putExtras(mBundle);
													startActivity(intent);
												} else if (which == 2) {
													AlertDialog DeleteDialog = new AlertDialog.Builder(
															MainActivity.this)
															.setTitle("删除")
															.setMessage(
																	"删除联系人"
																			+ ChooseContactName
																			+ "?")
															.setPositiveButton(
																	"确定",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			// 删除联系人操作,放在线程中处理
																			new DeleteContactTask()
																					.execute();
																		}
																	})
															.setNegativeButton(
																	"取消",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	}).create();
													DeleteDialog.show();
												} else if (which == 3) {
													Bundle bundle = new Bundle();
													bundle.putInt("tpye", 1);
													bundle.putString("id",
															ChooseContactID);
													bundle.putString("name",
															ChooseContactName);
													bundle.putString("number",
															ChooseContactNumber);

													Intent intent = new Intent(
															MainActivity.this,
															AddContactsActivity.class);
													intent.putExtra("person",
															bundle);
													startActivity(intent);
												}

												else if (which == 4) {

													AlertDialog DeleteDialog = new AlertDialog.Builder(
															MainActivity.this)
															.setTitle("黑名单")
															.setMessage(
																	"加入黑名单"
																			+ ChooseContactName
																			+ "?")
															.setPositiveButton(
																	"确定",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			ChooseContactNumber
																					.replace(
																							"-",
																							"")
																					.replace(
																							" ",
																							"");
																			Persons2 blocklist2 = new Persons2();
																			blocklist2.Number = ChooseContactNumber;
																			blockList
																					.add(blocklist2);

																		}
																	})
															.setNegativeButton(
																	"取消",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	}).create();
													DeleteDialog.show();

													// box.setText(ChooseContactNumber);
													// if
													// (isBlock(ChooseContactNumber))
													// {
													// box.setChecked(true);
													// }
													// return box;

												}
											}
										}).create();
						ListDialog.show();
						return false;
					}

				});

		m_listEmptyText = (TextView) findViewById(R.id.nocontacts_notice);
		// 新增联系人
		m_AddContactBtn = (ImageView) findViewById(R.id.add_contacts1);
		m_AddContactBtn.setOnClickListener(new BtnClick());
		// 去重联系人
		m_RemoveSameContactBtn = (Button) findViewById(R.id.remove_same_contacts);
		m_RemoveSameContactBtn.setOnClickListener(new BtnClick());

		m_topcontactslayout = (FrameLayout) findViewById(R.id.top_contacts_layout);

		// 初始化搜索编辑框,设置文本改变时的监听器
		m_FilterEditText = (EditText) findViewById(R.id.pb_search_edit);
		m_FilterEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (!"".equals(s.toString().trim())) {
					// 根据编辑框值过滤联系人并更新联系列表
					Utils.filterContacts(s.toString().trim());
					m_asb.setVisibility(View.GONE);
					m_topcontactslayout.setVisibility(View.GONE);
				} else {
					m_topcontactslayout.setVisibility(View.VISIBLE);
					m_asb.setVisibility(View.VISIBLE);
					m_contactsadapter.updateListView(Utils.persons);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		IntentFilter filter2 = new IntentFilter();
		filter2.addAction("huahua.action.UpdataSmssView");
		MainActivity.this.registerReceiver(mReceiver2, filter2);

		SmssCO = new SmssContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				Uri.parse("content://sms/"), false, SmssCO);

		m_smsslist = (ListView) findViewById(R.id.sms_list);
		m_smsadapter = new SmsAdapter(MainActivity.this, Utils.persons_sms);
		m_smsslist.setAdapter(m_smsadapter);
		m_smsslist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent intent = new Intent(MainActivity.this,
						ChatActivity.class);

				Bundle mBundle = new Bundle();

				mBundle.putSerializable("chatperson",
						Utils.persons_sms.get(arg2));
				intent.putExtras(mBundle);
				startActivity(intent);

			}
		});

		// 发送广播 当通话记录列表数据发生变化时,来更新列表
		IntentFilter filter3 = new IntentFilter();
		filter3.addAction("huahua.action.UpdataCallLogsView");// 设置广播发送的内容
		MainActivity.this.registerReceiver(mReceiver3, filter3);// 注册

		// 使用观察者 读取通话记录
		CallLogsCO = new CallHistoryContentObserver(new Handler());
		MainActivity.this.getContentResolver().registerContentObserver(
				CallLog.Calls.CONTENT_URI, false, CallLogsCO);

		// 通话记录列表 及设置适配器
		callHistoryListView = (ListView) findViewById(R.id.calllogs_list);
		calllogsadapter = new CallHistoryAdapter(MainActivity.this,
				Utils.calllogs);
		callHistoryListView.setAdapter(calllogsadapter);
		// 通话记录长按效果
		callHistoryListView
				.setOnItemLongClickListener(new OnItemLongClickListener() {

					@Override
					public boolean onItemLongClick(AdapterView<?> arg0,
							View arg1, int arg2, long arg3) {

						Vibrator vib = (Vibrator) MainActivity.this
								.getSystemService(Service.VIBRATOR_SERVICE);
						vib.vibrate(50);

						if (m_topcontactslayout.getVisibility() == View.VISIBLE) {
							ChooseContactName = Utils.calllogs.get(arg2).Name;
							ChooseContactNumber = Utils.calllogs.get(arg2).Number;
							// ChooseContactID = Utils.calllogs.get(arg2).ID;
						} else {
							ChooseContactName = Utils.calllogs.get(arg2).Name;
							ChooseContactNumber = Utils.calllogs.get(arg2).Number;
							// ChooseContactID = Utils.calllogs.get(arg2).ID;
						}

						if (ChooseContactName == null) {
							ChooseContactName = "未知号码";

						}

						final Person_Sms personsms = new Person_Sms();
						personsms.Name = ChooseContactName;
						personsms.Number = ChooseContactNumber;
						for (int i = 0; i < Utils.persons_sms.size(); i++) {
							if (personsms.Name.equals(Utils.persons_sms.get(i).Name)) {
								personsms.person_smss = Utils.persons_sms
										.get(i).person_smss;
								break;
							}
						}

						AlertDialog ListDialog = new AlertDialog.Builder(
								MainActivity.this)
								.setTitle(
										ChooseContactName + " "
												+ ChooseContactNumber)
								.setItems(
										new String[] { "拨号", "短信", "删除联系人",
												"编辑联系人" },
										new DialogInterface.OnClickListener() {

											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												if (which == 0) {
													Intent intent = new Intent(
															Intent.ACTION_CALL,
															Uri.parse("tel://"
																	+ ChooseContactNumber));
													startActivity(intent);
												} else if (which == 1) {
													Intent intent = new Intent(
															MainActivity.this,
															ChatActivity.class);
													Bundle mBundle = new Bundle();
													mBundle.putSerializable(
															"chatperson",
															personsms);
													intent.putExtras(mBundle);
													startActivity(intent);
												} else if (which == 2) {
													AlertDialog DeleteDialog = new AlertDialog.Builder(
															MainActivity.this)
															.setTitle("删除")
															.setMessage(
																	"删除联系人"
																			+ ChooseContactName
																			+ "?")
															.setPositiveButton(
																	"确定",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {
																			// 删除联系人操作,放在线程中处理
																			new DeleteContactTask()
																					.execute();
																		}
																	})
															.setNegativeButton(
																	"取消",
																	new DialogInterface.OnClickListener() {

																		@Override
																		public void onClick(
																				DialogInterface dialog,
																				int which) {

																		}
																	}).create();
													DeleteDialog.show();
												} else if (which == 3) {
													Bundle bundle = new Bundle();
													bundle.putInt("tpye", 1);
													bundle.putString("id",
															ChooseContactID);
													bundle.putString("name",
															ChooseContactName);
													bundle.putString("number",
															ChooseContactNumber);

													Intent intent = new Intent(
															MainActivity.this,
															AddContactsActivity.class);
													intent.putExtra("person",
															bundle);
													startActivity(intent);
												}

											}
										}).create();
						ListDialog.show();

						return false;
					}
				});

	}

	// 接收 当通话记录列表数据发生变化时,来更新列表 的广播
	BroadcastReceiver mReceiver3 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataCallLogsView")) {
				calllogsadapter.updateListView(Utils.calllogs);
			}
		}
	};

	class DeleteContactTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Utils.DeleteContact(ChooseContactID);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(MainActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			m_dialogLoading.setMessage("正在删除");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("huahua", "onProgressUpdate");
		}

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataContactsView")) {
				if (m_topcontactslayout.getVisibility() == View.VISIBLE) {
					m_contactsadapter.updateListView(Utils.persons);
				} else {
					// 如果没有匹配的联系人
					if (Utils.filterpersons.isEmpty()) {
						m_contactslist.setEmptyView(m_listEmptyText);
					}

					m_contactsadapter.updateListView(Utils.filterpersons);
				}

			}
		}
	};

	private class BtnClick implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v == m_AddContactBtn) {
				Bundle bundle = new Bundle();
				bundle.putInt("tpye", 0);
				bundle.putString("name", "");

				bundle.putString("number", "");

				Intent intent = new Intent(MainActivity.this,
						AddContactsActivity.class);
				intent.putExtra("person", bundle);
				startActivity(intent);
			} else if (v == m_RemoveSameContactBtn) {
				AlertDialog alertDialog = new AlertDialog.Builder(
						MainActivity.this)
						.setTitle("联系人去重")
						.setMessage("名字和号码都相同的联系人只保留一个?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// 删除联系人操作,放在线程中处理
										new DeleteSameContactsTask().execute();
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

									}
								}).create();
				alertDialog.show();
			}
			// Intent intent=new Intent(MainActivity.this,MainActivity.class);
			// MainActivity.this.startActivity(intent);
		}
	}

	class DeleteSameContactsTask extends AsyncTask<Void, Integer, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			Utils.DeleteSameContacts();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (m_dialogLoading != null) {
				m_dialogLoading.dismiss();
			}
		}

		@Override
		protected void onPreExecute() {
			m_dialogLoading = new ProgressDialog(MainActivity.this);
			m_dialogLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置风格为圆形进度条
			m_dialogLoading.setMessage("正在删除重复联系人");
			m_dialogLoading.setCancelable(false);
			m_dialogLoading.show();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Log.i("huahua", "onProgressUpdate");
		}

	}

	@Override
	public void onDestroy() {
		MainActivity.this.unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public class ContactsAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<Persons> persons;
		private Context context;

		public ContactsAdapter(Context context, ArrayList<Persons> persons) {
			this.m_inflater = LayoutInflater.from(context);
			this.persons = persons;
			this.context = context;
		}

		// 当联系人列表数据发生变化时,用此方法来更新列表
		public void updateListView(ArrayList<Persons> persons) {
			this.persons = persons;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return persons.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.contacts_list_item,
						null);
			}

			if (persons.isEmpty()) {
				Log.i("huahua", "为空");
				return convertView;
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.contacts_name);
			name.setText(persons.get(position).Name);

			TextView number = (TextView) convertView
					.findViewById(R.id.contacts_number);
			number.setText(persons.get(position).Number);

			// 字母提示textview的显示
			TextView letterTag = (TextView) convertView
					.findViewById(R.id.pb_item_LetterTag);
			// 获得当前姓名的拼音首字母
			String firstLetter = persons.get(position).PY.substring(0, 1)
					.toUpperCase();

			// 如果是第1个联系人 那么letterTag始终要显示
			if (position == 0) {
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			} else {
				// 获得上一个姓名的拼音首字母
				String firstLetterPre = persons.get(position - 1).PY.substring(
						0, 1).toUpperCase();
				// 比较一下两者是否相同
				if (firstLetter.equals(firstLetterPre)) {
					letterTag.setVisibility(View.GONE);
				} else {
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}

			return convertView;
		}
	}

	public class SmsAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<Person_Sms> persons_sms;
		private Context context;

		public SmsAdapter(Context context, ArrayList<Person_Sms> persons_sms) {
			this.m_inflater = LayoutInflater.from(context);
			this.persons_sms = persons_sms;
			this.context = context;
		}

		public void updateListView(ArrayList<Person_Sms> persons_sms) {
			this.persons_sms = persons_sms;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons_sms.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return persons_sms.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.sms_list_item, null);
			}

			TextView sms_name = (TextView) convertView
					.findViewById(R.id.sms_name);
			sms_name.setText(persons_sms.get(position).Name + "("
					+ persons_sms.get(position).person_smss.size() + ")");

			TextView sms_content = (TextView) convertView
					.findViewById(R.id.sms_content);
			sms_content
					.setText(persons_sms.get(position).person_smss.get(0).SMSContent);

			TextView sms_data = (TextView) convertView
					.findViewById(R.id.sms_date);
			Date date = new Date(
					persons_sms.get(position).person_smss.get(0).SMSDate);
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			String time = sfd.format(date);
			sms_data.setText(time);

			return convertView;
		}

	}

	public void onDestroy2() {
		MainActivity.this.unregisterReceiver(mReceiver2);
		super.onDestroy();
	}

	BroadcastReceiver mReceiver2 = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("huahua.action.UpdataSmssView")) {
				m_smsadapter.updateListView(Utils.persons_sms);
			}
		}
	};

	// 数字选择输入
	private class BtnClick2 implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.number1) {
				keyPressed(KeyEvent.KEYCODE_1);
			} else if (v.getId() == R.id.number2) {
				keyPressed(KeyEvent.KEYCODE_2);
			} else if (v.getId() == R.id.number3) {
				keyPressed(KeyEvent.KEYCODE_3);
			} else if (v.getId() == R.id.number4) {
				keyPressed(KeyEvent.KEYCODE_4);
			} else if (v.getId() == R.id.number5) {
				keyPressed(KeyEvent.KEYCODE_5);
			} else if (v.getId() == R.id.number6) {
				keyPressed(KeyEvent.KEYCODE_6);
			} else if (v.getId() == R.id.number7) {
				keyPressed(KeyEvent.KEYCODE_7);
			} else if (v.getId() == R.id.number8) {
				keyPressed(KeyEvent.KEYCODE_8);
			} else if (v.getId() == R.id.number9) {
				keyPressed(KeyEvent.KEYCODE_9);
			} else if (v.getId() == R.id.number0) {
				keyPressed(KeyEvent.KEYCODE_0);
			} else if (v.getId() == R.id.number_xing) {
				keyPressed(KeyEvent.KEYCODE_STAR);
			} else if (v.getId() == R.id.number_jing) {
				keyPressed(KeyEvent.KEYCODE_POUND);
			} else if (v.getId() == R.id.dialButton) {
				if (EditNum.length() != 0) {
					Intent intent = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel://" + EditNum.getText().toString()));
					startActivity(intent);
				} else {
					Toast.makeText(MainActivity.this, "请输入号码",
							Toast.LENGTH_SHORT).show();
				}
			} else if (v.getId() == R.id.backButton) {
				keyPressed(KeyEvent.KEYCODE_DEL);
			}
		}
	}

	// 数字点击响应输入
	private void keyPressed(int keyCode) {
		KeyEvent event = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
		EditNum.onKeyDown(keyCode, event);
	}

	// 通话记录适配器
	public class CallHistoryAdapter extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<CallLogs> calllogs;
		private Context context;

		public CallHistoryAdapter(Context context, ArrayList<CallLogs> calllogs) {
			this.m_inflater = LayoutInflater.from(context);
			this.calllogs = calllogs;
			this.context = context;
		}

		// 当联系人列表数据发生变化时,用此方法来更新列表
		public void updateListView(ArrayList<CallLogs> calllogs) {
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
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.calllogs_list_item,
						null);
			}

			if (calllogs.isEmpty()) {
				return convertView;
			}

			// 通话记录的姓名
			TextView name = (TextView) convertView
					.findViewById(R.id.calllog_name);
			name.setText(calllogs.get(position).Name);

			if (calllogs.get(position).Name == null) {
				name.setText("未知号码");
			} else {
				name.setText(calllogs.get(position).Name);
			}

			// 通话记录的电话状态
			TextView Type = (TextView) convertView
					.findViewById(R.id.calllog_type);
			if (calllogs.get(position).Type == CallLog.Calls.INCOMING_TYPE) {
				Type.setText("已接来电");
				Type.setTextColor(Color.rgb(0, 0, 255));
			} else if (calllogs.get(position).Type == CallLog.Calls.OUTGOING_TYPE) {
				Type.setText("拨出号码");
				Type.setTextColor(Color.rgb(0, 150, 0));
			} else if (calllogs.get(position).Type == CallLog.Calls.MISSED_TYPE) {
				Type.setText("未接来电");
				Type.setTextColor(Color.rgb(255, 0, 0));
			}

			// 通话记录的号码
			TextView number = (TextView) convertView
					.findViewById(R.id.calllog_number);
			number.setText(calllogs.get(position).Number);

			// 通话记录的日期
			TextView data = (TextView) convertView
					.findViewById(R.id.calllog_data);

			Date date2 = new Date(Long.parseLong(calllogs.get(position).Data));
			SimpleDateFormat sfd = new SimpleDateFormat("yyyy/MM/dd");
			String time = sfd.format(date2);
			data.setText(time);

			// 点击图片进行通话记录拨号
			ImageView dialBtn = (ImageView) convertView
					.findViewById(R.id.calllog_dial);
			dialBtn.setTag(calllogs.get(position).Number);
			dialBtn.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent(Intent.ACTION_CALL, Uri
							.parse("tel://" + (String) arg0.getTag()));
					MainActivity.this.startActivity(intent);
				}
			});

			return convertView;
		}
	}

	public void onDestroy3() {
		MainActivity.this.unregisterReceiver(mReceiver3);
		super.onDestroy();
	}

	// 四个界面布局及滑动
	private void init() {
		dialing = (TextView) findViewById(R.id.dialing);
		contactPerson = (TextView) findViewById(R.id.contactPerson);
		information = (TextView) findViewById(R.id.information);
		usecenter = (TextView) findViewById(R.id.usecenter);

		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	// 四个界面布局及滑动
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			dialing.setTextColor(0xff228B22);
			contactPerson.setTextColor(Color.BLACK);
			information.setTextColor(Color.BLACK);
			usecenter.setTextColor(Color.BLACK);

		} else if (index == 1) {
			dialing.setTextColor(Color.BLACK);
			contactPerson.setTextColor(0xff228B22);
			information.setTextColor(Color.BLACK);
			usecenter.setTextColor(Color.BLACK);

		} else if (index == 2) {
			dialing.setTextColor(Color.BLACK);
			contactPerson.setTextColor(Color.BLACK);
			information.setTextColor(0xff228B22);
			usecenter.setTextColor(Color.BLACK);

		} else {
			dialing.setTextColor(Color.BLACK);
			contactPerson.setTextColor(Color.BLACK);
			information.setTextColor(Color.BLACK);
			usecenter.setTextColor(0xff228B22);
		}
	}

	public class MyPhoneCallListener extends PhoneStateListener {
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			AudioManager audioManger = (AudioManager) MainActivity.this
					.getSystemService(Context.AUDIO_SERVICE);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:
				Toast.makeText(MainActivity.this, incomingNumber,
						Toast.LENGTH_SHORT).show();
				if (isBlock(incomingNumber)) {
					audioManger.setRingerMode(AudioManager.RINGER_MODE_SILENT);
					// TelephonyManager.CALL_STATE_IDLE:

				}

				break;

			case TelephonyManager.CALL_STATE_OFFHOOK:
				i = i + 1;
				Toast.makeText(MainActivity.this, "通话中", Toast.LENGTH_SHORT)
						.show();

				break;

			case TelephonyManager.CALL_STATE_IDLE:
				if (i == 1) {
					Toast.makeText(MainActivity.this, "挂断", Toast.LENGTH_SHORT)
							.show();
					audioManger.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
				}
				if (i == 0) {
					Toast.makeText(MainActivity.this, "拒接接听",
							Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;

			}

		}
	}

	public class ContactsAdapter1 extends BaseAdapter {
		private LayoutInflater m_inflater;
		private ArrayList<Persons> persons;
		private Context context;

		public ContactsAdapter1(Context context, ArrayList<Persons> persons) {
			this.m_inflater = LayoutInflater.from(context);
			this.persons = persons;
			this.context = context;
		}

		// 当联系人列表数据发生变化时,用此方法来更新列表
		public void updateListView(ArrayList<Persons> persons) {
			this.persons = persons;
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return persons.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return persons.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = m_inflater.inflate(R.layout.contacts_list_item,
						null);
			}

			if (persons.isEmpty()) {
				Log.i("huahua", "为空");
				return convertView;
			}

			TextView name = (TextView) convertView
					.findViewById(R.id.contacts_name);
			name.setText(persons.get(position).Name);

			TextView number = (TextView) convertView
					.findViewById(R.id.contacts_number);
			number.setText(persons.get(position).Number);

			// 字母提示textview的显示
			TextView letterTag = (TextView) convertView
					.findViewById(R.id.pb_item_LetterTag);
			// 获得当前姓名的拼音首字母
			String firstLetter = persons.get(position).PY.substring(0, 1)
					.toUpperCase();

			// 如果是第1个联系人 那么letterTag始终要显示
			if (position == 0) {
				letterTag.setVisibility(View.VISIBLE);
				letterTag.setText(firstLetter);
			} else {
				// 获得上一个姓名的拼音首字母
				String firstLetterPre = persons.get(position - 1).PY.substring(
						0, 1).toUpperCase();
				// 比较一下两者是否相同
				if (firstLetter.equals(firstLetterPre)) {
					letterTag.setVisibility(View.GONE);
				} else {
					letterTag.setVisibility(View.VISIBLE);
					letterTag.setText(firstLetter);
				}
			}

			return convertView;
		}

	}

	// 字母列触摸的监听器
	private class ScrollBarListener implements ABCDEFG.OnTouchBarListener {

		@Override
		public void onTouch(String letter) {

			// 触摸字母列时,将联系人列表更新到首字母出现的位置
			for (int i = 0; i < Utils.persons.size(); i++) {
				if (Utils.persons.get(i).PY.substring(0, 1)
						.compareToIgnoreCase(letter) == 0) {
					m_contactslist.setSelection(i);
					break;
				}
			}
		}
	}

	private boolean isBlock(String number) {
		// TODO Auto-generated method stub
		// 判断号码是否在blockList集合中
		for (Persons2 s : blockList) {
			if (s.equals(number)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void OnViewChange(int view) {
		// TODO Auto-generated method stub
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

}
