package romanow.abc.android;

import static firefighter.core.constants.Values.PIStates;
import static firefighter.core.constants.Values.WCRStates;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import firefighter.core.I_SelectObject;
import firefighter.core.constants.Values;
import firefighter.core.entity.EntityLink;
import firefighter.core.entity.artifacts.Artifact;
import firefighter.core.entity.subjectarea.Maintenance;
import firefighter.core.entity.subjectarea.MaintenanceJob;
import romanow.abc.android.service.AppData;

public class MaintenanceListFragment extends Fragment {

    private FillingList fillingList;
    private MainActivity base;
    private LinearLayout infoLayout;
    private LinearLayout workLayout;

    private AppData ctx;
    private LoginSettings settings;
    private ArrayList<TextView> infoArrayText;
    private ArrayList<Object> infoArrayObj;
    private ArrayList<TextView> workArrayText;
    private ArrayList<Object> workArrayObj;
    private Button back;
    private Maintenance maintenance;
    private MaintenanceFragment maintenanceFragment;

    private MaintenanceJob jobs;
    private MaintenanceJob job = new MaintenanceJob();
    private Artifact beginPhoto;
    private Artifact endPhoto;
    private WorkingFiles workingFiles;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        base = (MainActivity) this.getActivity();
        infoLayout = (LinearLayout) view.findViewById(R.id.infoMaintenanceLayout);
        workLayout = (LinearLayout) view.findViewById(R.id.workMaintenanceLayout);

        ctx = AppData.ctx();
        settings = ctx.loginSettings();

