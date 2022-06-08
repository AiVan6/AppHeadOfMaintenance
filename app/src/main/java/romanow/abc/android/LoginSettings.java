package romanow.abc.android;

public class LoginSettings {
    private String paramListName="...";
    private String mailToSend="romanow@ngs.ru";
    private String getTitle(){
        return paramListName;
    }
    private String dataSetverIP="217.71.138.9";
    private int dataServerPort=4567;
    private String userPhone="913*******";
    private String userPass="";
    private long userId=0;
    private String sessionToken="";
    private String registrationCode="";        // Хэш-код регистрации приложения
    private String mailHost="mail.nstu.ru";
    private String mailBox="romanow@corp.nstu.ru";
    private String mailPass="";
    private String mailSecur="starttls";
    private boolean fullInfo=false;
    private int mailPort=587;
    private String fatalMessage="";             // Текст фатального сообщения при перезагрузке
    private boolean technicianMode=false;       // Полнофункциональный режим
    public boolean isTechnicianMode() {
        return technicianMode; }
    public void setTechnicianMode(boolean technicianMode) {
        this.technicianMode = technicianMode; }
    public String getDataSetverIP() {
        return dataSetverIP; }
    public void setDataSetverIP(String dataSetverIP) {
        this.dataSetverIP = dataSetverIP; }
    public int getDataServerPort() {
        return dataServerPort; }
    public void setDataServerPort(int dataServerPort) {
        this.dataServerPort = dataServerPort; }
    public String getUserPhone() {
        return userPhone; }
    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone; }
    public String getUserPass() {
        return userPass; }
    public void setUserPass(String userPass) {
        this.userPass = userPass; }
    public String getSessionToken() {
        return sessionToken; }
    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken; }
    public long getUserId() {
        return userId; }
    public void setUserId(long iserId) {
        this.userId = iserId; }
    public String getRegistrationCode() {
        return registrationCode; }
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode; }
    public String getParamListName() {
        return paramListName; }
    public void setParamListName(String paramListName) {
        this.paramListName = paramListName; }
    public String getMailToSend() {
        return mailToSend; }
    public void setMailToSend(String mailToSend) {
        this.mailToSend = mailToSend; }
    public String getMailHost() {
        return mailHost; }
    public void setMailHost(String mailHost) {
        this.mailHost = mailHost; }
    public String getMailBox() {
        return mailBox; }
    public void setMailBox(String mailBox) {
        this.mailBox = mailBox; }
    public String getMailPass() {
        return mailPass; }
    public void setMailPass(String mailPass) {
        this.mailPass = mailPass; }
    public String getMailSecur() {
        return mailSecur; }
    public void setMailSecur(String mailSecur) {
        this.mailSecur = mailSecur; }
    public boolean isFullInfo() {
        return fullInfo; }
    public void setFullInfo(boolean fullInfo) {
        this.fullInfo = fullInfo; }
    public int getMailPort() {
        return mailPort; }
    public void setMailPort(int mailPort) {
        this.mailPort = mailPort; }
    public String getFatalMessage() {
        return fatalMessage; }
    public void setFatalMessage(String fatalMessage) {
        this.fatalMessage = fatalMessage; }
}
