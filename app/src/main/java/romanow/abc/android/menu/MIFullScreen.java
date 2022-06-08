package romanow.abc.android.menu;

import android.content.Intent;

import romanow.abc.android.FullScreenGraph;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;
import romanow.abc.android.FileDescriptionList;


public class MIFullScreen extends MenuItem {
    public MIFullScreen(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Полный экран") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Полный экран",procViewSelectorFull);
            }
        });
    }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procViewSelectorFull = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            Intent intent = new Intent();
            intent.setClass(main.getApplicationContext(), FullScreenGraph.class);
            AppData.ctx().setFileList(fd);
            main.startActivity(intent);
        }
    };

}
