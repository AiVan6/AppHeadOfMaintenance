package romanow.abc.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;
import java.util.List;

import firefighter.core.UniException;
import firefighter.core.constants.Values;
import firefighter.core.constants.ValuesBase;
import firefighter.core.entity.EntityList;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.Shift;
import firefighter.core.entity.subjectarea.arrays.FacilityList;
import firefighter.core.entity.users.Technician;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;

public class TechnicianFragment extends Fragment {


    public TechnicianFragment() {

    }

    public MainActivity base;

    public TextView technicianTitle;
    public TextView technicianStet;
    public TextView technicianPos;
    public TextView technicianExp;
    public TextView technicianObject;
    public TextView technicianOnDate;

    private Spinner titleSpinner;
    private Spinner objSpinner;
   // private Spinner dateSpinner;

    private EditText statEdit;
    private EditText posEdit;
    private EditText expEdit;
    private EditText dataEdit;


    public FillingList technicianList;

    public GridLayout mainLayout;
    private AppData ctx;
    private LoginSettings settings;

    private FacilityList facilityLists;
    private MaintenanceFragment maintenanceFragment;
    private MapFragment mapFragment;
    private FragmentTransaction fragmentTransaction;
    private Button selectButton;
    private Button mapButton;
    private String enteredDate;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        base = (MainActivity) this.getActivity();
        ctx = AppData.ctx();
        settings = ctx.loginSettings();
        mainLayout = (GridLayout) view.findViewById(R.id.technicianLayout);
        initializationObj();
        fillingView();
    }

    public void initializationObj() {
        technicianList = new FillingList(base);
        facilityLists = new FacilityList();
        maintenanceFragment = new MaintenanceFragment();//MaintenanceFragment.getInstance()
        mapFragment =new MapFragment();
        Shift shift = new Shift();
        shift.getGPSList();

        technicianTitle = technicianList.textViewCreate(mainLayout,"Техник");
        titleSpinner = technicianList.spinnerCreate(mainLayout);
        technicianStet = technicianList.textViewCreate(mainLayout,"Состояние");
        statEdit = technicianList.editTextCreate(mainLayout);
        statEdit.setEnabled(false);

        technicianPos = technicianList.textViewCreate(mainLayout,"В должности с ");
        posEdit = technicianList.editTextCreate(mainLayout);
        technicianExp = technicianList.textViewCreate(mainLayout,"Стаж");
        expEdit = technicianList.editTextCreate(mainLayout);
        technicianObject = technicianList.textViewCreate(mainLayout,"Объект");
        objSpinner = technicianList.spinnerCreate(mainLayout);
        technicianOnDate = technicianList.textViewCreate(mainLayout,"Смена (дата)");
        dataEdit = technicianList.editTextCreate(mainLayout);
        selectButton = technicianList.buttonCreate(mainLayout,"Заявки",false);
        mapButton = technicianList.buttonCreate(mainLayout,"Карта",false);
    }

    public void fillingView() {

        List<Technician> listTitle = new ArrayList<>();

        base.showDialogDownloadPage();

        new NetCall<EntityList<Technician>>().call(base,
                ctx.getService().getTechnicianList(settings.getSessionToken(), ValuesBase.GetAllModeActual,2),
                new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                listTitle.addAll((EntityList<Technician>) val);

            }
        });

        new NetCall<FacilityList>().call(base,
                ctx.getService().getFacilityList(settings.getSessionToken(), Values.GetAllModeActual, 2),
                new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        facilityLists = (FacilityList) val;

                        setAllSpinner(listTitle);
                        base.cancelDialogDownloadPage();
                    }

                    @Override
                    public void onError(UniException ee) {
                        base.popupInfo(ee.toString());
                    }
                });

    }

    String selectedFacility = "";
    String currentTechnician = "";

    public void setAllSpinner(List<Technician> technicianArr) {
        List<String> arrTechnicianTitle = new ArrayList<>();


        for(Technician technician : technicianArr){
            arrTechnicianTitle.add(technician.getTitle());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                R.id.textViewSpinner,arrTechnicianTitle);
        adapter.setDropDownViewResource(R.layout.item_spinner_text_view);
        titleSpinner.setAdapter(adapter);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                currentTechnician = (String)adapterView.getItemAtPosition(i);
                List<String> objectArr = new ArrayList<>();

                for(Facility facility : facilityLists) {
                    if(currentTechnician.equals(facility.getTechnician().getTitle())){
                        objectArr.add(facility.getTitle());
                    }
                }

                ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                        R.id.textViewSpinner,objectArr);
                adapterObj.setDropDownViewResource(R.layout.item_spinner_text_view);
                objSpinner.setAdapter(adapterObj);

                objSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedFacility = (String) adapterView.getItemAtPosition(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                posEdit.setText(technicianArr.get(i).getStartWorkDate().monthToString());
                statEdit.setText(Values.TStateList[technicianArr.get(i).tState()]);
                expEdit.setText((String.valueOf(technicianArr.get(i).getWorkStanding())));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        processingButton();

    }

    public void processingButton() {
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                enteredDate = dataEdit.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("technicianTitle",currentTechnician);
                bundle.putString("facilityTitle",selectedFacility);
                bundle.putString("enteredDate",enteredDate);

                mapFragment.setArguments(bundle);
                fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainLayoutActivity,mapFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("technicianTitle",currentTechnician);
                bundle.putString("facilityTitle",selectedFacility);

                maintenanceFragment.setArguments(bundle);

                fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainLayoutActivity,maintenanceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_technician, container, false);
    }
}