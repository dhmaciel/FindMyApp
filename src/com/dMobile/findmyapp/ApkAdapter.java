package com.dMobile.findmyapp;

import java.util.List;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dMobile.bean.MyApp;

public class ApkAdapter extends BaseAdapter { // implements Filterable

	private List<MyApp> packageList;
	private List<MyApp> filteredData;
	private Activity context;
	private PackageManager packageManager;
	
	public ApkAdapter(MainActivity mainActivity, List<MyApp> packageList,
			PackageManager packageManager) {
		super();
		this.context = mainActivity;
		this.packageList = packageList;
		this.packageManager = packageManager;
	}

	public List<MyApp> getFilteredData() {
		return filteredData;
	}

	@Override
	public int getCount() {
		// TODO Retirar if quando não mostrar todos apps
			if(filteredData == null){
				return packageList.size();
			}
		return filteredData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Retirar if quando não mostrar todos apps
		if(filteredData == null){
			return packageList.get(position);
		}
		return filteredData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {
		TextView text;
		ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MyApp entry = null;
		 // get the selected entry
		if(filteredData == null){
			entry = packageList.get(position);
		}else{
			entry = filteredData.get(position);			
		}
		
		// reference to convertView
        View v = convertView;
 
        // inflate new layout if null
        if(v == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            v = inflater.inflate(R.layout.apklist_item, null);
            holder = new ViewHolder();
            TextView textView = (TextView)v.findViewById(R.id.appname);
            //textView.setVisibility(View.GONE);
            holder.text = textView;
            holder.icon = (ImageView)v.findViewById(R.id.imagemviewAppIcon);
            
            v.setTag(holder);
            //v = inflater.inflate(R.layout.apklist_item, parent,false);
        }else{
        	holder = (ViewHolder) v.getTag();
        }
        
        Drawable d = new BitmapDrawable(BitmapFactory.decodeByteArray(entry.getImage(), 0, entry.getImage().length));
//        d = resizeImageicon(d);
        
        holder.text.setText(entry.getName());
        holder.icon.setImageDrawable(d);

        return v;
	}

}