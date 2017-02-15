package swetabh.com.smsapp.adapter;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import swetabh.com.smsapp.R;
import swetabh.com.smsapp.models.SMSModel;
import swetabh.com.smsapp.ui.ListParentSmsFragment;
import swetabh.com.smsapp.util.Utils;

/**
 * Created by abhi on 14/02/17.
 */

public class SmsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<SMSModel> mSmsList;
    private List<SMSModel> mFilteredSmsList;
    private Fragment mFragment = null;
    private SmsFilter mFilter;

    public SmsAdapter(List<SMSModel> smsList) {
        mSmsList = smsList;
    }

    public SmsAdapter(List<SMSModel> smsList, ListParentSmsFragment listParentSmsFragment) {
        mSmsList = smsList;
        mFilteredSmsList = smsList;
        mFragment = listParentSmsFragment;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mFragment != null) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sms_item_parent, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_sms_item_child, parent, false);
            return new ChildViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder1, int position) {
        if (mFragment != null) {
            ViewHolder holder = (ViewHolder) holder1;
            holder.vh_Address.setText(mSmsList.get(position).getStrAddress());
            holder.vh_Body.setText(mSmsList.get(position).getStrbody());
            if (mSmsList.get(position).getIntCount() > 0) {
                holder.vh_Count.setText("" + mSmsList.get(position).getIntCount());
            } else {
                holder.vh_Count.setText("");
            }
        } else {
            ChildViewHolder holder = (ChildViewHolder) holder1;
            holder.vh_Body.setText(mSmsList.get(position).getStrbody());
        }
    }

    @Override
    public int getItemCount() {
        return mSmsList.size();
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SmsFilter();
        }
        return mFilter;
    }

    public void setOriginalVale() {
        this.mSmsList = mFilteredSmsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView vh_Address;
        public TextView vh_Body;
        public TextView vh_Count;

        public ViewHolder(View itemView) {
            super(itemView);

            vh_Address = (TextView) itemView.findViewById(R.id.parentItem_textView_address);
            vh_Body = (TextView) itemView.findViewById(R.id.parentItem_textView_body);
            vh_Count = (TextView) itemView.findViewById(R.id.parentItem_textView_count);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mFragment != null) {
                ((ListParentSmsFragment) mFragment).onClickSms(mSmsList.get(getAdapterPosition()).getStrAddress());

            }
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {
        public TextView vh_Body;

        public ChildViewHolder(View itemView) {
            super(itemView);
            vh_Body = (TextView) itemView.findViewById(R.id.childItem_textView_body);

        }

    }

    private class SmsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<SMSModel> tempList = new ArrayList<>();

                for (SMSModel model : Utils.sAllSMSList) {
                    if (model.getStrAddress().toLowerCase().contains(constraint.toString().toLowerCase())
                            || model.getStrbody().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(model);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = mFilteredSmsList.size();
                filterResults.values = mFilteredSmsList;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mSmsList = (ArrayList<SMSModel>) filterResults.values;
            notifyDataSetChanged();
        }
    }
}
