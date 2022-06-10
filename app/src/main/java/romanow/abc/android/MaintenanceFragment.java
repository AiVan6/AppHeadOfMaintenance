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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
    private LinearLayout mainLayout;

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
        mainLayout = (LinearLayout) view.findViewById(R.id.selectorMaintenanceLayout);

        ctx = AppData.ctx();
        settings = ctx.loginSettings();
        initializationObj();
        technicianFragment = new TechnicianFragment();

        fillingView();
    }

    ArrayList<TextView> textViewArray = new ArrayList<>();
    ArrayList<LinearLayout> spinnerArrayList = new ArrayList<>();

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

        ArrayList<String> titleName = new ArrayList<>(Arrays.asList("Тип заявки","Состояние","Оплата",
                "Акт","Техник","Дата","Объект"));

        TextView textView;
        LinearLayout elemLayout;

        textViewArray.clear();
        spinnerArrayList.clear();

        for(int i = 0; i < 7; i++) {
            textView = fillingList.textViewCreate(mainLayout, titleName.get(i));
            textViewArray.add(textView);

            elemLayout = fillingList.createListBox("V");
            mainLayout.addView(elemLayout);
            spinnerArrayList.add(elemLayout);
        }
        TextView dateStartText = fillingList.textViewCreate(mainLayout,"Начальная дата");
        dateStart = fillingList.editTextCreate(mainLayout);
        TextView dateEndText = fillingList.textViewCreate(mainLayout,"Конечная дата");
        dateEnd = fillingList.editTextCreate(mainLayout);
        elemLayout = fillingList.createListBox("V");
        mainLayout.addView(elemLayout);
        spinnerArrayList.add(elemLayout);



        //refreshView = fillingList.imageButtonCreate(mainLayout);

        LinearLayout layout = fillingList.imageButtonCreate(R.drawable.scan_red,"ОК",refreshView,buttonOk);
        refreshView = (ImageView) layout.getChildAt(0);
        buttonOk = (Button)layout.getChildAt(1);
        mainLayout.addView(layout);


        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                System.out.println("type:"+ maintenance.getMaintenanceType());
                if(maintenance == null){
                    base.popupInfo("Заявка не была выбрана");
                    return;
                }

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

        TextView typeSelector =(TextView)spinnerArrayList.get(0).getChildAt(0);
        TextView typeInfo =(TextView)spinnerArrayList.get(0).getChildAt(1);


        fillingList.fillingListBox(typeInfo, typeSelector, type,0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                typeId = index-1;//?
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });

        TextView stateSelector =(TextView)spinnerArrayList.get(1).getChildAt(0);
        TextView stateInfo =(TextView)spinnerArrayList.get(1).getChildAt(1);

        fillingList.fillingListBox(stateInfo, stateSelector, state, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                stateId = index -1;
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });


        TextView paySelector =(TextView)spinnerArrayList.get(2).getChildAt(0);
        TextView payInfo =(TextView)spinnerArrayList.get(2).getChildAt(1);

        fillingList.fillingListBox(payInfo, paySelector, PIState, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                payId = index -1;
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });

        TextView wcrStateSelector =(TextView)spinnerArrayList.get(3).getChildAt(0);
        TextView wcrStateInfo =(TextView)spinnerArrayList.get(3).getChildAt(1);

        fillingList.fillingListBox(wcrStateInfo, wcrStateSelector, wcrState, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                wcrstateId = index -1;
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });


        TextView dateSelector =(TextView)spinnerArrayList.get(5).getChildAt(0);
        TextView dateInfo =(TextView)spinnerArrayList.get(5).getChildAt(1);

        fillingList.fillingListBox(dateInfo, dateSelector, date, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                dateId = index -1;
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

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
                long t1 = dateId==0 ? 0 : dtime1.timeInMS();
                long t2 = dateId==0 ? 0 : dtime2.timeInMS();

                new NetCall<MaintenanceList>().call(base,
                        ctx.getService().getMaintenanceConditionList(settings.getSessionToken(),
                                stateId,payId,wcrstateId,typeId,facilityId,technicianId,dateId,t1,t2,0),
                        new NetBackDefault() {
                            @Override
                            public void onSuccess(Object val) {
                                arrMaintenance.clear();
                                arrMaintenance.addAll((MaintenanceList)val);
                                titleMaintenance.clear();
                                for (Maintenance maintenances : (MaintenanceList) val){
                                    titleMaintenance.add(maintenances.getTitle());
                                }

                                TextView maintenanceSelector =(TextView)spinnerArrayList.get(7).getChildAt(0);
                                TextView maintenanceInfo =(TextView)spinnerArrayList.get(7).getChildAt(1);

                                fillingList.fillingListBox(maintenanceInfo, maintenanceSelector,titleMaintenance, 0, new I_ListBoxListener() {
                                    @Override
                                    public void onSelect(int index) {
                                        //String str = (String) adapterView.getItemAtPosition(i);
                                        String str = titleMaintenance.get(index);
                                        new NetCall<Maintenance>().call(base, ctx.getService().getMaintenance(settings.getSessionToken(), arrMaintenance.get(index).getOid(), 2), new NetBackDefault() {
                                            @Override
                                            public void onSuccess(Object val) {
                                                maintenance = (Maintenance) val;
                                                maintenanceListFragment.setMaintenanceObj(maintenance);
                                            }
                                        });
                                    }

                                    @Override
                                    public void onLongSelect(int index) {

                                    }

                                    @Override
                                    public void onCancel() {

                                    }
                                });

//
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

                TextView maintenanceSelector =(TextView)spinnerArrayList.get(6).getChildAt(0);
                TextView maintenanceInfo =(TextView)spinnerArrayList.get(6).getChildAt(1);

                fillingList.fillingListBox(maintenanceInfo, maintenanceSelector,facilityTitle, 0, new I_ListBoxListener() {
                    @Override
                    public void onSelect(int index) {
                        if(currentFacility == null){
                            facilityId = index == 0 ? 0 : arrFacility.get(index-1).getOid();}
                    }

                    @Override
                    public void onLongSelect(int index) {

                    }

                    @Override
                    public void onCancel() {

                    }
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

                        TextView technicianSelector =(TextView)spinnerArrayList.get(4).getChildAt(0);
                        TextView technicianInfo =(TextView)spinnerArrayList.get(4).getChildAt(1);

                        fillingList.fillingListBox(technicianInfo, technicianSelector,listTitleTechnician, 0, new I_ListBoxListener() {
                            @Override
                            public void onSelect(int index) {
                                if(currentTechnician == null) {
                                    technicianId  = index == 0 ? 0 : listTechnician.get(index - 1).getOid();
                                }
                            }

                            @Override
                            public void onLongSelect(int index) {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });

//                        Spinner technicianSpinner;
//                        technicianSpinner = fillingSpinner(listTitleTechnician,spinnerArrayList.get(4));
//                        technicianSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                            @Override
//                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                                if(currentTechnician == null) {
//                                    technicianId  = i == 0 ? 0 : listTechnician.get(i - 1).getOid();
//                                }
//                            }
//
//                            @Override
//                            public void onNothingSelected(AdapterView<?> adapterView) {
//
//                            }
//                        });
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_maintenance, container, false);
    }
}