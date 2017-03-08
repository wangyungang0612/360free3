package com.example.M360;



import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyScrollLayout extends ViewGroup{

    private static final String TAG = "ScrollLayout";      
    private VelocityTracker mVelocityTracker;  			//�����ж�˦������
    private static final int SNAP_VELOCITY = 400;   //������ͼ��     
    private Scroller  mScroller;						// ����������
    private int mCurScreen; //��ǰҳ��   						    
	private int mDefaultScreen = 0;  //Ĭ��ҳ��  						 
    private float mLastMotionX; //�ֶ�������x      
    private float mLastMotionY;       
    
    private boolean isPass = false;
 //   private int mTouchSlop;							
    
//    private static final int TOUCH_STATE_REST = 0;
//    private static final int TOUCH_STATE_SCROLLING = 1;
//    private int mTouchState = TOUCH_STATE_REST;
    
    private OnViewChangeListener mOnViewChangeListener;	 
	public MyScrollLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}	
	public MyScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public MyScrollLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub		
		init(context);
	}
	
	private void init(Context context)
	{
		mCurScreen = mDefaultScreen;    //Ĭ��������ʾ��һ��vie	  
	 //   mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();    	        
	    mScroller = new Scroller(context); 
	    
	}
	
	//OnLayout()��������ÿ����view����Ļ�Ǹ�λ������ʾ����view��������OnLayout()�������������Ǵ����Һ�����
	//ÿ����view������OnLayout���������ʵ������ʲô
	//�ط���ʾ��view����ʾ�ķ�Χ�Ƕ������������ܸı�ÿ��viewԭ���Ĵ�С��

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub		
		 if (changed) {    
	            int childLeft = 0;  //������0��ʼ�滭  
	            final int childCount = getChildCount();  //�����е���view�߶ȺͿ�ȶ�����ɺ͸�viewһ��  	                
	            for (int i=0; i<childCount; i++) {    
	                final View childView = getChildAt(i);    
	                if (childView.getVisibility() != View.GONE) {    
	                    final int childWidth = childView.getMeasuredWidth();    
	                    childView.layout(childLeft, 0,     
	                            childLeft+childWidth, childView.getMeasuredHeight());    
	                    childLeft += childWidth;    
	                }    
	            }    
	        }    
	}
//OnLayout()��onMeasureֻ��������view��С�ͻ滭��λ��
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		final int width = MeasureSpec.getSize(widthMeasureSpec);   //ת�����    
	    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);      
	    		
		final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);       
        }                
        scrollTo(mCurScreen * width, 0);	//������Ĭ����Ļ	
	}

	 public void snapToDestination() {    
	        final int screenWidth = getWidth();    
	        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;    
	        snapToScreen(destScreen);    
	 }  
	
	 public void snapToScreen(int whichScreen) {    	
	        // get the valid layout page    
	        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));    
	        if (getScrollX() != (whichScreen*getWidth())) {    	                
	            final int delta = whichScreen*getWidth()-getScrollX();    
	      	            mScroller.startScroll(getScrollX(), 0,     
	                    delta, 0, 300);
	            
	            mCurScreen = whichScreen;    
	            invalidate();       // Redraw the layout    	            
	            if (mOnViewChangeListener != null)
	            {
	            	mOnViewChangeListener.OnViewChange(mCurScreen);
	            }
	        }    
	    }    

	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if (mScroller.computeScrollOffset()) {    
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();    
        }   
	}
