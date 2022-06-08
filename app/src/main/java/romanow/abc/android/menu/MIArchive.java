package romanow.abc.android.menu;


import static romanow.abc.android.MainActivity.ViewProcHigh;

import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;
import romanow.abc.android.R;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;

public class MIArchive extends MenuItem {
    public MIArchive(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Архив") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Проcмотр архива",procViewMultiSelector);
            }
        });
    }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector procViewMultiSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList fd, boolean longClick) {
            main.log().addView(main.createMultiGraph(R.layout.graphview,ViewProcHigh));
            for (FileDescription ff : fd){
                main.procArchive(ff,false);
                }
            }
    };
}
