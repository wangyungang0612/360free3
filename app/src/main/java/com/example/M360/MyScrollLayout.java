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
    private VelocityTracker mVelocityTracker;  			//用于判断甩动手势
    private static final int SNAP_VELOCITY = 400;   //滑动视图的     
    private Scroller  mScroller;						// 滑动控制器
    private int mCurScreen; //当前页面   						    
	private int mDefaultScreen = 0;  //默认页面  						 
    private float mLastMotionX; //手动滑动的x      
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
		mCurScreen = mDefaultScreen;    //默认设置显示第一个vie	  
	 //   mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();    	        
	    mScroller = new Scroller(context); 
	    
	}
	
	//OnLayout()就是设置每个子view在屏幕那个位置来显示出子view，现在在OnLayout()方法里面设置是从左到右横向绘出
	//每个子view，其中OnLayout这个方法的实现是在什么
	//地方显示子view，显示的范围是多大，这个方法不能改变每个view原本的大小的

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub		
		 if (changed) {    
	            int childLeft = 0;  //从坐标0开始绘画  
	            final int childCount = getChildCount();  //将所有的子view高度和宽度都定义成和父view一样  	                
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
//OnLayout()和onMeasure只是设置了view大小和绘画的位置
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);		
		final int width = MeasureSpec.getSize(widthMeasureSpec);   //转换宽度    
	    final int widthMode = MeasureSpec.getMode(widthMeasureSpec);      
	    		
		final int count = getChildCount();       
        for (int i = 0; i < count; i++) {       
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);       
        }                
        scrollTo(mCurScreen * width, 0);	//滑动到默认屏幕	
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
//******实现滑动的效果，主要实现滑动动作是在MotionEvent.ACTION_MOVE: 里面的 scrollBy(X, y);，x表示水平滑动的距离，y表示垂直滑动的距离
	//，因为这里只有水平滑动，所以y可以设置为0
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub           	            
	        final int action = event.getAction();    
	        final float x = event.getX();    
	        final float y = event.getY();    
	            
	        switch (action) {    
	        case MotionEvent.ACTION_DOWN: 	
	        	System.out.println("锟斤拷锟斤拷锟斤拷onTouchEvent");
	        	  Log.i("", "onTouchEvent  ACTION_DOWN");	        	  
	        	if (mVelocityTracker == null) {    //获取动作捕捉器
			            mVelocityTracker = VelocityTracker.obtain();    
			            mVelocityTracker.addMovement(event); //开始捕捉动作
			    }        	 
	            if (!mScroller.isFinished()){    
	                mScroller.abortAnimation();    
	            }                
	            mLastMotionX = x;	           
	            mLastMotionY = y;	           
	            break;    
	                
	        case MotionEvent.ACTION_MOVE:  
	        	System.out.println("锟斤拷锟洁滑锟斤拷onTouchEvent");
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
	     //如果要像一些桌面应用那样，当手松开后就滑动到某个完整的view的页面，就需要使用scroller和MotionEvent.ACTION_UP: 
	           //上设置，scroller是可以控制整个页面的滑动，而在MotionEvent.ACTION_UP: 上面时判断手指滑动的速度，可以自己定义
	           //一个速度的基准，当达到就跨屏滑动，没有达到就判断滑动的位置是否超过当前屏幕的一半，超过就跨屏，否则仍然恢复到当前屏幕。
	        case MotionEvent.ACTION_UP:  //放手后的动作     
	        	System.out.println("锟斤拷锟斤拷趴锟給nTouchEvent");
	        	int velocityX = 0;
	            if (mVelocityTracker != null)
	            {
	            	mVelocityTracker.addMovement(event); 
	            	mVelocityTracker.computeCurrentVelocity(1000); //计算滑动的速度，差数1000表示1000毫秒为单位计算，即为秒为单位
	            	velocityX = (int) mVelocityTracker.getXVelocity();
	            }
	            //一个速度的基准，当达到就跨屏滑动，没有达到就判断滑动的位置是否超过当前屏幕的一半，超过就跨屏，否则仍然恢复到当前屏幕。
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
			System.out.println("锟斤拷锟斤拷锟斤拷onInterceptTouchEvent");
			if(isPass){
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE: 
			System.out.println("锟斤拷锟洁滑锟斤拷onInterceptTouchEvent");
			if(isPass){
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("锟斤拷锟斤拷趴锟給nInterceptTouchEvent");
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
			System.out.println("锟斤拷锟斤拷锟斤拷dispatchTouchEvent");
			break;
		case MotionEvent.ACTION_MOVE: 
			System.out.println(Math.abs(event.getX()- mLastMotionX));
			System.out.println(Math.abs(event.getY()- mLastMotionY));
			double tanNum = Math.atan(Math.abs(event.getY()-mLastMotionY)/Math.abs(event.getX()- mLastMotionX));
			double retote = tanNum/3.14*180;
			System.out.println("锟角讹拷:"+retote);
			if (retote<45) {
				System.out.println("---------锟斤拷锟洁滑锟斤拷dispatchTouchEvent");
				isPass= true;
			}else{
				isPass = false;
			}
			onInterceptTouchEvent(event);
			System.out.println("***************"+isPass);
			break;
		case MotionEvent.ACTION_UP:
			System.out.println("锟斤拷锟斤拷趴锟絛ispatchTouchEvent");
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