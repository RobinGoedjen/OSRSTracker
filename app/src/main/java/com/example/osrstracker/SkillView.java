package com.example.osrstracker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
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
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void fillView(@DrawableRes int resID, int currentLevel) {
        ImageView image = this.findViewById(R.id.imageViewSkill);
        image.setImageResource(resID);
        TextView text = this.findViewById(R.id.textViewLevel);
        text.setText("LV: " + currentLevel);
        //TODO:set Buttons
    }

}

