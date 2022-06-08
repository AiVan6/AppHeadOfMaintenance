package romanow.abc.android.yandexmap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
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

import java.util.StringTokenizer;

import firefighter.core.constants.ValuesBase;
import firefighter.core.utils.GPSPoint;
import romanow.abc.android.FragmentEmpty;
import romanow.abc.android.MainActivity;
import romanow.abc.android.MaintenanceFragment;
import romanow.abc.android.R;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.BaseActivity;

/**
 * This is a basic example that displays a map and sets camera focus on the target location.
 * Note: When working on your projects, remember to request the required permissions.
 */
public class MapActivity340 extends BaseActivity {
    protected MapObjectCollection mapObjects;
    protected int wx=800,wy=600;
    protected int zoom=20;
    protected MapView mapView;
    protected Handler animationHandler = new Handler();
    protected PlacemarkMapObject myPlace=null;
    public FragmentTransaction fragmentTransaction;
    public FragmentEmpty fragmentEmpty;
    private MainActivity base;
    private MaintenanceFragment maintenanceFragment;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MapKitFactory.setApiKey(AppData.MAPKIT_API_KEY);
        MapKitFactory.initialize(this);
        super.onCreate(savedInstanceState);

//        if (savedInstanceState == null){
//            this.getSupportFragmentManager().beginTransaction()
//                    .add(android.R.id.content, new MaintenanceFragment ()).commit();}
        //base = (MainActivity) this.getActivity();
        try {
            setContentView(R.layout.map340);
            mapView = (MapView)findViewById(R.id.mapview340);
            // And to show what can be done with it, we move the camera to the center of Saint Petersburg.
            //mapView.getMap().move(new CameraPosition(TARGET_LOCATION, 14.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 5), null);
            wy=this.getWindowManager().getDefaultDisplay().getHeight();
            wx=this.getWindowManager().getDefaultDisplay().getWidth();
            mapObjects = mapView.getMap().getMapObjects().addCollection();
//            onMyCreate();
//            paintSelf();
//            moveToSelf();
        }
        catch(Exception e1){
            popupInfo(e1.toString()); }


//        fragmentEmpty = new FragmentEmpty();
//        maintenanceFragment = new MaintenanceFragment();
//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.add(R.id.mapview340,fragmentEmpty);
//        fragmentTransaction.commit();
    }

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
        mapView.getMap().move(new CameraPosition(point, 20.0f, 0.0f, 0.0f), new Animation(Animation.Type.SMOOTH, 5), null);
    }
    @Override
    protected void onStop() {
        // Activity onStop call must be passed to both MapView and MapKit instance.
        super.onStop();
        mapView.onStop();
        MapKitFactory.getInstance().onStop();
        unregisterReceiver(gpsReceiver);        }
    @Override
    public void onStart() {
        // Activity onStart call must be passed to both MapView and MapKit instance.
        super.onStart();
        MapKitFactory.getInstance().onStart();
        mapView.onStart();
        IntentFilter filter = new IntentFilter(AppData.Event_GPS);
        this.registerReceiver(gpsReceiver, filter);
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
                System.out.println("111111111111111111111!!!");//обработчик кнопки
                Intent intent = new Intent(MapActivity340.this, MaintenanceFragment.class);
                intent.putExtra("title",tt.getText());
                //intent.setClass(MapActivity340.this, MaintenanceFragment.class);
                startActivity(intent);
                //////////////////////////////
//                fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.mapview340,maintenanceFragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
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
        myPlace.setOpacity(0.5f);
        myPlace.setIcon(ImageProvider.fromResource(this,R.drawable.user_location_gps));
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
        circle.setIcon(ImageProvider.fromResource(this,icon));
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
        //System.out.println("param"+text+" "+gp+" "+moveTo+" "+icon+" "+idx+" "+back);
        if (moveTo) moveTo(gp);
        return circle;
    }
    public void onMyCreate(){}          // Отложенные действия
    //----------------------------------------------------------------------------------
    @Override
    public void clearLog() {}
    @Override
    public void addToLog(String ss, int textSize) {
        popupInfo(ss);
    }
    @Override
    public void addToLogHide(String ss) {
        popupInfo(ss);
    }

    @Override
    public void addToLog(boolean fullInfoMes, String ss, int textSize, int textColor) { }

    @Override
    public void popupAndLog(String ss) {
        popupInfo(ss);
    }

    @Override
    public void errorMes(String ss) {
        AppData.ctx().errorMes(ss);
    }
    @Override
    public void notify(String s) {

    }

    @Override
    public boolean isFinish() {
        return false;
    }

    @Override
    public void onClose() {

    }
}
