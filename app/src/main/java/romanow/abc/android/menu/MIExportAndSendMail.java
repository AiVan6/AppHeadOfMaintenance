package romanow.abc.android.menu;

import android.content.Intent;
import android.net.Uri;

import java.io.FileInputStream;
import java.util.ArrayList;

import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;

public class MIExportAndSendMail extends MenuItem {
    public MIExportAndSendMail(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Отправить Excel в mail") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Отправить Excel в mail",exportAndSendMailSelector);
            }
        });
    }
    //--------------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector exportAndSendMailSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fdlist, boolean longClick) {
            for(FileDescription fd : fdlist) {
                try {
                    String pathName = AppData.ctx().androidFileDirectory() + "/" + fd.getOriginalFileName();
                    FileInputStream fis = new FileInputStream(pathName);
                    } catch (Throwable e) {
                        main.errorMes("Файл не открыт: " + fd.getOriginalFileName() + "\n" + main.createFatalMessage(e, 10));
                        }
                }
            try {
                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                emailIntent.setType("plain/text");
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{AppData.ctx().loginSettings().getMailToSend()});
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Звенящие опоры России");
                emailIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                ArrayList<Uri> uris = new ArrayList<Uri>();
                for(FileDescription fd : fdlist){
                    emailIntent.putExtra(Intent.EXTRA_TEXT, "Датчик: " + fd.toString());
                    }
                emailIntent.putExtra(Intent.EXTRA_STREAM,uris);
                main.startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
                //----------------- Читстить каталог после отправки
                //for(FileDescription fd : fdlist){
                //    String filePath = new FFTExcelAdapter(main, "", fd).createOriginalExcelFileName();
                //    File ff = new File(filePath);
                //    ff.delete();
                //    }
                } catch (Exception ee){
                    main.errorMes("Ошибка mail: "+ee.toString());
                    }
        }
    };

}
