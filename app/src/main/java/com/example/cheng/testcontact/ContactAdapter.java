package com.example.cheng.testcontact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class ContactAdapter extends BaseAdapter{

    private Context context;
    private LayoutInflater inflater;
    private List<ContactInfo> lists;

    public ContactAdapter(Context context, List<ContactInfo> lists){
        this.context = context;
        this.lists = lists;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_lv_contact, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(lists.get(position).UserHead!=null){
            holder.contact_iv_head.setImageBitmap(lists.get(position).UserHead);
        }else{
            holder.contact_iv_head.setImageResource(R.mipmap.ic_launcher);
        }
        holder.contact_tv_name.setText(lists.get(position).UserName + " " + lists.get(position).PhoneMobile.replace(" ", ""));
        return convertView;
    }

    class ViewHolder{

        private ImageView contact_iv_head;
        private TextView contact_tv_name;

        public ViewHolder(View v){
            contact_iv_head = v.findViewById(R.id.contact_iv_head);
            contact_tv_name = v.findViewById(R.id.contact_tv_name);
        }
    }

}
