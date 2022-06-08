package romanow.abc.android.service;

import firefighter.core.utils.GPSPoint;

public interface GPSListener {
    public void onEvent(String ss);
    public void onGPS(GPSPoint gpsPoint);
}
