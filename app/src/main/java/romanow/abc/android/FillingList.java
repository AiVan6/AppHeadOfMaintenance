package romanow.abc.android;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class FillingList {

    MainActivity mainActivity;

    public FillingList(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public TextView textViewCreate(LinearLayout gridLayout, String str) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate( R.layout.item_text_view,null);
        TextView textView = (TextView) layout.findViewById(R.id.textViewName);
        textView.setText(str);
        if(textView != null) {
            ((ViewGroup)textView.getParent()).removeView(textView);
        }

        gridLayout.addView(textView);
        return textView;
    }

    public Spinner spinnerCreate (LinearLayout gridLayout) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate( R.layout.item_spiner_text,null);
        Spinner spinner = layout.findViewById(R.id.SpinnerPanel);
        if(spinner != null) {
            ((ViewGroup)spinner.getParent()).removeView(spinner);
        }
//        if(union) {
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//            layoutParams.columnSpec = GridLayout.spec(1, 3);
//            gridLayout.addView(spinner, layoutParams);
//        }else
        gridLayout.addView(spinner);
        return spinner;
    }

//    public Spinner longSpinnerCreate (LinearLayout gridLayout) {
//        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate( R.layout.spinner_view,null);
//        Spinner spinner = layout.findViewById(R.id.SpinnerPanel);
//        if(spinner != null) {
//            ((ViewGroup)spinner.getParent()).removeView(spinner);
//        }
//
////        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
////        layoutParams.columnSpec = GridLayout.spec(0, 2);
////        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
////        gridLayout.addView(spinner, layoutParams);
//        //}else
//        //gridLayout.addView(spinner);
//        return spinner;
//    }

    public EditText editTextCreate (LinearLayout gridLayout) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.item_edit_text,null);
        EditText editText = layout.findViewById(R.id.EditTextPanel);

        if(editText != null) {
            ((ViewGroup)editText.getParent()).removeView(editText);
        }

        gridLayout.addView(editText);
        return editText;
    }

    public LinearLayout createListBox(String name){
        LinearLayout xx=(LinearLayout)mainActivity.getLayoutInflater().inflate(R.layout.settings_item_list, null);
        xx.setPadding(5, 5, 5, 5);
        final TextView tt=(TextView) xx.findViewById(R.id.dialog_settings_value);
        TextView img=(TextView)xx.findViewById(R.id.dialog_settings_name);
        img.setText(name);
        img.setClickable(true);
        return xx;
    }


    public void fillingListBox(TextView text, TextView img, final ArrayList<String> values, int idx, final I_ListBoxListener lsn) {
        if(!values.isEmpty())
            text.setText(""+values.get(idx));
//        else
//            text.setText("---");

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ListBoxDialog(mainActivity,values,"Закрыть", new I_ListBoxListener(){
                    @Override
                    public void onSelect(int index) {
                        lsn.onSelect(index);
                        text.setText(""+values.get(index));
                    }
                    @Override
                    public void onLongSelect(int index) {}
                    @Override
                    public void onCancel() {}
                }).create();
            }
        });
    }

    public LinearLayout twoButtonCreate (LinearLayout gridLayout,String nameLeft,String nameRight,Button left,Button right) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.tow_button,null);
        left = layout.findViewById(R.id.leftButton);
        if(left != null) {
            ((ViewGroup)left.getParent()).removeView(left);
        }

        right = layout.findViewById(R.id.rightButton);
        if(right != null) {
            ((ViewGroup)right.getParent()).removeView(right);
        }
        left.setText(nameLeft);
        right.setText(nameRight);
        layout.addView(left);
        layout.addView(right);
        return layout;
        //gridLayout.addView(layout);
    }

    public LinearLayout imageButtonCreate (int drawable, String nameRight, ImageView left, Button right) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.image_button,null);
        left = layout.findViewById(R.id.leftImage);
        if(left != null) {
            ((ViewGroup)left.getParent()).removeView(left);
        }

        right = layout.findViewById(R.id.rightButton);
        if(right != null) {
            ((ViewGroup)right.getParent()).removeView(right);
        }
        left.setImageResource(drawable);
        //left.setText(nameLeft);
        right.setText(nameRight);
        layout.addView(left);
        layout.addView(right);
        return layout;
        //gridLayout.addView(layout);
    }

    public Button buttonCreate (LinearLayout gridLayout,String name,boolean union) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.maintenance_button,null);
        Button button = layout.findViewById(R.id.button_press);
        if(button != null) {
            ((ViewGroup)button.getParent()).removeView(button);
        }
        button.setText(name);

            gridLayout.addView(button);
        return button;
    }

    public ImageView imageButtonCreate (LinearLayout gridLayout) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.image_view,null);
        ImageView img = layout.findViewById(R.id.imageView);
        if(img != null) {
            ((ViewGroup)img.getParent()).removeView(img);
        }

        gridLayout.addView(img);
        return img;
    }

    public ImageView imageCreate (LinearLayout gridLayout) {
        LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.image_view_photo,null);
        ImageView img = layout.findViewById(R.id.imageView);
        if(img != null) {
            ((ViewGroup)img.getParent()).removeView(img);
        }

        gridLayout.addView(img);
        return img;
    }

}
