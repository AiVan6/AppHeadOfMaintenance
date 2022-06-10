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

    public LinearLayout mainLayout;
    private AppData ctx;
    private LoginSettings settings;

    private FacilityList facilityLists;
    private MaintenanceFragment maintenanceFragment;
    private MapFragment mapFragment;
    private FragmentTransaction fragmentTransaction;
    private Button selectButton;
    private Button mapButton;
    private String enteredDate;
    private ListBoxDialog dialog;
    private LinearLayout technicianListBox;
    private LinearLayout objListBox;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        base = (MainActivity) this.getActivity();
        ctx = AppData.ctx();
        settings = ctx.loginSettings();
        mainLayout = (LinearLayout) view.findViewById(R.id.technicianLayout);
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
        flag = true;

        technicianTitle = technicianList.textViewCreate(mainLayout,"Техник");
        technicianListBox = technicianList.createListBox("V");
        mainLayout.addView(technicianListBox);

        technicianStet = technicianList.textViewCreate(mainLayout,"Состояние");
        statEdit = technicianList.editTextCreate(mainLayout);
        statEdit.setEnabled(false);

        technicianPos = technicianList.textViewCreate(mainLayout,"В должности с ");
        posEdit = technicianList.editTextCreate(mainLayout);

        technicianExp = technicianList.textViewCreate(mainLayout,"Стаж");
        expEdit = technicianList.editTextCreate(mainLayout);
        technicianObject = technicianList.textViewCreate(mainLayout,"Объект");
        objListBox = technicianList.createListBox("V");
        mainLayout.addView(objListBox);
        technicianOnDate = technicianList.textViewCreate(mainLayout,"Смена (дата)");
        dataEdit = technicianList.editTextCreate(mainLayout);


        LinearLayout layout = (LinearLayout) technicianList.twoButtonCreate(mainLayout,"Заявки","Карта",selectButton,mapButton);
        selectButton = (Button) layout.getChildAt(0);
        mapButton = (Button) layout.getChildAt(1);
        mainLayout.addView(layout);
        System.out.println("selectButton"+selectButton);
        System.out.println("mapButton"+mapButton);
    }

    ArrayList<String> arrTechnicianTitle = new ArrayList<>();
    ArrayList<Technician> listTechnician = new ArrayList<>();

    public void fillingView() {



        base.showDialogDownloadPage();

        new NetCall<EntityList<Technician>>().call(base,
                ctx.getService().getTechnicianList(settings.getSessionToken(), ValuesBase.GetAllModeActual,2),
                new NetBackDefault() {
            @Override
            public void onSuccess(Object val) {
                listTechnician.addAll((EntityList<Technician>) val);

            }
        });

        new NetCall<FacilityList>().call(base,
                ctx.getService().getFacilityList(settings.getSessionToken(), Values.GetAllModeActual, 2),
                new NetBackDefault() {
                    @Override
                    public void onSuccess(Object val) {
                        facilityLists = (FacilityList) val;
                        System.out.println("hi!!!");

                        for(Technician technician : listTechnician){
                            System.out.println("val:"+technician);
                            arrTechnicianTitle.add(technician.getTitle());
                        }

//                        LinearLayout layout = technicianList.createListBox("v", arrTechnicianTitle, 0, new I_ListBoxListener() {
//                            @Override
//                            public void onSelect(int index) {
//
//                            }
//
//                            @Override
//                            public void onLongSelect(int index) {
//
//                            }
//
//                            @Override
//                            public void onCancel() {
//
//                            }
//                        });

                        //mainLayout.addView(layout);
                        setAllSpinner(listTechnician);
                        System.out.println("зачему?");
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
    boolean flag = true;

    public void setAllSpinner(List<Technician> technicianArr) {
        ArrayList<String> arrTechnicianTitle = new ArrayList<>();
        System.out.println("Пачему1?");


        for(Technician technician : technicianArr){
            System.out.println("val:"+technician);
            arrTechnicianTitle.add(technician.getTitle());
        }

//        dialog = new ListBoxDialog( base, arrTechnicianTitle, "---", new I_ListBoxListener() {
//            @Override
//            public void onSelect(int index) {
//
//            }
//
//            @Override
//            public void onLongSelect(int index) {
//
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//        });
//
//        titleSpinner.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.create();
//            }
//        });



        //LinearLayout layout = create("---", arrTechnicianTitle, 0, new I_ListBoxListener());

        TextView selector =(TextView)technicianListBox.getChildAt(0);
        TextView info =(TextView)technicianListBox.getChildAt(1);
        System.out.println("selector"+selector);
        System.out.println("info"+info);

        TextView objSelector =(TextView)objListBox.getChildAt(0);
        TextView objInfo =(TextView)objListBox.getChildAt(1);
        System.out.println("objSelector"+objSelector);
        System.out.println("objInfo"+objInfo);
        selector.clearComposingText();
        ArrayList<String> objectArr = new ArrayList<>();
        technicianList.fillingListBox(info, selector, arrTechnicianTitle,0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                System.out.println("index:"+index);
//                posEdit.setText(technicianArr.get(index).getStartWorkDate().monthToString());
//                statEdit.setText(Values.TStateList[technicianArr.get(index).tState()]);
//                expEdit.setText((String.valueOf(technicianArr.get(index).getWorkStanding())));

              //  currentTechnician = arrTechnicianTitle.get(index);

                fillingViews(index);


                for(Facility facility : facilityLists) {
                    System.out.println("facility"+facility.getTitle()+"nameFacility:"+facility.getTechnician().getTitle());
                    if(currentTechnician.equals(facility.getTechnician().getTitle())){
                        objectArr.add(facility.getTitle());
                    }
                }

                    technicianList.fillingListBox(objInfo, objSelector, objectArr,0, new I_ListBoxListener() {
                        @Override
                        public void onSelect(int index) {
                            if(objectArr.size() != 0)
                                selectedFacility = objectArr.get(index);
                            else
                                selectedFacility = "";
                        }

                        @Override
                        public void onLongSelect(int index) {

                        }

                        @Override
                        public void onCancel() {

                        }
                    });

                    if(flag)
                        selectedFacility = objectArr.get(0);
                     flag = false;
//                ArrayAdapter<String> adapterObj = new ArrayAdapter<>(base.getApplicationContext(), R.layout.text_form_spinner,
//                        R.id.textViewPanel,objectArr);
//                adapterObj.setDropDownViewResource(R.layout.text_form_spinner);
//                objSpinner.setAdapter(adapterObj);
//
//
//                objSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                    @Override
//                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                        selectedFacility = (String) adapterView.getItemAtPosition(i);
//                    }
//
//                    @Override
//                    public void onNothingSelected(AdapterView<?> adapterView) {
//
//                    }
//                });
//
//                posEdit.setText(technicianArr.get(i).getStartWorkDate().monthToString());
//                statEdit.setText(Values.TStateList[technicianArr.get(i).tState()]);
//                expEdit.setText((String.valueOf(technicianArr.get(i).getWorkStanding())));
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {}
//        });

            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });
        if(flag) {


            fillingViews(0);
        }

        processingButton();

    }

    public void fillingViews(int index){
        posEdit.setText(listTechnician.get(index).getStartWorkDate().monthToString());
        statEdit.setText(Values.TStateList[listTechnician.get(index).tState()]);
        expEdit.setText((String.valueOf(listTechnician.get(index).getWorkStanding())));
        currentTechnician = arrTechnicianTitle.get(index);
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