package romanow.abc.android;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import java.util.ArrayList;

import firefighter.core.constants.Values;
import firefighter.core.entity.EntityLink;
import firefighter.core.entity.EntityLinkList;
import firefighter.core.entity.EntityList;
import firefighter.core.entity.subjectarea.Contract;
import firefighter.core.entity.subjectarea.Equipment;
import firefighter.core.entity.subjectarea.Facility;
import firefighter.core.entity.subjectarea.FacilityOrder;
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
    private LinearLayout mainLayout;
    private FacilityList facilityLists;
    private Button selectButton;
    private FragmentTransaction fragmentTransaction;
    private MaintenanceFragment maintenanceFragment;
    private String selectFacility;
    private String facilityTech;

    private LinearLayout titleListBox;
    private LinearLayout technicianTitleListBox;
    private LinearLayout regulationsListBox;
    private LinearLayout contractListBox;
    private LinearLayout equipmentsListBox;


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
        titleListBox = fillingList.createListBox("V");
        mainLayout.addView(titleListBox);
        nameText = fillingList.textViewCreate(mainLayout,"Наименование");
        nameEdit = fillingList.editTextCreate(mainLayout);
        idText= fillingList.textViewCreate(mainLayout,"Идентификатор");
        identifierEdit = fillingList.editTextCreate(mainLayout);
        serviceDateText= fillingList.textViewCreate(mainLayout,"Начало обслуживания");
        serviceDateEdit = fillingList.editTextCreate(mainLayout);
        addressText= fillingList.textViewCreate(mainLayout,"Адрес");
        addressEdit = fillingList.editTextCreate(mainLayout);
        counterpartyText= fillingList.textViewCreate(mainLayout,"Контрагент");
        counterpartyEdit = fillingList.editTextCreate(mainLayout);
        technicianText= fillingList.textViewCreate(mainLayout,"Техник");
        technicianTitleListBox = fillingList.createListBox("V");
        mainLayout.addView(technicianTitleListBox);
        gpsText= fillingList.textViewCreate(mainLayout,"GPS");
        gpsEdit = fillingList.editTextCreate(mainLayout);
        regulationsText= fillingList.textViewCreate(mainLayout,"Регламенты");
        regulationsListBox = fillingList.createListBox("V");
        mainLayout.addView(regulationsListBox);
        contractText= fillingList.textViewCreate(mainLayout,"Договор");
        contractListBox = fillingList.createListBox("V");
        mainLayout.addView(contractListBox);


        securityText = fillingList.textViewCreate(mainLayout,"Пост охраны");
        securityEdit = fillingList.editTextCreate(mainLayout);
        contactPersonText = fillingList.textViewCreate(mainLayout,"Конт. Лицо");
        contactPersonEdit = fillingList.editTextCreate(mainLayout);
        telephoneText = fillingList.textViewCreate(mainLayout,"Доп. телефон");
        telephoneEdit = fillingList.editTextCreate(mainLayout);
        equipmentsText = fillingList.textViewCreate(mainLayout,"Оборудование");
        equipmentsListBox = fillingList.createListBox("V");
        mainLayout.addView(equipmentsListBox);
        //equipmentsSpinner = fillingList.spinnerCreate(mainLayout);
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

    ArrayList<String> facilityListsStr = new ArrayList<>();
    boolean flag = true;

    public void fillingFields (){

        ArrayList<Facility> facilities = new ArrayList<>();

        for (Facility facility : this.facilityLists){
            facilityListsStr.add(facility.getTitle());
            //System.out.println(facility.getTitle());
            facilities.add(facility);
        }

        TextView titleSelector =(TextView)titleListBox.getChildAt(0);
        TextView titleInfo =(TextView)titleListBox.getChildAt(1);
        System.out.println("selector"+titleSelector);
        System.out.println("info"+titleInfo);

        fillingList.fillingListBox(titleInfo, titleSelector, facilityListsStr, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                fillingViews(index);
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });

        if(flag){
            fillingViews(0);
        }

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


    public void fillingViews(int index) {
        if(facilityLists.size() !=0)
            selectFacility = facilityListsStr.get(index);

        ArrayList<String> technicianList = new ArrayList<>();
        ArrayList<String> regulationsList = new ArrayList<>();
        ArrayList<String> contractList = new ArrayList<>();
        ArrayList<String> equipmentsList = new ArrayList<>();
        //base.showDialogDownloadPage();
        new NetCall<EntityList<TechnicianAssign>>().call(base, ctx.getService().getTechnicianAssignFacilityList(settings.getSessionToken(),
                facilityLists.get(index).getOid()), new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                for(TechnicianAssign technician : (EntityList<TechnicianAssign>) val){
                    technicianList.add(technician.getFacilityTitle());
                    facilityTech = technician.getTechnician().getTitle();
                }

                fillingSpinner(technicianList,technicianTitleListBox);
               // base.cancelDialogDownloadPage();
            }
        });

        for(EntityLink<FacilityOrder> entityLink : (EntityLinkList<FacilityOrder>) facilityLists.get(index).getOrder()){
            regulationsList.add(entityLink.getTitle());
        }
        fillingSpinner(regulationsList,regulationsListBox);

        for(EntityLink<Contract> entityLink : (EntityLinkList<Contract>)facilityLists.get(index).getContract()) {
            contractList.add(entityLink.getTitle());

        }
        fillingSpinner(contractList,contractListBox);

        ArrayList<String> str = new ArrayList<>();
        for(EntityLink<Equipment> entityLink : (EntityLinkList<Equipment>) facilityLists.get(index).getEquipments()){
            equipmentsList.add(entityLink.getRef().getType().getRef().getName());
        }

        fillingSpinner(equipmentsList,equipmentsListBox);


        nameEdit.setText(facilityLists.get(index).getName());
        identifierEdit.setText(facilityLists.get(index).getIdentifier());
        serviceDateEdit.setText(facilityLists.get(index).getAssingmentDate().monthToString());
        serviceDateEdit.setEnabled(false);
        addressEdit.setText(facilityLists.get(index).getAddress().toShortString());
        addressEdit.setEnabled(false);
        counterpartyEdit.setText(facilityLists.get(index).getContractor().getTitle());
        counterpartyEdit.setEnabled(false);
        gpsEdit.setText(facilityLists.get(index).getGPS().getTitle());
        securityEdit.setText(String.valueOf(facilityLists.get(index).getSecurityPost()));//?
        contactPersonEdit.setText(facilityLists.get(index).getContactPerson().getTitle());
        telephoneEdit.setText(String.valueOf(facilityLists.get(index).getAdditionalPhones()));
    }


    public void fillingSpinner(ArrayList<String> objData,LinearLayout layout) {
        TextView selector =(TextView)layout.getChildAt(0);
        TextView info =(TextView)layout.getChildAt(1);
        System.out.println("selector"+selector);
        System.out.println("info"+info);

        fillingList.fillingListBox(info, selector, objData, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {

            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_facility, container, false);
    }
}