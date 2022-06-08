package romanow.abc.android.service;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import firefighter.core.UniException;
import firefighter.core.Utils;
import firefighter.core.constants.ValuesBase;
import romanow.abc.android.MainActivity;

public abstract class APICall2<T> {
    public abstract Call<T> apiFun();
    public APICall2(){}
    public T call(MainActivity base)throws UniException {
        String mes="";
        String mes1="";
        Response<T> res;
        long tt;
        try {
            tt = System.currentTimeMillis();
            res = apiFun().execute();
            } catch (Exception ex) {
                throw UniException.bug(ex);
                }
        if (!res.isSuccessful()){
            if (res.code()== ValuesBase.HTTPAuthorization){
                mes =  "Сеанс закрыт " + Utils.httpError(res);
                //logOff();
                throw UniException.io(mes);
                }
            try {
                mes1 = "Ошибка " + res.message() + " (" + res.code() + ")";
                mes = mes1+"$" + res.errorBody().string();
                }
            catch (IOException ex){ mes += "$Ошибка: "+ex.toString(); }
            base.errorMes(BaseActivity.EmoSet,mes);
            throw UniException.io(mes1);
            }
        //System.out.println("time="+(System.currentTimeMillis()-tt)+" мс");
        return res.body();
        }
}

