package romanow.abc.android.menu;

import java.io.FileInputStream;

import romanow.abc.android.FileDescription;
import romanow.abc.android.FileDescriptionList;
import romanow.abc.android.I_ArchiveMultiSelector;
import romanow.abc.android.MainActivity;
import romanow.abc.android.service.AppData;

public class MIExport extends MenuItem {
    public MIExport(MainActivity main0) {
        super(main0);
        main.addMenuList(new MenuItemAction("Экспорт в Excel") {
            @Override
            public void onSelect() {
                main.selectMultiFromArchive("Экспорт в Excel",exportSelector);
                }
            });
        }
    //------------------------------------------------------------------------------------
    private I_ArchiveMultiSelector exportSelector = new I_ArchiveMultiSelector() {
        @Override
        public void onSelect(FileDescriptionList flist, boolean longClick) {
            for(FileDescription fd : flist) {
                try {
                    String pathName = AppData.ctx().androidFileDirectory() + "/" + fd.getOriginalFileName();
                    FileInputStream fis = new FileInputStream(pathName);
                    } catch (Throwable e) {
                        main.errorMes("Файл не открыт: " + fd.getOriginalFileName() + "\n" + main.createFatalMessage(e, 10));
                        }
                }
            }
        };
    }

