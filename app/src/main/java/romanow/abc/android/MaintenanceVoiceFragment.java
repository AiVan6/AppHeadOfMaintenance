package romanow.abc.android;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;

import firefighter.core.I_SelectObject;
import firefighter.core.entity.artifacts.Artifact;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.Maintenance;
import firefighter.core.entity.subjectarea.Shift;
import firefighter.core.entity.subjectarea.arrays.MaintenanceList;
import firefighter.core.entity.users.Technician;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;


public class MaintenanceVoiceFragment extends Fragment {

    private FillingList fillingList;
    private MainActivity base;
    private GridLayout mainLayout;
    private ArrayList<Object> arrayView = new ArrayList<>();
    private Spinner maintenanceSpinner;
    private Spinner durationSpinner;
    private Maintenance maintenance;

    private AppData ctx;
    private LoginSettings settings;
    private ImageView voiceStart;
    private MediaPlayer mediaPlayer;
    private AudioPlayer player;
    private WorkingFiles workingFiles;
    private Technician curTechnician;
    private Facility curFacility;
    private Shift curShift;


    public MaintenanceVoiceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        base = (MainActivity) getActivity();
        ctx = AppData.ctx();
        settings = ctx.loginSettings();

        mainLayout = (GridLayout) getActivity().findViewById(R.id.voiceMaintenanceLayout);

        initializationObj();
        fillingView();
        voiceNetCall();
    }

    public void initializationObj(){
        fillingList = new FillingList(base);
        workingFiles = new WorkingFiles(getActivity());

        ArrayList<String> infoTextView = new ArrayList<>(Arrays.asList("Заявки","Создана","Получена","Ожидание",
                "Телефон","Объект","Техник","Смена","Продолжительность"));

        TextView textView;
        Spinner spinner;
        EditText editText;


        arrayView.clear();
        for (int i = 0; i < infoTextView.size();i++){
            textView = fillingList.textViewCreate(mainLayout,infoTextView.get(i));
            if(i == infoTextView.size()-1){
                spinner = fillingList.spinnerCreate(mainLayout);
                arrayView.add(spinner);
            }else {
                editText = fillingList.editTextCreate(mainLayout);
                arrayView.add(editText);
            }
        }

        voiceStart = fillingList.imageButtonCreate(mainLayout);
        player = new AudioPlayer(null,base);
        player.setImageView(voiceStart);

    }

    public void fillingView(){
        setEditView(0,arrayView,maintenance.getTitle());
        setEditView(1,arrayView,maintenance.getCreateDate().toString());
        setEditView(2,arrayView,maintenance.getReceiveDate().toString());
        setEditView(3,arrayView, String.valueOf(maintenance.getDuration()));
        setEditView(4,arrayView, String.valueOf(maintenance.getCallPhone()));

    }

    public void setEditView(int index,ArrayList<?> arr,String str) {

        EditText type = (EditText) arr.get(index);
        type.setText(str);
        type.setEnabled(false);
    }


    public void setVoiceMaintenance(Maintenance maintenance) {
        this.maintenance = maintenance;
    }
    MaintenanceList maintenanceList = new MaintenanceList();
    Artifact artifactVoice;
    public void voiceNetCall(){
        base.showDialogDownloadPage();
        new NetCall<MaintenanceList>().call(base, ctx.getService().getVoiceList(settings.getSessionToken(),2),
                new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {

                maintenanceList = (MaintenanceList) val;

                for(Maintenance m : (MaintenanceList) val) {
                    if(m.getTitle().equals(maintenance.getTitle())) {

                        artifactVoice = m.getVoiceMessage().getRef();
                        curTechnician = m.getTechnician().getRef();
                        curFacility = m.getFacility().getRef();
                        curShift = m.getShift().getRef();

                        voiceStart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                loadAndPlay();
                            }
                        });
                    }
                }
                setEditView(5,arrayView,curFacility.getTitle());
                setEditView(6,arrayView,curTechnician.getTitle());
                if(curShift == null)
                    setEditView(7,arrayView,"---");
                else
                    setEditView(7,arrayView, curShift.getTitle());
                base.cancelDialogDownloadPage();
            }
        });
    }


    private void loadAndPlay(){
        if (maintenanceList.size()==0) return;
        if (player.isPlaying()) {
            player.playStop();
            return;
        }

        workingFiles.startLoadByUrl(artifactVoice,"temp.wav", new I_SelectObject(){
            @Override
            public void onSelect(Object ent) {
                String fname = (String)ent;
                if (voiceStart!=null)
                    player.setImageView(voiceStart);
                player.playStart(fname);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance_voice, container, false);
    }
}