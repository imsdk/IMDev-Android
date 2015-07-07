package com.imsdk.imdeveloper.ui.view.gridview;

import java.util.HashMap;
import java.util.List;

import com.imsdk.imdeveloper.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class GroupInfoAdapter extends BaseAdapter{

	private List<HashMap<String,Object>> mList;
    private Context mContext=null;  
    private LayoutInflater inflater=null;
    private boolean delShow = false;
    private View.OnClickListener mRemoveMemberListener;
	
    public GroupInfoAdapter(Context context,List<HashMap<String,Object>> list, View.OnClickListener removeMemberListener) {  
        super();  
        this.mList = list;
        this.mContext = context;  
        inflater=LayoutInflater.from(context);
        mRemoveMemberListener = removeMemberListener;
    }
    
    
	public boolean isDelShow() {
		return delShow;
	}

	public void setIsDelShow(boolean isDelShow) {
		this.delShow = isDelShow;
	}



	@Override
	public int getCount() {

		return mList.size();
	}

	@Override
	public Object getItem(int position) {

		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Holder holder;  
        if(convertView==null){  
        	
            convertView=inflater.inflate(R.layout.item_groupinfo_user, null);  
            holder=new Holder();  
            holder.del = (ImageButton)convertView.findViewById(R.id.ItemDel);
            holder.tv=(TextView) convertView.findViewById(R.id.ItemText);  
            holder.img=(ImageView) convertView.findViewById(R.id.ItemImage);  
            
            convertView.setTag(holder);
              
        }else{
            holder=(Holder) convertView.getTag();
        }
        String tn = (String)mList.get(position).get("text");
        if(tn != null && (tn.equals("jia") || tn.equals("jian"))){
        	holder.tv.setText("");
            holder.img.setImageResource((Integer)mList.get(position).get("img"));	
        }else{
        	holder.tv.setText((String)mList.get(position).get("text"));
        	if(mList.get(position).get("img") == null){
        		holder.img.setImageResource(R.drawable.icon);//默认
        	}else{
        		holder.img.setImageBitmap((Bitmap)mList.get(position).get("img"));	
        	}
            //是否显示删除按钮
            if(isDelShow()){
            	holder.del.setVisibility(ImageButton.VISIBLE);
            	holder.del.setOnClickListener(new View.OnClickListener() {
    				@Override
    				public void onClick(View v) {
    					v.setTag(mList.get(position).get("cid"));
    					mRemoveMemberListener.onClick(v);
    				}
    			});
            }else{
            	holder.del.setVisibility(ImageButton.INVISIBLE);
            }
        }
        
        return convertView;
	}
	
	private class Holder{
        
        TextView tv=null;  
        ImageView img=null;
        ImageButton del=null;
          
    }
	
}
