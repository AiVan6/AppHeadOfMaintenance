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



                if(bundle != null) {



                    for(Facility facility : (FacilityList) val){
                        if(titleFacility.equals(facility.getTitle()) && facility.getGPS().gpsValid()) {
                            ctx.sendGPS(facility.getGPS(), R.drawable.building,facility.getTitle(),true);

                        }
                    }
                }else
                    for(Facility facility : (FacilityList)val){
                        if (facility.getGPS().gpsValid()) {
                            ctx.sendGPS(facility.getGPS(), R.drawable.building, facility.getTitle(), true);
                        }
                    }
            }});


        ArrayList<Technician> technicians = new ArrayList<>();



        new NetCall<EntityList<Technician>>().call(base, ctx.getService().getTechnicianList(ctx.loginSettings().getSessionToken(),
                0, 2), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                technicians.addAll((EntityList<Technician>)val);

                if(bundle != null) {
                    enteredDate = bundle.getString("enteredDate");


                    OwnDateTime dateTime = new OwnDateTime(enteredDate);
                    for (Technician technician : technicians) {
                        if (technicianTitle.equals(technician.getTitle())) {
                            currentTechnician = technician;
                        }
                    }
                    new NetCall<Shift>().call(base, ctx.getService().getShiftToDate(ctx.loginSettings().getSessionToken(),
                            currentTechnician.getOid(), dateTime.timeInMS(), 2, true), new NetBackDefault() {
                        @Override
                        public void onSuccess(Object val) {

                            Shift shift = (Shift) val;

                            if(shift.getGPSList() == null){//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                base.popupInfo("У данного техника нет смены");
                                return;
                            }
                            for(EntityLink<GPSPoint> point : shift.getGPSList())
                            {
                                String text = technicianTitle+point.getRef().geoTime().timeToString();
                                ctx.sendGPS(point.getRef(), R.drawable.technicianlmg,text, true);
                            }
                        }
                    });
                }
            }
        });




        OwnDateTime dateTime = new OwnDateTime("07.04.2022");
        Technician selectedTech = new Technician();
        for(Technician technician : technicians){
            if(technicianTitle.equals(technician.getTitle())){
                selectedTech = technician;
            }
        }



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
            base.popupInfo(e1.toString());
        }

    }
    String strFid;
    protected void moveToSelf(){
            moveTo(AppData.ctx().getLastGPS());
    }

    protected GPSPoint getDefaultLocation(){
        return AppData.ctx().getLastGPS();
    }

    protected void moveTo(GPSPoint pp){
        if (pp==null || !pp.gpsValid())
            return;
        Point point =  new Point(pp.geoy(),pp.geox());
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
        tt.setText(text);
        facilityTitle = text;
        //------------------------------------------------------------------------------------------
        final ViewProvider viewProvider = new ViewProvider(xx);
        Point newPoint = new Point (point.getLatitude()-0.00005, point.getLongitude());
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

    }

    protected void paintSelf(){
        final GPSPoint geo = AppData.ctx().getLastGPS();
        if (!geo.gpsValid()) return;
        if (myPlace!=null)
            mapObjects.remove(myPlace);

        myPlace = mapObjects.addPlacemark(new Point(geo.geoy(),geo.geox()));
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
        circle.addTapListener(new MapObjectTapListener() {
            @Override
            public boolean onMapObjectTap(MapObject mapObject, Point point) {
                popupText(point, text);
                if (back!=null)
                    back.onSelect(idx);
                return true;
            }
        });

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