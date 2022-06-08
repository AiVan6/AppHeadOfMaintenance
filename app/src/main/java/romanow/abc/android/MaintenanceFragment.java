package romanow.abc.android;

import static firefighter.core.constants.Values.DateStates;
import static firefighter.core.constants.Values.MaintenanceStates;
import static firefighter.core.constants.Values.MaintenanceTypes;
import static firefighter.core.constants.Values.PIStates;
import static firefighter.core.constants.Values.WCRStates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import firefighter.core.constants.ValuesBase;
import firefighter.core.entity.EntityList;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.Maintenance;
import firefighter.core.entity.subjectarea.arrays.FacilityList;
import firefighter.core.entity.subjectarea.arrays.MaintenanceList;
import firefighter.core.entity.users.Technician;
import firefighter.core.utils.OwnDateTime;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;


public class MaintenanceFragment extends Fragment {

    private Button buttonOk;
    private ImageView refreshView;

    private FillingList fillingList;
    private MainActivity base;
    private GridLayout mainLayout;

    private AppData ctx;
    private LoginSettings settings;
    private MaintenanceListFragment maintenanceListFragment;
    private MaintenanceVoiceFragment maintenanceVoiceFragment;

    private MaintenanceList arrMaintenance;
    public Maintenance maintenance;
    private int maintenanceId = 1;
    private int typeId=-1,stateId=-1,payId=-1,wcrstateId=-1,dateId;
    private long technicianId=0;
    private long facilityId;
    private TechnicianFragment technicianFragment;
    private static MaintenanceFragment instance;

    private OwnDateTime dtime1;
    private OwnDateTime dtime2;

    private EditText dateEnd;
    private EditText dateStart;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        base = (MainActivity) this.getActivity();
        mainLayout = (GridLayout) view.findViewById(R.id.selectorMaintenanceLayout);

        ctx = AppData.ctx();
        settings = ctx.loginSettings();
        initializationObj();
        technicianFragment = new TechnicianFragment();