        initializationObj();
        if(maintenance != null)
            fillingView();
    }

    public MaintenanceListFragment() {
        // Required empty public constructor
    }

    public void setMaintenanceObj(Maintenance maintenance) {
        this.maintenance = maintenance;
    }

    public void initializationObj() {
        fillingList = new FillingList(base);
        workingFiles = new WorkingFiles(getActivity());
        job = new MaintenanceJob();
        maintenanceFragment = new MaintenanceFragment();

        ArrayList<String> infoTextView = new ArrayList<>(Arrays.asList("Тип","Состояние","Объект","Техник",
                "Акт Приемки работ","Дата регламента","Дата создания","Дата выполнения","Время начала",
                "Продолжительность","Начало(Факт)","Окончание(Факт)","Сумма оплаты","Оплата","Дата списания"));

        ArrayList<String> workTextView = new ArrayList<>(Arrays.asList("Работы","Наименование","Состояние","Продолж. по плану",
                "Начало по факту","Окончание по факту","Проблема","Инвентарь","Фото до","Фото после"));

        infoArrayText = new ArrayList<>();
        infoArrayObj = new ArrayList<>();
        workArrayText = new ArrayList<>();
        workArrayObj = new ArrayList<>();
        TextView textView;
        EditText editText;
        //Spinner spinner;
        ImageView imageView;
        LinearLayout layout;

        back = fillingList.buttonCreate(infoLayout,"Назад",true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                base.fragmentTransaction = base.getSupportFragmentManager().beginTransaction();
                base.fragmentTransaction.replace(R.id.mainLayoutActivity,maintenanceFragment);
                base.fragmentTransaction.addToBackStack(null);
                base.fragmentTransaction.commit();
            }
        });

        for(int i = 0; i < 15;i++){
            textView = fillingList.textViewCreate(infoLayout,infoTextView.get(i));
            infoArrayText.add(textView);
            if(i<15) {
                editText = fillingList.editTextCreate(infoLayout);
                infoArrayObj.add(editText);
            }else {
                layout = fillingList.createListBox("V");
                infoArrayObj.add(layout);
                infoLayout.addView(layout);
                //spinner = fillingList.spinnerCreate(infoLayout);
                //infoArrayObj.add(spinner);
            }
        }

        for(int i = 0; i < 10;i++){
                textView = fillingList.textViewCreate(workLayout, workTextView.get(i));
            workArrayText.add(textView);
            if(i==0 || i== 7) {
                layout = fillingList.createListBox("V");
                workArrayObj.add(layout);
                workLayout.addView(layout);
            }else if(i==8 || i == 9){
                imageView = fillingList.imageCreate(workLayout);
                workArrayObj.add(imageView);
            }else {
                editText = fillingList.editTextCreate(workLayout);
                workArrayObj.add(editText);
            }

        }
    }

    public void fillingView() {
        setEditView(0,infoArrayObj, Values.MaintenanceTypes[this.maintenance.getMaintenanceType()]);
        setEditView(1,infoArrayObj, Values.MaintenanceStates[this.maintenance.getMaintenanceState()]);
        setEditView(2,infoArrayObj, maintenance.getFacilityTitle());
        setEditView(3,infoArrayObj, maintenance.getTechnician().getTitle());
        setEditView(4,infoArrayObj, WCRStates[maintenance.getWorkCompletionReportState()]);//-'
        setEditView(5,infoArrayObj, maintenance.getReglamentMonth()+"-"+ maintenance.getReglamentYear());//-
        setEditView(6,infoArrayObj, maintenance.getCreateDate().dateToString());
        setEditView(7,infoArrayObj, maintenance.getVisitDate().dateToString());
        setEditView(8,infoArrayObj, maintenance.getVisitDate().timeToString());
        setEditView(9,infoArrayObj, String.valueOf(maintenance.getDuration()));
        setEditView(10,infoArrayObj, maintenance.getBeginTime().timeToString());
        setEditView(11,infoArrayObj, maintenance.getEndTime().timeToString());
        setEditView(12,infoArrayObj, maintenance.getCurrentPay().toString());
        setEditView(13,infoArrayObj, PIStates[maintenance.getPaymentState()]);
        setEditView(14,infoArrayObj, "");//дата списания
        ArrayList<String> maintenanceJobsTitle = new ArrayList<>();
        //maintenanceJobs.addAll(maintenance.getJobList());
        for(EntityLink<MaintenanceJob> jobEntityLink : maintenance.getJobList()){
            maintenanceJobsTitle.add(jobEntityLink.getTitle());
        }


        jobs = maintenance.getJobList().get(0).getRef();

        TextView selector =(TextView)((LinearLayout)workArrayObj.get(0)).getChildAt(0);
        TextView info =(TextView)((LinearLayout)workArrayObj.get(0)).getChildAt(1);

        fillingList.fillingListBox(info, selector, maintenanceJobsTitle, 0, new I_ListBoxListener() {
            @Override
            public void onSelect(int index) {
                jobs = maintenance.getJobList().get(index).getRef();
            }

            @Override
            public void onLongSelect(int index) {

            }

            @Override
            public void onCancel() {

            }
        });
        setEditView(1,workArrayObj,jobs.getFacilityOrder().getTitle());
        setEditView(2,workArrayObj,Values.JobState[jobs.getJobState()]);
        setEditView(3,workArrayObj,jobs.getDuration()+"");
        setEditView(4,workArrayObj,jobs.getBeginTime().timeToString());
        setEditView(5,workArrayObj,jobs.getEndTime().timeToString());
        setEditView(6,workArrayObj,jobs.getProblem()+"");
        //setSpinner(jobs.getImplList(),(Spinner) workArrayObj.get(8));
        ArrayList<String> strImpl = new ArrayList<>();
        for(int i = 0; i < jobs.getImplList().size();i++){
            strImpl.add(job.getImplList().get(i).getRef().getTitle());
        }

        setListBox(strImpl,(LinearLayout) workArrayObj.get(7));

        beginPhoto = jobs.getBeginPhoto().getRef();
        endPhoto = jobs.getEndPhoto().getRef();
        setImageArtifact(beginPhoto,((ImageView) workArrayObj.get(8)),"temp.jpg");
        setImageArtifact(endPhoto,((ImageView) workArrayObj.get(9)),"temp.jpg");

    }

    public void setImageArtifact(Artifact art,ImageView view,String file){
        workingFiles.startLoadByUrl(art,file, new I_SelectObject(){
            @Override
            public void onSelect(Object ent) {
                String fname = (String)ent;
                File image = new File(fname);


                if(image.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                    view.setImageBitmap(myBitmap);
                }
            }
        });
    }


    public void setEditView(int index,ArrayList<?> arr,String str) {

        EditText type = (EditText) arr.get(index);
        type.setText(str);
        type.setEnabled(false);
    }


    public void setListBox(ArrayList<String>arr,LinearLayout layout){
        TextView selector =(TextView)layout.getChildAt(0);
        TextView info =(TextView)layout.getChildAt(1);

        fillingList.fillingListBox(info, selector, arr, 0, new I_ListBoxListener() {
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
        return inflater.inflate(R.layout.fragment_maintenance_list, container, false);
    }

}