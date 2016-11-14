package com.zacharymitchell.magiclifecounter;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.zacharymitchell.magiclifecounter.R;

public class TrashDialog extends DialogFragment implements View.OnClickListener {
    Button delete, cancel;
    Commincator commincator;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        commincator = (Commincator) activity;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);

        View view = inflater.inflate(R.layout.trash_dialog, null);
        delete = (Button) view.findViewById(R.id.dialogDelete);
        cancel = (Button) view.findViewById(R.id.dialogCancel);
        delete.setOnClickListener(this);
        cancel.setOnClickListener(this);



        return view;
    }

    @Override
    public void onClick(View view) {
        if(view.getId()== R.id.dialogDelete) {


            dismiss();
            commincator.onDialogMessage("Deleted");
//            Toast.makeText(getActivity(),"Deleted",Toast.LENGTH_LONG).show();

        } else {
            dismiss();
            commincator.onDialogMessage("Cancelled");
//            Toast.makeText(getActivity(),"Cancelled",Toast.LENGTH_LONG).show();

        }
    }
    interface Commincator {
        public void onDialogMessage(String message);
    }
}
