package romanow.abc.android.menu;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.Base64Coder;

public class MIAbout extends MenuItem {
    public MIAbout(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("О программе") {
            @Override
            public void onSelect() {
                String title ="Мобильный клиент \"СМУ СНЭЭ\", версия "+ AppData.apkVersion+"\n";
                title+="Разработчик: НГТУ\n";
                title+="Контакты: romanov@corp.nstu.ru\n";
                title+=calcOwnMD5();
                main.addToLog(title,16);
            }
        });
    }
    //--------------------------------------------------------------------------------------
    private String calcOwnMD5(){
        try {
            final PackageManager pm = main.getPackageManager();
            PackageInfo info = pm.getPackageInfo("romanow.abc.android",PackageManager.GET_META_DATA);
            File ff = new File(info.applicationInfo.sourceDir);
            long apkSize = ff.length();
            InputStream is = new FileInputStream(ff);
            byte apk[] = new byte[(int)apkSize];
            is.read(apk);
            is.close();
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(apk);
            byte[] digest = m.digest();
            String base64 = Base64Coder.encodeLines(digest);
            return "MD5(Base64):"+base64;
            //BigInteger bigInt = new BigInteger(1,digest);
            //String hashtext = bigInt.toString(16);
            //while(hashtext.length() < 32 ){
            //    hashtext = "0"+hashtext;
            //    }
            //return "MD5:"+hashtext;
        } catch (Exception ee){
            return "Ошибка подсчета MD5:"+ee.toString();
        }
    }

}
