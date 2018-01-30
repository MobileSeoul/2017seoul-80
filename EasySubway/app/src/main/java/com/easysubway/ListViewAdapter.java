package com.easysubway;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {
    Context context;
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<>();

    public ListViewAdapter(){}

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();

        // 커스텀 리스트 뷰에 ImageView 와 TextView 를 설정한 ListViewItem 을 Add
        if (convertView == null) {
            LayoutInflater inflater
                    = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        TextView textView        = (TextView)convertView.findViewById(R.id.subwaytitle);

        ListViewItem listViewItem = listViewItemList.get(position);

        textView.setText(listViewItem.getTitle());

        return convertView;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int position) { return listViewItemList.get(position); }

    // ListViewItem 을 생성해 Adapter 에 Add
    public void addItem(String title) {
        ListViewItem item = new ListViewItem();

        item.setTitle(title);

        listViewItemList.add(item);
    }
}

