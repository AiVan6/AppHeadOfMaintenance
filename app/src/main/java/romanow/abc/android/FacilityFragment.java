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

import firefighter.core.constants.Values;
import firefighter.core.entity.EntityLink;
import firefighter.core.entity.EntityLinkList;
import firefighter.core.entity.EntityList;
import firefighter.core.entity.subjectarea.Contract;
import firefighter.core.entity.subjectarea.Equipment;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.FacilityOrder;
import firefighter.core.entity.subjectarea.Implements;
import firefighter.core.entity.subjectarea.TechnicianAssign;
import firefighter.core.entity.subjectarea.arrays.FacilityList;
import romanow.abc.android.service.AppData;
import romanow.abc.android.service.NetBackDefault;
import romanow.abc.android.service.NetCall;


public class FacilityFragment extends Fragment {

    private TextView titleText;
    private TextView nameText;
    private TextView idText;
    private TextView serviceDateText;
    private TextView addressText;
    private TextView counterpartyText;
    private TextView technicianText;
    private TextView gpsText;
    private TextView regulationsText;
    private TextView inventoryText;
    private TextView contractText;
    private TextView securityText;
    private TextView contactPersonText;
    private TextView telephoneText;
    private TextView equipmentsText;



    public Spinner titleSpinner;
    public Spinner technicianTitleSpin;
    public Spinner regulationsSpinner;
    public Spinner inventorySpinner;
    public Spinner contractSpinner;
    public Spinner equipmentsSpinner;

    public EditText nameEdit;
    public EditText identifierEdit;
    public EditText serviceDateEdit;
    public EditText addressEdit;
    public EditText counterpartyEdit;
    public EditText gpsEdit;
    public EditText securityEdit;
    public EditText contactPersonEdit;
    public EditText telephoneEdit;

    private FillingList fillingList;
    private MainActivity base;
    private AppData ctx;
    private LoginSettings settings;
    private GridLayout mainLayout;
    private FacilityList facilityLists;
    private Button selectButton;
    private FragmentTransaction fragmentTransaction;
    private MaintenanceFragment maintenanceFragment;
    private String selectFacility;
    private String facilityTech;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        base = (MainActivity) this.getActivity();
        ctx = AppData.ctx();
        settings = ctx.loginSettings();
        mainLayout = view.findViewById(R.id.facilityLayout);
        initializationView();

