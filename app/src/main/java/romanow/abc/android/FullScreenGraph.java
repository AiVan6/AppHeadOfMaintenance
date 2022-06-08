package romanow.abc.android;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import romanow.abc.android.service.AppData;
import romanow.abc.android.service.BaseActivity;


public class FullScreenGraph extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.graph_gorizontal);
            getSupportActionBar().hide();
            LinearLayout lrr = (LinearLayout) findViewById(R.id.viewPanelHoriz);
            LinearLayout hd = (LinearLayout) findViewById(R.id.viewPanelHead);
            FileDescriptionList fd = AppData.ctx().getFileList();
            setDefferedList(fd);
            LinearLayout graph = createMultiGraph(R.layout.graphviewhoriz,0);
            lrr.addView(graph);
            int ii=0;
            for (FileDescription ff : fd) {
                Button bb = new Button(this);
                bb.setTextColor(getPaintColor(ii++) | 0xFF000000);
                bb.setBackgroundColor(0xFF00574B);
                bb.setTextSize(20);
                bb.setHeight(40);
                bb.setWidth(150);
                bb.setPadding(10,0,0,0);
                bb.setText(ff.getSensor());
                hd.addView(bb);
                procArchive(ff);
                }
            } catch (Exception ee){
                addToLog(createFatalMessage(ee,10));
                }
        }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        }

    @Override
    public void clearLog() {}

    @Override
    public void addToLog(String ss, int textSize) {}

    @Override
    public void addToLogHide(String ss) {}

    @Override
    public void addToLog(boolean fullInfoMes, String ss, int textSize, int textColor) {}

    @Override
    public void popupAndLog(String ss) {}

    @Override
    public void errorMes(String ss) {
        AppData.ctx().errorMes(ss);
        }

    @Override
    public void notify(String s) {

    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void onClose() {

    }
}
