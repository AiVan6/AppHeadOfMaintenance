package romanow.abc.android.menu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import romanow.abc.android.I_ArchiveMultiSelector2;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;
import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;


public class MIFileCopy extends MIFileBrowser {
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector2 procFileCopy = new I_ArchiveMultiSelector2() {
        @Override
        public void onSelect(String path, FileDescriptionList fd, boolean longClick) {
            for (FileDescription ff : fd){
                try{
                    final InputStream is = new FileInputStream(path+"/"+ff.getOriginalFileName());
                    if (is==null){
                        main.errorMes("Файл "+ff.getOriginalFileName()+" не найден");
                        continue;
                        }
                    File gg = new File(AppData.ctx().androidFileDirectory());
                    if (!gg.exists()) {
                        gg.mkdir();
                        }
                    final FileOutputStream fos = new FileOutputStream(AppData.ctx().androidFileDirectory()+"/"+ff.getOriginalFileName());
                    final String zz = ff.getOriginalFileName();
                    Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                int vv = is.read();
                                if (vv == -1)
                                    break;
                                fos.write(vv);
                                }
                            fos.flush();
                            fos.close();
                            is.close();
                            main.addToLog("Файл "+zz+" скопирован");
                            } catch (final Exception ee) {
                                main.errorMes("Ошибка копирования: "+ee.toString());
                                }
                        }});
                    thread.start();
                    } catch (Throwable ee){
                        main.errorMes(ff.getOriginalFileName()+"\n"+ee.toString());
                        }
                    }
            }
        };
    public MIFileCopy(MainActivity main0) {
        super(main0,"Файлы в архив");
        setProcSelector(procFileCopy);
        }
}
