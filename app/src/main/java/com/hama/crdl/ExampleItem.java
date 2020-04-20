package com.hama.crdl;

public class ExampleItem {
    private int mImageResource;
    private String mTextTitle;
    private String mTextProgess;
    private int mIntProgess;

    public ExampleItem(int imageResource, String TextTitle, String TextProgess, int IntProgess) {
        mImageResource = imageResource;
        mTextTitle = TextTitle;
        mTextProgess = TextProgess;
        mIntProgess = IntProgess;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public String getTextTitle() {
        return mTextTitle;
    }

    public String getTextProgess() {
        return mTextProgess;
    }

    public int getmIntProgess() {
        return mIntProgess;
    }
}
