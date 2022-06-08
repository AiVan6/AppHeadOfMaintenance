package romanow.abc.android;

import android.app.Activity;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import firefighter.core.I_SelectObject;
import firefighter.core.constants.Values;
import firefighter.core.entity.artifacts.Artifact;
import romanow.abc.android.service.AppData;

public class WorkingFiles {

    private LoginSettings settings;
    private AppData ctx;
    private Activity activity;

    public WorkingFiles(Activity activity) {
        this.activity = activity;
        ctx = AppData.ctx();
        settings = ctx.loginSettings();
    }

    public final String androidFileDirectory(){
        String path;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            path = activity.getApplicationContext().getExternalFilesDir(null).getAbsolutePath();
        else
            path = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/firefighter";
        System.out.println("path:"+path);
        return path;
    };

    public String testDataDirectoty(){
        String outdir = androidFileDirectory();
        //String outdir= "romanow.abs.android/"+artifactVoice.directoryName();
        //String outdir= "app/src/main/java/romanow/abc/android/"+artifactVoice.directoryName();
        File saveDir = new File(outdir);

        try {
            if(saveDir.createNewFile()){
                System.out.println(outdir + " Файл создан");
            } else {
                System.out.println("Файл " + outdir + " уже существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!saveDir.exists()){
            saveDir.mkdirs();
        }
        System.out.println("dirs:"+saveDir.getAbsoluteFile());

        //fileCreate(artifactVoice);

        return outdir;
    }


    public void startLoadByUrl(Artifact art, String temp, I_SelectObject back){
        final String fname = testDataDirectoty()+"/"+temp;
        //final String fname = "file/3_Audio" +"/"+ art.createArtifactFileName();

        System.out.println("fname1:"+fname);
        //String fname = artifactVoice.createArtifactFileName();


        File ff = new File(fname);
        if (ff.exists()){
            back.onSelect(fname);
            return;
        }
        System.out.println("Start");
        new Thread() {
            public void run() {
                try {
                    System.out.println("continue");
                    URL url2 = new URL("http://"+settings.getDataSetverIP()+":"+settings.getDataServerPort()+"/file/"+art.createArtifactServerPath());
                    System.out.println("url:"+url2);
                    HttpURLConnection connection;
                    connection= (HttpURLConnection) url2.openConnection();
                    connection.setReadTimeout(10000);
                    connection.getResponseCode();
                    InputStream in = connection.getInputStream();
                    System.out.println("111111111");
                    final FileOutputStream out = new FileOutputStream(fname);
                    System.out.println("2222222222222");
                    int sz = Values.FileBufferSize*4;
                    int fileSize=0;
                    int idx=0;
                    byte bb[] = new byte[sz];
                    boolean fin=false;
                    while (true) {
                        int cc = in.read();
                        if (cc == -1){
                            fin = true;
                        }
                        else{
                            bb[idx++] = (byte) cc;
                            if (idx!=sz)
                                continue;
                        }
                        out.write(bb, 0,  idx);
                        fileSize+=idx;
                        idx=0;
                        if (fin)
                            break;
                    }
                    in.close();
                    out.flush();
                    out.close();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    back.onSelect(fname);
//                }
//            });
                } catch (Exception ee) {
//            addBugMessage(Utils.createFatalMessage(ee));
//            popupToast("Ошибка загрузки файла");
                    System.out.println("Error:"+ee);
                }
            }
        }.start();
    }

}
