package romanow.abc.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.runtime.image.ImageProvider;
import com.yandex.runtime.ui_view.ViewProvider;

import java.util.ArrayList;
import java.util.StringTokenizer;

import firefighter.core.constants.Values;
import firefighter.core.constants.ValuesBase;
import firefighter.core.entity.EntityLink;
import firefighter.core.entity.EntityList;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.Shift;
import firefighter.core.entity.subjectarea.arrays.FacilityList;
import firefighter.core.entity.users.Technician;
import firefighter.core.utils.GPSPoint;
import firefighter.core.utils.OwnDateTime;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;
import romanow.abc.android.yandexmap.I_MapSelect;


public class MapFragment extends Fragment {

    protected MapObjectCollection mapObjects;
    protected int wx=800,wy=600;
    protected int zoom=20;
    public MapView mapView;
    protected Handler animationHandler = new Handler();
    protected PlacemarkMapObject myPlace=null;
    public FragmentTransaction fragmentTransaction;
    public FragmentEmpty fragmentEmpty;
    private MainActivity base;
    private LoginSettings settings;
    private AppData ctx;
    private MaintenanceFragment maintenanceFragment;
    private String facilityTitle;
    private String titleFacility;
    private String technicianTitle;
    private String enteredDate;
    private Technician currentTechnician;

    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            GPSPoint gps = new GPSPoint();
            int state = intent.getIntExtra("state", ValuesBase.GeoNone);
            gps.setCoord(intent.getDoubleExtra("gpsY",0),
                    intent.getDoubleExtra("gpsX",0),false);
            gps.state(state);
            paint(intent.getStringExtra("title"),gps,intent.getIntExtra("drawId",R.drawable.mappoint),intent.getBooleanExtra("moveTo",false));
        }
    };

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        base = (MainActivity) this.getActivity();
        ctx = AppData.ctx();
        maintenanceFragment = MaintenanceFragment.getInstance();
        Bundle bundle = getArguments();
        if(bundle != null) {
            titleFacility = bundle.getString("facilityTitle");
            technicianTitle = bundle.getString("technicianTitle");
        }


        new NetCall<FacilityList>().call(base,ctx.getService().getFacilityList(
                ctx.loginSettings().getSessionToken(), Values.GetAllModeActual,2), new NetBackDefault(){
            @Override
            public void onSuccess(Object val) {
                //System.out.println("11"+val);



                if(bundle != null) {



                    for(Facility facility : (FacilityList) val){
                        if(titleFacility.equals(facility.getTitle()) && facility.getGPS().gpsValid()) {
                            //System.out.println("facility.getGPS():"+facility.getGPS());
                            ctx.sendGPS(facility.getGPS(), R.drawable.building,facility.getTitle(),true);

                        }
                    }
                }else
                    for(Facility facility : (FacilityList)val){
                        if (facility.getGPS().gpsValid()) {
                            System.out.println("facility.getGPS():"+facility.getGPS());
                            ctx.sendGPS(facility.getGPS(), R.drawable.building, facility.getTitle(), true);
                        }
                    }
            }});


        ArrayList<Technician> technicians = new ArrayList<>();



        new NetCall<EntityList<Technician>>().call(base, ctx.getService().getTechnicianList(ctx.loginSettings().getSessionToken(),
                0, 2), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                //System.out.println("Shift:"+((Shift)val).getGPSList().size());
                //OwnDateTime dateTime = new OwnDateTime("18.04.2022");
                technicians.addAll((EntityList<Technician>)val);

                if(bundle != null) {
                    //technicianTitle = bundle.getString("technicianTitle");
                    enteredDate = bundle.getString("enteredDate");
                    System.out.println("technicianTitle111"+technicianTitle);
                    System.out.println("enteredDate111"+enteredDate);

                    System.out.println("111111"+technicians.get(2).getTitle());
                    OwnDateTime dateTime = new OwnDateTime(enteredDate);
                    for (Technician technician : technicians) {
                        if (technicianTitle.equals(technician.getTitle())) {
                            System.out.println("technician123");
                            currentTechnician = technician;
                        }
                    }
                    //if(technician.getShifts() != null)
                    System.out.println("date:"+dateTime.timeInMS());
                    new NetCall<Shift>().call(base, ctx.getService().getShiftToDate(ctx.loginSettings().getSessionToken(),
                            currentTechnician.getOid(), dateTime.timeInMS(), 2, true), new NetBackDefault() {
                        @Override
                        public void onSuccess(Object val) {

//                            try {
//                                Thread.sleep(50000);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }

                            Shift shift = (Shift) val;
                            //.getGPSList().get(1713)
                            System.out.println("Shift1");
                            //if (shift.getGPSList().size() != 0) {
                            System.out.println("Shift2");
                            for(EntityLink<GPSPoint> point : shift.getGPSList())
                            //for(int i = 0; i < shift.getGPSList().size(); i++)
                            {
                                String text = technicianTitle+"\n"+"["+point.getRef().geoTime().timeToString()+"]";
                                ctx.sendGPS(point.getRef(), R.drawable.technicianlmg,text, true);
                                System.out.println("Shift:" + point.getRef());
                            }
                            //}
                        }
                    });
                }

//                for(int i = 0; i < ((EntityList<Technician>)val).size(); i++)
//                    for(int j = 0; j < ((EntityList<Technician>)val).get(i).getShifts().size();j++)
//                        //if(((EntityList<Technician>)val).get(i).getShifts().get(j).getRef() != null)
//                            System.out.println("Техник:"+((EntityList<Technician>)val).get(i).getShifts().get(j).getRef().getGPSList().get(0).getRef());
                //System.out.println("Техник:"+((EntityList<Technician>)val).get(5).getShifts().get);

//6

                //for(int i = 0; i < technicians.size(); i++) {
                   // System.out.println("1111111111111111:"+technicians.get(i).getShifts());
//                    new NetCall<Shift>().call(base, ctx.getService().getShift(ctx.loginSettings().getSessionToken(),
//                            technicians.get(i).getShifts().get(i).getOid(), 2), new NetBackDefault() {
//                        @Override
//                        public void onSuccess(Object val) {
//                            System.out.println("MyShift:"+val);
//                        }
//                    });
                //}
                //System.out.println("technicianShift:"+(((EntityList<Technician>)val).get(0).getShifts()));
            }
        });




        OwnDateTime dateTime = new OwnDateTime("07.04.2022");
        Technician selectedTech = new Technician();
        for(Technician technician : technicians){
            if(technicianTitle.equals(technician.getTitle())){
                selectedTech = technician;
            }
        }

