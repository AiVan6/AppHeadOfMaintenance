package romanow.abc.android;

import android.widget.LinearLayout;

import firefighter.core.UniException;
import firefighter.core.constants.Values;
import firefighter.core.entity.baseentityes.JEmpty;
import firefighter.core.entity.subjectarea.WorkSettings;
import firefighter.core.entity.users.Account;
import firefighter.core.entity.users.User;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBack;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;

public class LoginSettingsMenu extends SettingsMenuBase {
    public LoginSettingsMenu(MainActivity base0){
        super(base0);
    }
    private AppData ctx;
    @Override
    public void settingsSave() {
        ctx.fileService().saveJSON(ctx.loginSettings());
        }

    @Override
    public void createDialog(LinearLayout trmain) {
        ctx = AppData.ctx();
        try {
            final LoginSettings set = AppData.ctx().loginSettings();
            LinearLayout layout = createItem("IP", set.getDataSetverIP(), true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setDataSetverIP(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            layout = createItem("Порт", ""+set.getDataServerPort(), new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    try {
                        set.setDataServerPort(Integer.parseInt(ss));
                        settingsChanged();
                        } catch (Exception ee){
                           base.popupInfo("Формат числа");}
                            }
                    });
            trmain.addView(layout);
            layout = createItem("Телефон", set.getUserPhone(),true,false,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPhone(ss);
                    settingsChanged();
                    }});
            trmain.addView(layout);
            layout = createItem("Пароль", "******", true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    set.setUserPass(ss);
                    settingsChanged();
                }});
            trmain.addView(layout);
            final boolean isLogin = ctx.cState()== AppData.CStateGreen;
            layout = createItem((!isLogin ? "Войти" : "Выйти"), "",true,true,new I_EventListener(){
                @Override
                public void onEvent(String ss) {
                    final SettingsMenuBase pp = LoginSettingsMenu.this;
                    if (!isLogin){
                        base.retrofitConnect();
                        Account acc = new Account("",set.getUserPhone(), set.getUserPass());
                        new NetCall<User>().call(base,ctx.getService().login(acc), new NetBack(){
                            @Override
                            public void onError(int code, String mes) {
                                if (code == Values.HTTPAuthorization)
                                    ctx.toLog(false,"Ошибка авторизации: "+mes+"");
                                else if (code==Values.HTTPNotFound)
                                    ctx.toLog(false,"Ошибка соединения: "+mes+"");
                                else
                                    ctx.toLog(false,mes);
                                }
                            @Override
                            public void onError(UniException ee) {
                                ctx.popupToastFatal(ee);
                                }
                            @Override
                            public void onSuccess(Object val) {
                                base.sessionOn();
                                User user =(User)val;
                                final LoginSettings set = ctx.loginSettings();
                                set.setUserId(user.getOid());
                                set.setSessionToken(user.getSessionToken());
                                new NetCall<WorkSettings>().call(base,ctx.getService().workSettings(ctx.loginSettings().getSessionToken()), new NetBackDefault() {
                                    @Override
                                    public void onSuccess(Object val) {
                                            ctx.workSettings((WorkSettings)(val));
                                            ctx.setRegisteredOnServer(true);
                                            }
                                        });
                                //base.addCalendar();//////////////////////////?
                               }
                            });
                        pp.cancel();
                        }
                    else{
                        new NetCall<JEmpty>().call(base,ctx.getService().logoff(ctx.loginSettings().getSessionToken()), new NetBackDefault(){
                            @Override
                            public void onSuccess(Object val) {
                                base.sessionOff();
                                ctx.cState(AppData.CStateGray);
                                base.getMainPage();
                                }
                            });
                        }
                    pp.cancel();
                    }
                });
            trmain.addView(layout);
        } catch(Exception ee){
            int a=1;
            }
        catch(Error ee){
            int u=0;
        }
    }
}