        fillingView();
    }

    public FacilityFragment() {
        // Required empty public constructor
    }

    public void initializationView() {
        fillingList = new FillingList(base);
        facilityLists = new FacilityList();
        maintenanceFragment = new MaintenanceFragment();

        titleText = fillingList.textViewCreate(mainLayout,"Объект");
        titleSpinner = fillingList.spinnerCreate(mainLayout);
        nameText = fillingList.textViewCreate(mainLayout,"Наименование");
        nameEdit = fillingList.editTextCreate(mainLayout);
        idText= fillingList.textViewCreate(mainLayout,"Идентификатор");
        identifierEdit = fillingList.editTextCreate(mainLayout);
        serviceDateText= fillingList.textViewCreate(mainLayout,"Начало обслуживания");
        serviceDateEdit = fillingList.editTextCreate(mainLayout);
        addressText= fillingList.textViewCreate(mainLayout,"Адресс");
        addressEdit = fillingList.editTextCreate(mainLayout);
        counterpartyText= fillingList.textViewCreate(mainLayout,"Контрагент");
        counterpartyEdit = fillingList.editTextCreate(mainLayout);
        technicianText= fillingList.textViewCreate(mainLayout,"Техник");
        technicianTitleSpin = fillingList.spinnerCreate(mainLayout);
        gpsText= fillingList.textViewCreate(mainLayout,"GPS");
        gpsEdit = fillingList.editTextCreate(mainLayout);
        regulationsText= fillingList.textViewCreate(mainLayout,"Регламенты");
        regulationsSpinner = fillingList.spinnerCreate(mainLayout);
        inventoryText= fillingList.textViewCreate(mainLayout,"Инвентарь");
        inventorySpinner = fillingList.spinnerCreate(mainLayout);
        contractText= fillingList.textViewCreate(mainLayout,"Договор");
        contractSpinner = fillingList.spinnerCreate(mainLayout);


        securityText = fillingList.textViewCreate(mainLayout,"Пост охраны");
        securityEdit = fillingList.editTextCreate(mainLayout);
        contactPersonText = fillingList.textViewCreate(mainLayout,"Конт. Лицо");
        contactPersonEdit = fillingList.editTextCreate(mainLayout);
        telephoneText = fillingList.textViewCreate(mainLayout,"Доп. телефон");
        telephoneEdit = fillingList.editTextCreate(mainLayout);
        equipmentsText = fillingList.textViewCreate(mainLayout,"Оборудование");
        equipmentsSpinner = fillingList.spinnerCreate(mainLayout);
        selectButton = fillingList.buttonCreate(mainLayout,"Заявки",false);
    }

    private void fillingView() {
        base.showDialogDownloadPage();
        new NetCall<FacilityList>().call(base,
                ctx.getService().getFacilityList(settings.getSessionToken(), Values.GetAllModeActual, 2), new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        facilityLists = (FacilityList) val;
                        fillingFields();
                        base.cancelDialogDownloadPage();
                    }
                });

    }

    public void fillingFields (){
        List<String> facilityListsStr = new ArrayList<>();
        ArrayList<Facility> facilities = new ArrayList<>();

        for (Facility facility : this.facilityLists){
            facilityListsStr.add(facility.getTitle());
            System.out.println(facility.getTitle());
            facilities.add(facility);
        }

        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                R.id.textViewSpinner,facilityListsStr);
        adapterObj.setDropDownViewResource(R.layout.item_spinner_text_view);
        titleSpinner.setAdapter(adapterObj);
        titleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectFacility = (String) adapterView.getItemAtPosition(i);

                List<String> technicianList = new ArrayList<>();
                List<String> regulationsList = new ArrayList<>();
                List<String> inventoryList = new ArrayList<>();
                List<String> contractList = new ArrayList<>();
                List<String> equipmentsList = new ArrayList<>();

                new NetCall<EntityList<TechnicianAssign>>().call(base, ctx.getService().getTechnicianAssignFacilityList(settings.getSessionToken(),
                        facilityLists.get(i).getOid()), new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        for(TechnicianAssign technician : (EntityList<TechnicianAssign>) val){
                               technicianList.add(technician.getFacilityTitle());
                               facilityTech = technician.getTechnician().getTitle();
                        }

                        fillingSpinner(technicianList,technicianTitleSpin);
                    }
                });

                for(EntityLink<FacilityOrder> entityLink : (EntityLinkList<FacilityOrder>) facilityLists.get(i).getOrder()){
                    regulationsList.add(entityLink.getTitle());
                }
                fillingSpinner(regulationsList,regulationsSpinner);
                for (EntityLink<Implements> entityLinks : (EntityLinkList<Implements>)facilityLists.get(i).getImpl()) {
                    inventoryList.add(entityLinks.getTitle());
                }
                fillingSpinner(inventoryList,inventorySpinner);
                for(EntityLink<Contract> entityLink : (EntityLinkList<Contract>)facilityLists.get(i).getContract()) {
                    contractList.add(entityLink.getTitle());

                }
                fillingSpinner(contractList,contractSpinner);

                ArrayList<String> str = new ArrayList<>();
                for(EntityLink<Equipment> entityLink : (EntityLinkList<Equipment>) facilityLists.get(i).getEquipments()){
                    equipmentsList.add(entityLink.getRef().getType().getRef().getName());
                }

                fillingSpinner(equipmentsList,equipmentsSpinner);


                nameEdit.setText(facilityLists.get(i).getName());
                identifierEdit.setText(facilityLists.get(i).getIdentifier());
                serviceDateEdit.setText(facilityLists.get(i).getAssingmentDate().monthToString());
                serviceDateEdit.setEnabled(false);
                addressEdit.setText(facilityLists.get(i).getAddress().toShortString());
                addressEdit.setEnabled(false);
                counterpartyEdit.setText(facilityLists.get(i).getContractor().getTitle());
                counterpartyEdit.setEnabled(false);
                gpsEdit.setText(facilityLists.get(i).getGPS().getTitle());
                securityEdit.setText(String.valueOf(facilityLists.get(i).getSecurityPost()));//?
                contactPersonEdit.setText(facilityLists.get(i).getContactPerson().getTitle());
                telephoneEdit.setText(String.valueOf(facilityLists.get(i).getAdditionalPhones()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString("facilityTitle",selectFacility);
                bundle.putString("technicianTitle",facilityTech);
                maintenanceFragment.setArguments(bundle);

                fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainLayoutActivity,maintenanceFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    public void fillingSpinner(List<String> objData,Spinner spinner) {
        ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.item_spinner_text_view,
                R.id.textViewSpinner,objData);
        adapterObj.setDropDownViewResource(R.layout.item_spinner_text_view);
        spinner.setAdapter(adapterObj);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facility, container, false);
    }
}