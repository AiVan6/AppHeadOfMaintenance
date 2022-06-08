package romanow.abc.android.menu;

import romanow.abc.android.I_FacilityMenuGPS;
import romanow.abc.android.service.BaseActivity;

public class MenuFacilityGPS implements I_FacilityMenuGPS {
    @Override
    public void getFacility(BaseActivity activity) {

    }
//    private FacilityList noGps = new FacilityList();
//    AppData ctx = AppData.ctx();
//    LoginSettings settings = ctx.loginSettings();
//
//    @Override
//    public void getFacility(BaseActivity activity) {
//        Shift shift = AppData.gbl().shift();
//        if (shift==null)
//            return;
//        new NetCall<FacilityList>().call(ctx.getService().getFacilityList(settings.getSessionToken(),
//                Values.GetAllModeActual,2), new NetBackDefault(activity){
//            @Override
//            public void onSuccess(Object val) {
//                FacilityList list =  (FacilityList)val;
//                list.sortByTitle();
//                ArrayList<ListBoxItem> items = new ArrayList<>();
//                for(Facility fac : list){
//                    GPSPoint pp = fac.getAddress().getRef().getLocation().getRef();
//                    if (!(pp!=null &&  pp.gpsValid())){
//                        items.add(new ListBoxItem(R.drawable.nomap,fac.getName(),null,0));
//                        noGps.add(fac);
//                    }
//                }
//                for(Facility fac : list){
//                    GPSPoint pp = fac.getAddress().getRef().getLocation().getRef();
//                    if (pp!=null &&  pp.gpsValid()){
//                        items.add(new ListBoxItem(R.drawable.map,fac.getName(),null,0));
//                        noGps.add(fac);
//                    }
//                }
//                new ListBoxDialog(activity, "GPS объекта", items).setListener(new ListBoxListener() {
//                    @Override
//                    public void onSelect(int index) {
//                        Intent intent = new Intent();
//                        activity.gbl.setMapViewFacility(noGps.get(index));
//                        activity.formCall(TValues.FormFacilityMapGPS,intent);
//                    }
//                    @Override
//                    public void onLongSelect(int index) {
//                    }
//                }).setTextSize(15).setnLines(2).create();
//            }
//        });
//    }
}
