package romanow.abc.android.menu;

import romanow.abc.android.MainActivity;
import romanow.abc.android.MapFragment;
import romanow.abc.android.R;
import romanow.abc.android.service.AppData;


public class MIMap extends MenuItem {
    private AppData ctx;
    MapFragment mapFragment;
    public MIMap(MainActivity main0) {
        super(main0);
        ctx = AppData.ctx();
        mapFragment = new MapFragment();
        main.addMenuList(new MenuItemAction("Показать на карте") {
            @Override
            public void onSelect() {
//                Intent intent = new Intent();
//                intent.setClass(main.getApplicationContext(), MapActivity340.class);
//                main.startActivity(intent);

                main.fragmentTransaction = main.getSupportFragmentManager().beginTransaction();
                main.fragmentTransaction.replace(R.id.mainLayoutActivity, mapFragment);
                //main.fragmentTransaction.addToBackStack(null);
                main.fragmentTransaction.commit();

//                new NetCall<FacilityList>().call(main,ctx.getService().getFacilityList(
//                        ctx.loginSettings().getSessionToken(), Values.GetAllModeActual,2), new NetBackDefault(){
//                    @Override
//                    public void onSuccess(Object val) {
//                        System.out.println(val);
//                        for(Facility facility : (FacilityList)val){
//                            if (facility.getGPS().gpsValid())
//                                ctx.sendGPS(facility.getGPS(), R.drawable.building,facility.getTitle(),true);
//                        }
//                    }});
            }
        });
    }
}