        fillingView();
    }

    ArrayList<TextView> textViewArray = new ArrayList<>();
    ArrayList<Spinner> spinnerArrayList = new ArrayList<>();

    public static MaintenanceFragment getInstance() {
        if (instance == null) {
            instance = new MaintenanceFragment();
        }
        return instance;
    }

    public void initializationObj() {
        fillingList = new FillingList(base);
        maintenanceListFragment = new MaintenanceListFragment();
        maintenanceVoiceFragment = new MaintenanceVoiceFragment();
        arrMaintenance = new MaintenanceList();

        ArrayList<String> titleName = new ArrayList<>(Arrays.asList("Тип зявки","Состояние","Оплата",
                "Акт","Техник","Дата","Объект"));

        TextView textView;
        Spinner spinner;

        textViewArray.clear();
        spinnerArrayList.clear();

        for(int i = 0; i < 7; i++) {
            textView = fillingList.textViewCreate(mainLayout, titleName.get(i));
            textViewArray.add(textView);

            spinner = fillingList.spinnerCreate(mainLayout);
            spinnerArrayList.add(spinner);
        }
        TextView dateStartText = fillingList.textViewCreate(mainLayout,"Начальная дата");
        dateStart = fillingList.editTextCreate(mainLayout);
        TextView dateEndText = fillingList.textViewCreate(mainLayout,"Конечная");
        dateEnd = fillingList.editTextCreate(mainLayout);
        spinner = fillingList.longSpinnerCreate(mainLayout);
        spinnerArrayList.add(spinner);

        refreshView = fillingList.imageButtonCreate(mainLayout);
        refreshView.setImageResource(R.drawable.scan_red);
        buttonOk = fillingList.buttonCreate(mainLayout,"ОК",false);

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("type:"+ maintenance.getMaintenanceType());

                if(maintenance.getMaintenanceType() == 3){
                    maintenanceVoiceFragment.setVoiceMaintenance(maintenance);
                    base.fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                    base.fragmentTransaction.replace(R.id.mainLayoutActivity,maintenanceVoiceFragment);
                    base.fragmentTransaction.addToBackStack(null);
                    base.fragmentTransaction.commit();
                }else {

                    base.fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                    base.fragmentTransaction.replace(R.id.mainLayoutActivity, maintenanceListFragment);
                    base.fragmentTransaction.addToBackStack(null);
                    base.fragmentTransaction.commit();
                }
            }
        });
    }


    String currentFacility= null;
    String currentTechnician= null;



    ArrayList<String> type = new ArrayList<>();
    ArrayList<String> state = new ArrayList<>();
    ArrayList<String> PIState = new ArrayList<>();
    ArrayList<String> wcrState = new ArrayList<>();
    ArrayList<String> date = new ArrayList<>();

    public void fillingView() {
        type.clear();
        state.clear();
        PIState.clear();
        wcrState.clear();
        date.clear();


        type.add("---");
        type.addAll(Arrays.asList(MaintenanceTypes));
        state.add("---");
        state.addAll(Arrays.asList(MaintenanceStates));
        PIState.add("---");
        PIState.addAll(Arrays.asList(PIStates));
        wcrState.add("---");
        wcrState.addAll(Arrays.asList(WCRStates));
        date.addAll(Arrays.asList(DateStates));

        Spinner typeSpinner,stateSpinner,paySpinner,wcrSpinner,dateSpinner;

        typeSpinner = fillingSpinner(type,spinnerArrayList.get(0));
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                typeId = i-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        stateSpinner = fillingSpinner(state,spinnerArrayList.get(1));
        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                stateId = i-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        paySpinner = fillingSpinner(PIState,spinnerArrayList.get(2));
        paySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                payId = i-1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        wcrSpinner = fillingSpinner(wcrState,spinnerArrayList.get(3));
        wcrSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                wcrstateId = i -1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        dateSpinner = fillingSpinner(date,spinnerArrayList.get(5));
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                dateId = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Bundle bundle = getArguments();
        System.out.println("bundle:"+bundle);
        if(bundle != null) {
            currentFacility = bundle.getString("facilityTitle");//facilityTitle
            currentTechnician = bundle.getString("technicianTitle");
        }

        fillingTechnicianSpinner();
        fillingFacilitySpinner();
        fillingMaintenanceSpinner();
    }

    public void fillingMaintenanceSpinner() {
        ArrayList<String> titleMaintenance = new ArrayList<>();

        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base.showDialogDownloadPage();
                dtime1 = new OwnDateTime(dateStart.getText().toString());
                dtime2 = new OwnDateTime(dateEnd.getText().toString());
                System.out.println("dataStart:"+dateStart.getText().toString());
                System.out.println("dataEnd:"+dateStart.getText().toString());
                long t1 = dateId==0 ? 0 : dtime1.timeInMS();
                long t2 = dateId==0 ? 0 : dtime2.timeInMS();

                System.out.println("info0:"+stateId+" "+payId+" "+wcrstateId+" facilityID:"+facilityId+" "+typeId+" TechnicianID:"+
                        technicianId+" "+dateId+" t1:"+t1+" t2:"+t2);
                new NetCall<MaintenanceList>().call(base,
                        ctx.getService().getMaintenanceConditionList(settings.getSessionToken(),
                                stateId,payId,wcrstateId,typeId,facilityId,technicianId,dateId,t1,t2,0),//stateId,payId,wcrstateId,typeId,facilityId,technicianId,dateId,t1,t2,0
                        new NetBackDefault() {
                            @Override
                            public void onSuccess(Object val) {
                                arrMaintenance.clear();
                                arrMaintenance.addAll((MaintenanceList)val);
                                System.out.println("arr:"+arrMaintenance);
                                titleMaintenance.clear();
                                for (Maintenance maintenances : (MaintenanceList) val){
                                    titleMaintenance.add(maintenances.getTitle());
                                }

                                ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                                        R.id.textViewSpinner,titleMaintenance);
                                adapterObj.setDropDownViewResource(R.layout.item_spinner_text_view);
                                spinnerArrayList.get(7).setAdapter(adapterObj);
                                spinnerArrayList.get(7).setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                        String str = (String) adapterView.getItemAtPosition(i);
                                        new NetCall<Maintenance>().call(base, ctx.getService().getMaintenance(settings.getSessionToken(), arrMaintenance.get(i).getOid(), 2), new NetBackDefault() {
                                            @Override
                                            public void onSuccess(Object val) {
                                                maintenance = (Maintenance) val;
                                                maintenanceListFragment.setMaintenanceObj(maintenance);
                                            }
                                        });

                                    }

                                    @Override
                                    public void onNothingSelected(AdapterView<?> adapterView) {

                                    }
                                });
                                base.cancelDialogDownloadPage();
                            }
                        });

            }
        });
    }


    public void fillingFacilitySpinner() {
        ArrayList<Facility> arrFacility = new ArrayList<>();

        base.showDialogDownloadPage();

        new NetCall<FacilityList>().call(base, ctx.getService().getFacilityList(settings.getSessionToken(),
                ValuesBase.GetAllModeActual, 2), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                arrFacility.addAll((FacilityList) val);

                ArrayList<String> facilityTitle = new ArrayList<>();
                if(currentFacility == null || currentFacility.equals("")) {
                    facilityTitle.add("---");
                    for(int j = 0; j < arrFacility.size(); j++) {
                        facilityTitle.add(arrFacility.get(j).getTitle());
                    }
                }else {
                    for(int j = 0; j < arrFacility.size(); j++){
                        if(currentFacility.equals(arrFacility.get(j).getTitle())){
                            facilityId = arrFacility.get(j).getOid();
                            facilityTitle.add(arrFacility.get(j).getTitle());
                        }
                    }
                }

                Spinner facilitySpinner;
                facilitySpinner = fillingSpinner(facilityTitle,spinnerArrayList.get(6));
                facilitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if(currentFacility == null){
                            facilityId = i == 0 ? 0 : arrFacility.get(i-1).getOid();}
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) { }
                });
                base.cancelDialogDownloadPage();
            }
        });
    }

    public void fillingTechnicianSpinner() {
        ArrayList<String> listTitleTechnician = new ArrayList<>();
        ArrayList<Technician> listTechnician = new ArrayList<>();
        new NetCall<EntityList<Technician>>().call(base,
                ctx.getService().getTechnicianList(settings.getSessionToken(), ValuesBase.GetAllModeActual,2),
                new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        for(Technician technician : (EntityList<Technician>)val){
                            listTechnician.add(technician);
                        }

                        if(currentTechnician == null){
                            listTitleTechnician.add(0,"---");
                            for(int j = 0; j < listTechnician.size(); j++){
                                listTitleTechnician.add(listTechnician.get(j).getTitle());
                            }
                        }else {
                            for(int j = 0; j < listTechnician.size(); j++){

                                if(currentTechnician.equals(listTechnician.get(j).getTitle())){
                                    technicianId = listTechnician.get(j).getOid();
                                    //System.out.println("technicianId:"+technicianId);
                                    listTitleTechnician.add(listTechnician.get(j).getTitle());
                                }

                            }
                        }

                        Spinner technicianSpinner;
                        technicianSpinner = fillingSpinner(listTitleTechnician,spinnerArrayList.get(4));
                        technicianSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if(currentTechnician == null) {
                                    technicianId  = i == 0 ? 0 : listTechnician.get(i - 1).getOid();
                                }
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }
                });
    }

    public Spinner fillingSpinner(ArrayList list, Spinner spinner) {
        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                R.id.textViewSpinner,list);
        adapterObj.setDropDownViewResource(R.layout.item_spinner_text_view);
        spinner.setAdapter(adapterObj);
        return  spinner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }
}