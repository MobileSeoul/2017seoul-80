package com.easysubway;

public class ListViewItem {
    private String title;



    public ListViewItem(){}

    public void setTitle(String text) {

        title = text;

        // 화면에 출력할 tag (한글), Id 와 같은 역할을 하는 title(영어)

        /*
        title = text;
        int resId = MainActivity.mContext.getResources()
                .getIdentifier(text+"kr", "string", MainActivity.mContext.getPackageName());

        tag = MainActivity.mContext.getResources().getString(resId);
        */



    }

    public String getTitle() { return this.title; }

}

