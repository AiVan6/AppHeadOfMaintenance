package romanow.abc.android.service;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import firefighter.core.I_Notify;
import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;
import romanow.abc.android.I_FDComparator;
import romanow.abc.android.R;

import firefighter.core.Utils;
import romanow.abc.android.ListBoxDialog;
import romanow.abc.android.MainActivity;


public abstract class BaseActivity extends AppCompatActivity implements I_Notify {
    //------------------------------------------------------------------------------
    public final static int EmoSet = 0x1F6E0;
    public final static int EmoErr = 0x1F4A3;
    public Thread guiThread;
    private AppData ctx;
    private LineGraphView multiGraph=null;
    private boolean fullInfo=false;             // Вывод полной информации о спектре
    public final static int greatTextSize=20;   // Крупный шрифт
    public final static int middleTextSize=16;
    public final static int smallTextSize=12;
    private final static int paintColors[]={0x00007000,0x000000FF,0x00A00000,0x000070C0,0x00C000C0,0x00206060};
    public abstract void clearLog();
    public abstract void addToLog(String ss, int textSize);
    public abstract void addToLogHide(String ss);
    public abstract void addToLog(boolean fullInfoMes, final String ss, final int textSize, final int textColor);
    public abstract void popupAndLog(String ss);
    public abstract void errorMes(String ss);
    public void addToLog(String ss){
        addToLog(ss,0);
        }
    protected ListBoxDialog menuDialog=null;
    private FileDescriptionList defferedList = null;    // Описатели файлов (список) для deffered
    //--------------------------------------------------------------------------
    public BufferedReader openReader(String fname) throws IOException {
        FileInputStream fis = new FileInputStream(ctx.androidFileDirectory()+"/"+fname);
        return new BufferedReader(new InputStreamReader(fis, "Windows-1251"));
        }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        guiThread = Thread.currentThread();
        ctx = AppData.ctx();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull  Thread t, @NonNull  Throwable e) {
                if (menuDialog!=null)
                    menuDialog.cancel();
                String ss = Utils.createFatalMessage(e);
                ctx.addBugMessage(ss);
                ctx.toLog(true,ss);
                //bigPopup("Фатальная ошибка",ss);
                //-------------- Перезапуск с сообщением о сбое ----------------------------
                ctx.loginSettings().setFatalMessage("Фатальная ошибка\n"+ss);
                saveContext();
                overLoad(true);
                }
            });
        }

    @Override
    protected void onStop() {
        super.onStop();
        ctx.setCanSendPopup(false);
        unregisterReceiver(receiver);
        }
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int drawId = intent.getIntExtra("drawId",0);
            String mes = intent.getStringExtra("mes");
            boolean toLog = intent.getBooleanExtra("toLog",false);
            boolean popup = intent.getBooleanExtra("popup",true);
            boolean error = intent.getBooleanExtra("error",false);
            if (toLog){
                if (error)
                    errorMes(mes);
                else
                    addToLog(mes);
                }
            if (popup)
                popupToast(drawId,mes);
            }
        };
    @Override public void onStart(){
        super.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_Popup);
        this.registerReceiver(receiver, filter);
        ctx.setCanSendPopup(true);
        }
    public int getPaintColor(int idx){
        if (idx < paintColors.length)
            return paintColors[idx];
        idx -= paintColors.length;
        int color = 0x00808080;
        while(idx--!=0 && color!=0)
            color-=0x00202020;
        return color;
        }
    public void paintOne(double data[], int color){
        paintOne(multiGraph,0,data,color,0,0,false);
        }
    public void paintOne(LineGraphView graphView, double freq, double data[], int color, int noFirst, int noLast, boolean freqMode){
        GraphView.GraphViewData zz[] = new GraphView.GraphViewData[data.length-noFirst-noLast];
        for(int j=noFirst;j<data.length-noLast;j++){                    // Подпись значений факторов j-ой ячейки
            double cfreq = freqMode ? (j*freq/data.length/2) : (j/100.);
            zz[j-noFirst] = new GraphView.GraphViewData(cfreq,data[j]);
            }
        GraphViewSeries series = new GraphViewSeries(zz);
        series.getStyle().color = color | 0xFF000000;
        graphView.addSeries(series);
        }
    public LinearLayout createMultiGraph(int resId,double procHigh){
        LinearLayout lrr=(LinearLayout)getLayoutInflater().inflate(resId, null);
        LinearLayout panel = (LinearLayout)lrr.findViewById(R.id.viewPanel);
        if (procHigh!=0){
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)panel.getLayoutParams();
            params.height = (int)(getResources().getDisplayMetrics().widthPixels*procHigh);
            panel.setLayoutParams(params);
            }
        multiGraph = new LineGraphView(this,"");
        multiGraph.setScalable(true);
        multiGraph.setScrollable(true);
        multiGraph.getGraphViewStyle().setTextSize(15);
        panel.addView(multiGraph);
        return lrr;
        }
    public LineGraphView getMultiGraph() {
        return multiGraph;
        }
    //-----------------------------------------------------------------------------------------------------
    public void procArchive(FileDescription fd){
        String fname = fd.getOriginalFileName();
        try {
            FileInputStream fis = new FileInputStream(ctx.androidFileDirectory()+"/"+fname);
           } catch (Throwable e) {
                errorMes("Файл не открыт: "+fname+"\n"+createFatalMessage(e,10));
                }
        }
    public static String createFatalMessage(Throwable ee, int stackSize) {
        String ss = ee.toString() + "\n";
        StackTraceElement dd[] = ee.getStackTrace();
        for (int i = 0; i < dd.length && i < stackSize; i++) {
            ss += dd[i].getClassName() + "." + dd[i].getMethodName() + ":" + dd[i].getLineNumber() + "\n";
            }
        String out = "Программная ошибка:\n" + ss;
        return out;
        }
   //--------------------------------------------------------------------------------------------------------
    public void bigPopup(String title, String text){
        AlertDialog.Builder messageBox = new AlertDialog.Builder(ctx.getContext());
        messageBox.setTitle(title);
        messageBox.setMessage(text);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        messageBox.show();
        }
    public void popupToast(int viewId, String ss) {
        Toast toast3 = Toast.makeText(getApplicationContext(), ss, Toast.LENGTH_LONG);
        LinearLayout toastContainer = (LinearLayout) toast3.getView();
        if (toastContainer==null){
            toast3.show();
            return;
            }
        ImageView catImageView = new ImageView(getApplicationContext());
        TextView txt = (TextView)toastContainer.getChildAt(0);
        txt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        txt.setGravity(Gravity.CENTER);
        catImageView.setImageResource(viewId);
        toastContainer.addView(catImageView, 0);
        toastContainer.setOrientation(LinearLayout.HORIZONTAL);
        toastContainer.setGravity(Gravity.CENTER);
        toastContainer.setVerticalGravity(5);
        //toastContainer.setBackgroundResource(R.color.status_almostFree);
        toast3.setGravity(Gravity.TOP, 0, 200);
        toast3.show();
        }
    public void popupInfo(final String ss) {
        guiCall(new Runnable() {
            @Override
            public void run() {
                popupToast(R.drawable.info,ss);
            }
        });
        }
    public void guiCall(Runnable code){
        if (Thread.currentThread()==guiThread)
            code.run();
        else
            runOnUiThread(code);
        }
    public boolean testGuiThread(int point){
        boolean bb = Thread.currentThread()==guiThread;
        addToLog((bb ? "" : "Не ")+"поток GUI: "+point);
        return bb;
        }
    public void onMessage(String mes){
        addToLog( mes);
        }
    public void onError(Exception ee){
        errorMes(ee.toString());
        }
    public boolean isFullInfo() {
        return fullInfo; }
    public void setFullInfo(boolean fullInfo) {
        this.fullInfo = fullInfo; }
    public FileDescriptionList getDefferedList() {
        return defferedList; }
    public void setDefferedList(FileDescriptionList defferedList) {
        this.defferedList = defferedList; }
    //----------------------------------------------------------------------------------------------
    public FileDescriptionList createArchive(){
        return createArchive(null);
        }
    public FileDescriptionList createArchive(String subdir){
        String path = ctx.androidFileDirectory()+(subdir!=null ? "/"+subdir : "");
        return createArchivePath(path,false);
        }
    public FileDescriptionList createArchivePath(String path, boolean trace){
        File ff = new File(path);
        if (!ff.exists()) {
            ff.mkdir();
            }
        FileDescriptionList out = new FileDescriptionList();
        String vv[] = ff.list();
        if (vv==null)
            return out;
        for(String ss : vv){
            File file = new File(ctx.androidFileDirectory()+"/"+ss);
            if (file.isDirectory())
                continue;
            FileDescription dd = new FileDescription(ss);
            if (dd.getFormatError().length()!=0)
                continue;
            String zz = dd.validDescription();
            if (zz.length()!=0){
                if (trace)
                    addToLog("Файл: "+ss+"\n"+zz);
                }
            out.add(dd);
            }
        out.sort(new I_FDComparator() {
            @Override
            public int compare(FileDescription o2, FileDescription o1) {
                return (int)(o2.getCreateDate().timeInMS() - o1.getCreateDate().timeInMS());
                }
            });
        return out;
        }
    public FileDescriptionList createDirArchive(){
        return createDirArchive(ctx.androidFileDirectory());
        }
    public FileDescriptionList createDirArchive(String path){
        File ff = new File(path);
        if (!ff.exists()) {
            ff.mkdir();
            }
        FileDescriptionList out = new FileDescriptionList();
        String zz[] = ff.list();
        if (zz==null)
            return out;
        for(String ss : zz){
            if (AppData.SubDirList.get(ss)!=null)
                continue;
            File file = new File(path+"/"+ss);
            if (file.isDirectory())
                out.add(new FileDescription(ss));
            }
        out.sort(new I_FDComparator() {
            @Override
            public int compare(FileDescription o2, FileDescription o1) {
                return o1.getOriginalFileName().compareTo(o2.getOriginalFileName());
                }
            });
        return out;
        }

    public void saveContext(){
        ctx.fileService.saveContext();
        }
    public void loadContext(){
        ctx.fileService.loadContext();
        }
    public void overLoad(boolean kill){
        //------------------------------- перезагрузка -------------------------
        Context context = getApplicationContext();
        Intent mStartActivity = new Intent(context, MainActivity.class);
        int mPendingIntentId = 123456;
        PendingIntent mPendingIntent = PendingIntent.getActivity(context, mPendingIntentId,mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager mgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, mPendingIntent);
        if (kill)
            android.os.Process.killProcess(android.os.Process.myPid());
        else
            finish();
        //-----------------------------------------------------------------------
        }

}
