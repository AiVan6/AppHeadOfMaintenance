package romanow.abc.android;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Locale;

import firefighter.core.utils.GPSPoint;
import firefighter.core.utils.OwnDateTime;

public class FileDescription {
    private OwnDateTime createDate = new OwnDateTime(false);
    private String powerLine="";        // Группа (из имени файла)
    private String sensor="";           // Имя датчика
    private String support="";          // Имя опоры
    private String comment="";          // Комментарий
    private String originalFileName=""; // Оригинальное имя
    private GPSPoint gps = new GPSPoint();
    private double fileFreq = 0;        // Частота измерений из файла
    private int measureCounter=0;       // Последовательный номер измерения
    private String parseError = "";
    private String formatError="";
    public String validDescription(){
        String ss = "";
        if (powerLine.length()==0){
            if (ss.length()!=0) ss+="," ;
            ss+="нет линии";
        }
        if (support.length()==0){
            if (ss.length()!=0) ss+="," ;
            ss+="нет опоры";
        }
        if (sensor.length()==0){
            if (ss.length()!=0) ss+="," ;
            ss+="нет датчика";
        }
        if (!createDate.dateTimeValid()){
            if (ss.length()!=0) ss+="," ;
            ss+="нет даты";
        }
        if (measureCounter==0){
            if (ss.length()!=0) ss+="," ;
            ss+="нет номера измерений";
        }
        if (parseError.length()!=0){
            if (ss.length()!=0) ss+="," ;
            ss+=parseError;
        }
        return ss.length()==0 ? "" : "Формат: "+ss;
    }
    public void parseFromName() {
        formatError="";
        try{
            String ss = originalFileName.toLowerCase(Locale.ROOT);
            if (!ss.endsWith(".txt")){
                formatError = originalFileName + " - тип файла - не txt";
                return;
            }
            ss = ss.substring(0,ss.length()-4);
            int idx=ss.indexOf("_");
            if (idx==-1)
                return;
            String ss1 = ss.substring(0,idx);
            DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");
            DateTime tt = formatter.parseDateTime(ss1);
            createDate = new OwnDateTime(tt.getMillis());
            ss = ss.substring(idx+1);
            idx=ss.indexOf("_");
            if (idx==-1)
                return;
            ss1 = ss.substring(0,idx);
            ss = ss.substring(idx+1);
            int idx2 = ss1.indexOf("-");
            if (idx2!=-1){
                String ss2 = ss1.substring(0,idx2);
                try {
                    measureCounter = Integer.parseInt(ss2);
                } catch (Exception ee){
                    parseError = "Формат номера измерения: "+ss2;
                }
                sensor = ss1.substring(idx2+1);
            }
            idx2 = ss.indexOf("_");
            if (idx2==-1)
                idx2=ss.lastIndexOf(" ");
            if (idx2==-1)
                powerLine = ss;
            else{
                powerLine = ss.substring(0,idx2);
                support = ss.substring(idx2+1);
            }
        }
        catch(Exception ee){
            parseError = originalFileName+": "+ee.toString();
        }
    }
    private static String def(String ss){
        return ss.length()==0 ? "..." : ss;
    }
    public String toString(){
        return def(powerLine)+" / "+def(support)+" / "+def(sensor)+" ("+def(""+measureCounter)+")"+"\n"+createDate.dateTimeToString();
    }
    public String measureMetaData(){
        return toString()+"\nГеолокация: "+gps.toString()+ "\nЧастота: "+String.format("%6.2f",fileFreq)+"\nКомментарий: "+comment;
    }
    public FileDescription(String fname){
        originalFileName = fname;
        parseFromName();
    }
    public OwnDateTime getCreateDate() {
        return createDate; }
    public void setCreateDate(OwnDateTime createDate) {
        this.createDate = createDate; }
    public String getPowerLine() {
        return powerLine; }
    public void setPowerLine(String powerLine) {
        this.powerLine = powerLine; }
    public String getSensor() {
        return sensor; }
    public void setSensor(String sensor) {
        this.sensor = sensor; }
    public String getSupport() {
        return support; }
    public void setSupport(String support) {
        this.support = support; }
    public String getComment() {
        return comment; }
    public void setComment(String comment) {
        this.comment = comment; }
    public String getOriginalFileName() {
        return originalFileName; }
    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName; }
    public GPSPoint getGps() {
        return gps; }
    public void setGps(GPSPoint gps) {
        this.gps = gps; }
    public double getFileFreq() {
        return fileFreq; }
    public void setFileFreq(double fileFreq) {
        this.fileFreq = fileFreq; }
    public int getMeasureCounter() {
        return measureCounter; }
    public void setMeasureCounter(int measureCounter) {
        this.measureCounter = measureCounter; }
    public String getFormatError() {
        return formatError; }
    public void setFormatError(String formatError) {
        this.formatError = formatError; }
    //---------------------------------------------------------------------------------------------------------
    public static void main(String ss[]){
        FileDescription ff= new FileDescription("20210727T150304_316-4в_Изынский 245.txt");
        String zz = ff.validDescription();
        if (zz!=null)
            System.out.println(zz);
        System.out.println(ff.toString());
    }
}