//        new NetCall<Shift>().call(base, ctx.getService().getShiftToDate(ctx.loginSettings().getSessionToken(),
//                5,dateTime.timeInMS(), 2,true), new NetBackDefault() {
//            @Override
//            public void onSuccess(Object val) {
//                System.out.println("Shift:"+ ((Shift)val).getGPSList());
//                        Technician technician;
//            }
//        });

//        new NetCall<EntityList<Shift>>().call(base, ctx.getService().getShiftList(ctx.loginSettings().getSessionToken(), 0, 0),
//                new NetBackDefault() {
//            @Override
//            public void onSuccess(Object val) {
//              //  Shift shift = (Shift) val;
////                System.out.println("state:"+shift.getStates());
////                EntityLinkList<GPSPoint> gps = shift.getGPSList();
////                for(EntityLink<GPSPoint> jps : gps){
////                    System.out.println("Ref: "+jps.getRef());
////                }
//
//                //for(Shift shift1 : (EntityList<Shift>)val){
//                for(int i = 0; i< ((EntityList<Shift>)val).size(); i++){
//                    if(((EntityList<Shift>)val).get(i).getGPSList().size() != 0)
//                        for (int j = 0; j < ((EntityList<Shift>)val).get(i).getGPSList().size();j++) {
//                            EntityLink<GPSPoint> point = (EntityLink<GPSPoint>)(((EntityList<Shift>) val).get(i).getGPSList())
//                                    .get(j);
//                            GPSPoint g_point = point.getRef();
//                            System.out.println("Красаучег брат!: " +point);
//                        }
//                }
//
//                //technician.getShifts().get(0).getRef().getGPSList().get(0).getRef().geox()
//
//                    //System.out.println("Shift222:"+shift.getGPSList().get(0).getRef());
//
//                //System.out.println("888888!:"+shift.getGPSList().geo);
//            }
//        });



        try {
            mapView = (MapView)getActivity().findViewById(R.id.mapView);
            ctx = AppData.ctx();
            settings = ctx.loginSettings();
            // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
            //mapView.getMap().move(new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 5), null);
            wy=getActivity().getWindowManager().getDefaultDisplay().getHeight();
            wx=getActivity().getWindowManager().getDefaultDisplay().getWidth();
            mapObjects = mapView.getMap().getMapObjects().addCollection();
            onMyCreate();
            paintSelf();
            moveToSelf();
        }
        catch(Exception e1){
            System.out.println(e1.toString()); //?????????????????????????????????
        }

