package com.zacharymitchell.magiclifecounter.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zacharymitchell.magiclifecounter.MainActivity;
import com.zacharymitchell.magiclifecounter.R;

public class ElementPicker extends RelativeLayout {
    ImageView redView;
    ImageView greenView;
    ImageView whiteView;
    ImageView blueView;
    ImageView blackView;
    ImageView poisonView;
    ImageView dotView;
    Button redButton;
    Button greenButton;
    Button whiteButton;
    Button blueButton;
    Button blackButton;
    Button poisonButton;

    ElementPickListener elementPickListener;

    public enum Elements{
        Red,
        Green,
        White,
        Blue,
        Black,
        Poison
    }

    public ElementPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        addView(inflate(context,R.layout.layout_element_picker,null));
        redView = (ImageView) findViewById(R.id.red_circle_view);
        greenView = (ImageView) findViewById(R.id.green_circle_view);
        whiteView =(ImageView) findViewById(R.id.white_circle_view);
        blueView = (ImageView) findViewById(R.id.blue_circle_view);
        blackView = (ImageView) findViewById(R.id.black_circle_view);
        poisonView = (ImageView) findViewById(R.id.poison_circle_view);

        dotView = (ImageView) findViewById(R.id.anim_dice_dot_view);

        redButton = (Button) findViewById(R.id.red_circle_button);
        greenButton = (Button) findViewById(R.id.green_circle_button);
        whiteButton = (Button) findViewById(R.id.white_circle_button);
        blueButton = (Button)findViewById(R.id.blue_circle_button);
        blackButton = (Button) findViewById(R.id.black_circle_button);
        poisonButton = (Button) findViewById(R.id.poison_circle_button);
        setOnClickListeners();
    }

    public void setElementPickListener(ElementPickListener listener)
    {
        elementPickListener = listener;
    }
    private void setOnClickListeners()
    {
        redButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.Red);
                }
            }
        });
        greenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.Green);
                }
            }
        });
        whiteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.White);

                }
            }
        });
        blueButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.Blue);
                }
            }
        });
        blackButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.Black);
                }
            }
        });
        poisonButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(elementPickListener!=null)
                {
                    elementPickListener.onElementPick(Elements.Poison);
                }
            }
        });
    }

    public interface ElementPickListener {
    void onElementPick(Elements element);
    }
}
