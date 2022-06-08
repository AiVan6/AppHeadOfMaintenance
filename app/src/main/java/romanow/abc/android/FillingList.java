package romanow.abc.android;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

public class FillingList {

    MainActivity mainActivity;

    public FillingList(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public TextView textViewCreate(GridLayout gridLayout, String str) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate( R.layout.item_spiner_text,null);
        TextView textView = (TextView) layout.findViewById(R.id.TextViewPanel);
        textView.setText(str);
        if(textView != null) {
            ((ViewGroup)textView.getParent()).removeView(textView);
        }

        gridLayout.addView(textView);
        return textView;
    }

    public Spinner spinnerCreate (GridLayout gridLayout) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate( R.layout.item_spiner_text,null);
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

    public Spinner longSpinnerCreate (GridLayout gridLayout) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate( R.layout.spinner_view,null);
        Spinner spinner = layout.findViewById(R.id.SpinnerPanel);
        if(spinner != null) {
            ((ViewGroup)spinner.getParent()).removeView(spinner);
        }

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.columnSpec = GridLayout.spec(0, 2);
        layoutParams.width = GridLayout.LayoutParams.MATCH_PARENT;
        gridLayout.addView(spinner, layoutParams);
        //}else
        //gridLayout.addView(spinner);
        return spinner;
    }

    public EditText editTextCreate (GridLayout gridLayout) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate(R.layout.item_edit_text,null);
        EditText editText = layout.findViewById(R.id.EditTextPanel);

        if(editText != null) {
            ((ViewGroup)editText.getParent()).removeView(editText);
        }
//        if(union) {
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//            layoutParams.columnSpec = GridLayout.spec(1, 3);
//            gridLayout.addView(editText, layoutParams);
//        }else
        gridLayout.addView(editText);
        return editText;
    }

    public Button buttonCreate (GridLayout gridLayout,String name,boolean union) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate(R.layout.maintenance_button,null);
        Button button = layout.findViewById(R.id.button_press);
        if(button != null) {
            ((ViewGroup)button.getParent()).removeView(button);
        }
        button.setText(name);

//        if(union){
//            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
//            layoutParams.columnSpec = GridLayout.spec(0,2);
//            gridLayout.addView(button,layoutParams);
//        }else
            gridLayout.addView(button);
        return button;
    }

    public ImageView imageButtonCreate (GridLayout gridLayout) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate(R.layout.image_view,null);
        ImageView img = layout.findViewById(R.id.imageView);
        if(img != null) {
            ((ViewGroup)img.getParent()).removeView(img);
        }

        gridLayout.addView(img);
        return img;
    }

    public ImageView imageCreate (GridLayout gridLayout) {
        GridLayout layout = (GridLayout) mainActivity.getLayoutInflater().inflate(R.layout.image_view_photo,null);
        ImageView img = layout.findViewById(R.id.imageView);
        if(img != null) {
            ((ViewGroup)img.getParent()).removeView(img);
        }

        gridLayout.addView(img);
        return img;
    }

}
