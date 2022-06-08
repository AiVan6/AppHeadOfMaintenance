package romanow.abc.android.menu;

import java.io.File;

import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;

public class MIDeleteFromArchive extends MenuItem {
    public MIDeleteFromArchive(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Удалить из архива") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Удалить из архива",deleteMultiSelector);
            }
        });
    }
    //-----------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector deleteMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            for (FileDescription ff : fd){
                File file = new File(AppData.ctx().androidFileDirectory()+"/"+ff.getOriginalFileName());
                file.delete();
                }
            }
    };
}
