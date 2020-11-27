package com.example.osrstracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class SkillView extends ConstraintLayout {

    public SkillView(Context context) {
        super(context);
        init(null);
    }

    public SkillView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SkillView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {
        LayoutInflater inflater = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.skill_view, this);
        //TODO:set Buttons
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //TODO
        // Sets the images for the previous and next buttons. Uses
        // built-in images so you don't need to add images, but in
        // a real application your images should be in the
        // application package so they are always available.
        ImageView image = this.findViewById(R.id.imageViewSkill);
        image.setImageResource(android.R.drawable.star_big_on);

    }


}

