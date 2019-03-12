package com.example.avery.chamberofwizards.Questions;

public class SortBySpinnerItem {
    private String mCategoryName;
    private int mFlagImage;

    public SortBySpinnerItem(String mCategoryName, int mFlagImage) {
        this.mCategoryName = mCategoryName;
        this.mFlagImage = mFlagImage;
    }

    public String getmCategoryName() {
        return mCategoryName;
    }

    public int getmFlagImage() {
        return mFlagImage;
    }
}
