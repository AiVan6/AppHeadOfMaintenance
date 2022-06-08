package romanow.abc.android.menu;

import okhttp3.MultipartBody;
import firefighter.core.API.RestAPICommon;
import firefighter.core.entity.artifacts.Artifact;
import firefighter.core.utils.FileNameExt;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;
import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;


public class MIUpLoad extends MenuItem {
    public MIUpLoad(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Выгрузка на сервер") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Выгрузка файлов",procViewMultiSelector);
            }
        });
    }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procViewMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            final AppData ctx = AppData.ctx();
            final String token = ctx.loginSettings().getSessionToken();
            for (FileDescription ff : fd){
                final FileDescription ff2 = ff;
                FileNameExt fname = new FileNameExt(ctx.androidFileDirectory(),ff.getOriginalFileName());
                MultipartBody.Part body2 = RestAPICommon.createMultipartBody(fname);
                String name = "Выгружен: "+ctx.loginSettings().getUserPhone();
                //new NetCall<Artifact>().call(main,ctx.getService().upload(token,name,fname.fileName(),body2), new NetBackDefault(){
                //    @Override
                //    public void onSuccess(Object val) {
                //        new NetCall<MeasureFile>().call(main,ctx.getService2().addMeasure(token, ((Artifact) val).getOid()), new NetBackDefault() {
                //            @Override
                //            public void onSuccess(Object val) {
                //                    ctx.popupAndLog(false, "Файл выгружен: "+ff2.toString());
                //                    }
                //                });
                //            }
                //        });
                }
            }
    };
}
