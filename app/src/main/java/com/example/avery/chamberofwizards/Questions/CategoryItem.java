package com.example.avery.chamberofwizards.Questions;

public class CategoryItem {
    private String mCategoryName;
    private int mFlagImage;

    public CategoryItem(String mCategoryName, int mFlagImage) {
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

//Ito ang magho-hold ng mga values ng mga nasa spinner.