//        fragmentEmpty = new FragmentEmpty();

//        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.mapview340,fragmentEmpty);
//        fragmentTransaction.commit();
    }
    String strFid;
    protected void moveToSelf(){
//        Bundle bundle = getArguments();
//        if(bundle != null){
//            System.out.println("2222222222222222222!!");
//             strFid = bundle.getString("idFacility");
//             String strTid = bundle.getString("idTech");
//            new NetCall<Facility>().call(base, ctx.getService().getTechnicianAssignFacilityList(settings.getSessionToken(), Integer.parseInt(strFid), 2), new NetBackDefault() {
//                @Override
//                public void onSuccess(Object val) {
//                    System.out.println(val);
//                    Facility facility = (Facility) val;
////                    moveTo(facility.getGPS());
////                    System.out.println("111111111111111111111111111111!!"+facility.getGPS());
//                    ctx.sendGPS(facility.getGPS(), R.drawable.building,facility.getTitle(),true);
//                }
//            });
//             //moveToFacility(Integer.parseInt(strFid));
//            System.out.println("3333333333333!!");
//        }else
            moveTo(AppData.ctx().getLastGPS());
    }

    public void moveToFacility(int idF) {
        System.out.println("111111111111111111111111111111!!");

    }

    protected GPSPoint getDefaultLocation(){
        return AppData.ctx().getLastGPS();
    }

    protected void moveTo(GPSPoint pp){
        if (pp==null || !pp.gpsValid())
            return;
        Point point =  new Point(pp.geoy(),pp.geox());
        //Point point =  new Point(54.96781445,82.95159894278376);
        moveTo(point);
    }
    protected void moveTo(Point point) {
        //mapView.getMap().move(new CameraPosition(point, 20.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 5), null);
        mapView.getMap().move(new CameraPosition(point, 20.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 5), null);
    }

    public void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        getActivity().unregisterReceiver(gpsReceiver);        }
    @Override
    public void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_GPS);
        getActivity().registerReceiver(gpsReceiver, filter);
    }
    protected void paint(String text,GPSPoint geo,int icon,boolean moveTo){
        paint(text,geo,icon,moveTo,0,null);
    }

    private void popupText(Point point, String text){
        LinearLayout xx=(LinearLayout)getLayoutInflater().inflate(R.layout.listbox_item, null);
        xx.setBackgroundColor(0x00FFFFFF);
        xx.setPadding(5, 5, 5, 5);
        Button tt=(Button) xx.findViewById(R.id.dialog_listbox_name);
        text = text.replace("...","\n");
        StringTokenizer ss = new StringTokenizer(text,"\n");
        int cnt = ss.countTokens();
        tt.setLines(cnt);
        tt.setTextSize(15);
        tt.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //text += "\n"+point.getLatitude()+" "+point.getLatitude();
        tt.setText(text);
        facilityTitle = text;
        //System.out.println("dsfdlfmsdlfsmdfoldsmfolsdmclsdcml");
        //------------------------------------------------------------------------------------------
        //final ViewProvider viewProvider = new ViewProvider(textView);
        final ViewProvider viewProvider = new ViewProvider(xx);
        //Point newPoint = new Point (point.getLatitude(), point.getLongitude()+0.0001);
        Point newPoint = new Point (point.getLatitude()-0.00005, point.getLongitude());
        //System.out.println("point.getLongitude()"+point.getLongitude());
        final PlacemarkMapObject viewPlacemark = mapObjects.addPlacemark(newPoint, viewProvider);
        viewProvider.snapshot();
        viewPlacemark.setView(viewProvider);
        viewPlacemark.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(@NonNull MapObject mapObject, @NonNull Point point) {
                Bundle bundle = new Bundle();
                bundle.putString("facilityTitle",tt.getText().toString());
                bundle.putString("technicianTitle",technicianTitle);
                maintenanceFragment.setArguments(bundle);

                //////////////////////////////
                fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mapView,maintenanceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return false;
            }
        });

        animationHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mapObjects.remove(viewPlacemark);
            }
        }, AppData.PopupShortDelay*1000);

        System.out.println("mapObjectsmapObjectsmapObjects:"+mapObjects);
    }

    protected void paintSelf(){
        final GPSPoint geo = AppData.ctx().getLastGPS();
        if (!geo.gpsValid()) return;
        if (myPlace!=null)
            mapObjects.remove(myPlace);
        System.out.println("geogeogeogeogeogeoY:"+geo.geoy());
        System.out.println("geogeogeogeogeogeoX:"+geo.geox());
        myPlace = mapObjects.addPlacemark(new Point(geo.geoy(),geo.geox()));
        //myPlace = mapObjects.addPlacemark(new Point(54.96781445,82.95159894278376));
        myPlace.setOpacity(0.5f);
        myPlace.setIcon(ImageProvider.fromResource(getActivity(),R.drawable.user_location_gps));
        myPlace.setDraggable(false);         // ПЕРЕМЕЩЕНИЕ !!!!!!!!!!!!
        myPlace.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                String text = geo.toString();
                popupText(point, text);
                return true;
            }
        });
    }
    protected PlacemarkMapObject paint(final String text,GPSPoint geo,int icon,boolean moveTo, final int idx, final I_MapSelect back){
        if (!geo.gpsValid())
            return null;
        Point gp=new Point(geo.geoy(),geo.geox());
        return paint(text,gp,icon,moveTo,idx,back);
    }

    protected PlacemarkMapObject paint(final String text,Point gp,int icon,boolean moveTo, final int idx, final I_MapSelect back){
        final PlacemarkMapObject circle = mapObjects.addPlacemark(gp);
        circle.setOpacity(0.5f);
        circle.setIcon(ImageProvider.fromResource(getActivity(),icon));
        circle.setDraggable(false);
        //System.out.println("idx1:"+idx);
        circle.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                popupText(point, text);
                if (back!=null)
                    back.onSelect(idx);
                return true;
            }
        });

        //System.out.println("param"+text+" "+gp+" "+moveTo+" "+icon+" "+idx+" "+back);
        if (moveTo) moveTo(gp);
        return circle;
    }
    public void onMyCreate(){}          // Отложенные действия

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MapKitFactory.setApiKey(AppData.MAPKIT_API_KEY);
        MapKitFactory.initialize(getActivity());
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = v.findViewById(R.id.mapView);

        return v;
    }
}