//******ʵ�ֻ�����Ч������Ҫʵ�ֻ�����������MotionEvent.ACTION_MOVE: ����� scrollBy(X, y);��x��ʾˮƽ�����ľ��룬y��ʾ��ֱ�����ľ���
	//����Ϊ����ֻ��ˮƽ����������y��������Ϊ0
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub           	            
	        final int action = event.getAction();    
	        final float x = event.getX();    
	        final float y = event.getY();    
	            
	        switch (action) {    
	        case MotionEvent.ACTION_DOWN: 	
	        	System.out.println("������onTouchEvent");
	        	  Log.i("", "onTouchEvent  ACTION_DOWN");	        	  
	        	if (mVelocityTracker == null) {    //��ȡ������׽��
			            mVelocityTracker = VelocityTracker.obtain();    
			            mVelocityTracker.addMovement(event); //��ʼ��׽����
			    }        	 
	            if (!mScroller.isFinished()){    
	                mScroller.abortAnimation();    
	            }                
	            mLastMotionX = x;	           
	            mLastMotionY = y;	           
	            break;    
	                
	        case MotionEvent.ACTION_MOVE:  
	        	System.out.println("���໬��onTouchEvent");
		           int deltaX = (int)(mLastMotionX - x);	           
	        	   if (IsCanMove(deltaX))
	        	   {
	        		 if (mVelocityTracker != null)
	  		         {
	  		            	mVelocityTracker.addMovement(event); 
	  		         }   
	  	            mLastMotionX = x;     
	  	            scrollBy(deltaX, 0);	
	        	   }
         
	           break;   
	     //���Ҫ��һЩ����Ӧ�������������ɿ���ͻ�����ĳ��������view��ҳ�棬����Ҫʹ��scroller��MotionEvent.ACTION_UP: 
	           //�����ã�scroller�ǿ��Կ�������ҳ��Ļ���������MotionEvent.ACTION_UP: ����ʱ�ж���ָ�������ٶȣ������Լ�����
	           //һ���ٶȵĻ�׼�����ﵽ�Ϳ���������û�дﵽ���жϻ�����λ���Ƿ񳬹���ǰ��Ļ��һ�룬�����Ϳ�����������Ȼ�ָ�����ǰ��Ļ��
	        case MotionEvent.ACTION_UP:  //���ֺ�Ķ���     
	        	System.out.println("����ſ�onTouchEvent");
	        	int velocityX = 0;
	            if (mVelocityTracker != null)
	            {
	            	mVelocityTracker.addMovement(event); 
	            	mVelocityTracker.computeCurrentVelocity(1000); //���㻬�����ٶȣ�����1000��ʾ1000����Ϊ��λ���㣬��Ϊ��Ϊ��λ
	            	velocityX = (int) mVelocityTracker.getXVelocity();
	            }
	            //һ���ٶȵĻ�׼�����ﵽ�Ϳ���������û�дﵽ���жϻ�����λ���Ƿ񳬹���ǰ��Ļ��һ�룬�����Ϳ�����������Ȼ�ָ�����ǰ��Ļ��
	            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {       
	                // Fling enough to move left       
	                Log.e(TAG, "snap left");    
	                snapToScreen(mCurScreen - 1);       
	            } else if (velocityX < -SNAP_VELOCITY       
	                    && mCurScreen < getChildCount() - 1) {       
	                // Fling enough to move right       
	                Log.e(TAG, "snap right");    
	                snapToScreen(mCurScreen + 1);       
	            } else {       
	                snapToDestination();       
	            }      
	            	            
	            if (mVelocityTracker != null) {       
	                mVelocityTracker.recycle();       
	                mVelocityTracker = null;       
	            }       
	      //      mTouchState = TOUCH_STATE_REST;
	            break;      
	        }    	            
	        return true;    
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			System.out.println("������onInterceptTouchEvent");
			if(isPass){
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE: 
			System.out.println("���໬��onInterceptTouchEvent");
			if(isPass){
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("����ſ�onInterceptTouchEvent");
			break;
		}
		return super.onInterceptTouchEvent(event);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: 
			mLastMotionX = event.getX();	           
            mLastMotionY = event.getY();
			System.out.println("������dispatchTouchEvent");
			break;
		case MotionEvent.ACTION_MOVE: 
			System.out.println(Math.abs(event.getX()- mLastMotionX));
			System.out.println(Math.abs(event.getY()- mLastMotionY));
			double tanNum = Math.atan(Math.abs(event.getY()-mLastMotionY)/Math.abs(event.getX()- mLastMotionX));
			double retote = tanNum/3.14*180;
			System.out.println("�Ƕ�:"+retote);
			if (retote<45) {
				System.out.println("---------���໬��dispatchTouchEvent");
				isPass= true;
			}else{
				isPass = false;
			}
			onInterceptTouchEvent(event);
			System.out.println("***************"+isPass);
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("����ſ�dispatchTouchEvent");
			break;
		}
		return super.dispatchTouchEvent(event);
	}

	private boolean IsCanMove(int deltaX)
	{
		if (getScrollX() <= 0 && deltaX < 0 ){
			return false;
		}	
		if  (getScrollX() >=  (getChildCount() - 1) * getWidth() && deltaX > 0){
			return false;
		}		
		return true;
	}
	
	public void SetOnViewChangeListener(OnViewChangeListener listener)
	{
		mOnViewChangeListener = listener;
	}
	
	
}