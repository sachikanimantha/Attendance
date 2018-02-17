package com.example.bellvantage.smartTracking.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.bellvantage.smartTracking.AttencaneDetailsActivity;
import com.example.bellvantage.smartTracking.R;
import com.example.bellvantage.smartTracking.SWF.AttendaneDataBean;
import com.example.bellvantage.smartTracking.SWF.DataLogin;
import com.example.bellvantage.smartTracking.Utils.DateManager;

import java.util.ArrayList;

/**
 * Created by Sachika on 12/01/2018.
 */

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.MyViewHolder> {

    Context context;
    ArrayList<AttendaneDataBean> arrayList = new ArrayList<>();
    DataLogin dataLogin;

    public AttendanceAdapter(Context context, ArrayList<AttendaneDataBean> arrayList,DataLogin dataLogin) {
        this.context = context;
        this.arrayList = arrayList;
        this.dataLogin = dataLogin;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_for_attendance,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final AttendaneDataBean attendaneDataBean = arrayList.get(position);
        try{
            String date = attendaneDataBean.getAttendanceDate();
            String inTime = attendaneDataBean.getTimeIN();
            String outTime = attendaneDataBean.getTimeOut();

            int start = date.indexOf("(");
            int end = date.indexOf(")");
            String attendanceDate = date.substring(start+1,end);
            holder.tvDate.setText(new DateManager().getDateAccordingToMil(Long.parseLong(attendanceDate), "yyyy/MM/dd"));

            start = inTime.indexOf("(");
            end = inTime.indexOf(")");
            inTime = inTime.substring(start+1,end);
            holder.tvInTime.setText(new DateManager().getDateAccordingToMil(Long.parseLong(inTime), "HH:mm:ss"));

            start = outTime.indexOf("(");
            end = outTime.indexOf(")");
            outTime = outTime.substring(start+1,end);

            if (inTime.equals(outTime)){
                holder.tvOutTime.setText("Not Available");
            }else{
                holder.tvOutTime.setText(new DateManager().getDateAccordingToMil(Long.parseLong(outTime), "HH:mm:ss"));
            }

            try {
                YoYo.with(Techniques.BounceInDown)
                        .duration(700)
                        .playOn(holder.view);
            } catch (Exception e) {

            }


            //item selection
            holder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context,AttencaneDetailsActivity.class);
                    intent.putExtra("AttendaneDataBean",attendaneDataBean);
                    intent.putExtra("DataLogin",dataLogin);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });

        }catch (Exception e){
            System.out.println("========= Error at Attendance Adapter ==============");
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        if (arrayList.size()!=0){
            return arrayList.size();
        }
        return 0;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        View view;
        TextView tvDate,tvInTime,tvOutTime;
        public MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvDate = view.findViewById(R.id.tvDate);
            tvInTime = view.findViewById(R.id.tvInTime);
            tvOutTime = view.findViewById(R.id.tvOutTime);
        }
    }
}
