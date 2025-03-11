package com.aaditya.inv.custom;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.aaditya.inv.R;
import com.aaditya.inv.models.HomeMenuModel;
import com.aaditya.inv.utils.Constants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapterHomeScreen extends RecyclerView.Adapter<RecyclerViewAdapterHomeScreen.ViewHolder> {

    private FragmentActivity fragmentActivity;
    private List<HomeMenuModel> menuList;

    public RecyclerViewAdapterHomeScreen(List<HomeMenuModel> auditCardList,FragmentActivity fragmentActivity){
        this.menuList = auditCardList;
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.home_menu,
                        viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.heading.setText(menuList.get(i).getHeading());
        viewHolder.description.setText(menuList.get(i).getDescription());
        viewHolder.icon.setImageDrawable(menuList.get(i).getIcon());
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView heading;
        public TextView description;

        public ImageView icon;
        public ViewHolder(View view) {
            super(view);
            heading = view.findViewById(R.id.menu_heading);
            description = view.findViewById(R.id.menu_description);
            icon = view.findViewById(R.id.menu_icon);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            try {
                switch (getAdapterPosition()) {
                    case 0: Navigation.findNavController(v).navigate(R.id.gold_rates_page);
                            break;
                    case 1: Navigation.findNavController(v).navigate(R.id.loan_application_page);
                            break;
                    case 2: Navigation.findNavController(v).navigate(R.id.loan_applications_views_page);
                            break;
                    default: Toast.makeText(fragmentActivity.getApplicationContext(), Constants.WORK_IN_PROGRESS_MSG, Toast.LENGTH_SHORT).show();
                            break;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(fragmentActivity.getApplicationContext(), Constants.WORK_IN_PROGRESS_MSG, Toast.LENGTH_SHORT).show();
            }
            //Commons.logEventDetails(events, context.getApplicationContext());
        }
    }
}
