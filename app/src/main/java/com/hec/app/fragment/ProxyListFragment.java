package com.hec.app.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.JsonParseException;
import com.hec.app.R;
import com.hec.app.activity.AddFriendActivity;
import com.hec.app.activity.DownloadTransferActivity;
import com.hec.app.activity.OfflineTransferActivity;
import com.hec.app.activity.ProxyLinkActivity;
import com.hec.app.activity.base.BaseApp;
import com.hec.app.entity.AgentListInfo;
import com.hec.app.entity.BizException;
import com.hec.app.entity.Result;
import com.hec.app.entity.SecurityInfoFinishInfo;
import com.hec.app.util.ConstantProvider;
import com.hec.app.util.DialogUtil;
import com.hec.app.util.IndexableListView;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.webservice.AccountService;
import com.hec.app.webservice.AgentService;
import com.hec.app.webservice.ServiceException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hec.app.util.ConstantProvider.*;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProxyListFragment extends ListFragment {

    private final String[] fields = {"Name", "ID", "Amount", "Change", "Profit"};
    private List<AgentListInfo> proxyList;
    private ProgressDialog mProgressDialog;
    EditText inputSearch;
    DListAdapter mAdapter;
    IndexableListView list_v;

    AgentService agentService;
    MyAsyncTask.OnError onError;
    int currentPage = 0;
    boolean loading = false;
    boolean hasNew = false;
    boolean mIsError = false;

    public ProxyListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        proxyList = new ArrayList<>();
        agentService = new AgentService();
        mAdapter = new DListAdapter(getActivity(), proxyList);
        setListAdapter(mAdapter);
        list_v = (IndexableListView) getListView();
        list_v.setFastScrollEnabled(true);

        onError = new MyAsyncTask.OnError() {

            @Override
            public void handleError(Exception e) {
                closeProgressDialog();
                mIsError = true;
            }
        };
        populateList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_proxy_list, container, false);

        inputSearch = (EditText) v.findViewById(R.id.search_proxy_list);
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                mAdapter.getFilter().filter(cs);
            }

            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub

            }

            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        //    getSupportActionBar().setElevation(0);

        Drawable drawable_add;
        drawable_add = ContextCompat.getDrawable(getContext(), R.mipmap.icon_add_friend);
        drawable_add.setBounds(0, 0, (int) (drawable_add.getIntrinsicWidth() * 0.5),
                (int) (drawable_add.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd_add = new ScaleDrawable(drawable_add, 0, -1, -1);
        RelativeLayout btn_add_rel = (RelativeLayout) v.findViewById(R.id.proxy_btn_rel_1);
        btn_add_rel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAddFriend();
            }
        });


        View btn = v.findViewById(R.id.proxy_btn_2);
        Drawable drawable;
        drawable = ContextCompat.getDrawable(getContext(), R.mipmap.icon_transfer);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.5),
                (int) (drawable.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, -1, -1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUserMonIn();
            }
        });

        View btn_link = v.findViewById(R.id.proxy_btn_3);
        Drawable drawable_link;
        drawable_link = ContextCompat.getDrawable(getContext(), R.mipmap.icon_open_account);
        drawable_link.setBounds(0, 0, (int) (drawable_link.getIntrinsicWidth() * 0.5),
                (int) (drawable_link.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd_link = new ScaleDrawable(drawable_link, 0, -1, -1);

        btn_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getActivity(), ProxyLinkActivity.class);
                startActivity(it);
            }
        });

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        AgentListInfo entry = mAdapter.getData().get(position);
        HashMap<String, String> mMap = new HashMap<>();
        mMap.put(fields[0], entry.getUserName());
        mMap.put(fields[1], entry.getUserID());
        mMap.put(fields[2], Double.toString(entry.getAvailableScores()));
        mMap.put(fields[3], Double.toString(entry.getTurnover()));
        mMap.put(fields[4], Double.toString(entry.getWinLoss()));
        Intent it = new Intent(getActivity(), DownloadTransferActivity.class);
        it.putExtra("data", mMap);
        startActivityForResult(it, 0);
    }

    void startAddFriend() {
        Intent it = new Intent(getActivity(), AddFriendActivity.class);
        startActivityForResult(it, 0);
    }

    private void populateList() {
        getResultData();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String id);
    }

    private void getResultData() {
        mIsError = false;
        showProgressDialog();
        MyAsyncTask<List<AgentListInfo>> task = new MyAsyncTask<List<AgentListInfo>>(getContext()) {

            @Override
            public List<AgentListInfo> callService() throws IOException,
                    JsonParseException, BizException, ServiceException {
                return agentService.getAgentList();
            }

            @Override
            public void onLoaded(List<AgentListInfo> result) throws Exception {
                if (getActivity() == null || getActivity().isFinishing())
                    return;
                closeProgressDialog();
                if (!mIsError) {
                    hasNew = true;
                    proxyList = result;
                    mHandler.sendEmptyMessage(0);
                } else {
                    BaseApp.changeUrl(getActivity(), new BaseApp.OnChangeUrlListener() {
                        @Override
                        public void changeSuccess() {
                            getResultData();
                        }

                        @Override
                        public void changeFail() {
                        }
                    });
                }
            }
        };
        task.setOnError(onError);
        task.executeTask();
    }

    private void checkUserMonIn() {
        MyAsyncTask<Result<Object>> task = new MyAsyncTask<Result<Object>>(getContext()) {
            @Override
            public Result<Object> callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new AccountService().checkUserMonIn();
            }

            @Override
            public void onLoaded(Result<Object> data) throws Exception {
                if (ProxyListFragment.this == null) {
                    return;
                }

                closeProgressDialog();
                if (!mIsError && data != null) {
                    if (data != null && data.isSuccess()) {
                        Intent it = new Intent(getActivity(), OfflineTransferActivity.class);
                        startActivityForResult(it, OFFLINE_TRANSFER_REQUEST_CODE);
                    } else {
                        DialogUtil.getAlertDialog(getActivity(), getString(R.string.friendly_reminder), data.getMessage(), getString(R.string.confirm_send), null, "", null).show();
                    }
                } else {
                    DialogUtil.getAlertDialog(getActivity(), getString(R.string.friendly_reminder), data.getMessage(), getString(R.string.confirm_send), null, "", null).show();
                }
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception e) {
                mIsError = true;
                DialogUtil.getErrorAlertDialog(getActivity(), e.toString()).show();
            }
        });
        task.executeTask();
    }

    private void showProgressDialog() {
        try {
            mProgressDialog = DialogUtil.getProgressDialog(getContext(), getResources().getString(R.string.loading_list));
            mProgressDialog.show();
        } catch (Exception e) {

        }
    }

    private void closeProgressDialog() {
        try {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        } catch (Exception e) {

        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //Update UI
            mAdapter.setData(proxyList);
            mAdapter.notifyDataSetChanged();
            closeProgressDialog();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            getResultData();
        } else if (requestCode == OFFLINE_TRANSFER_REQUEST_CODE) {
            if (resultCode == ConstantProvider.OFFLINE_TRANSFER_FINISH_RESULT_CODE) {
                getActivity().finish();
            }
        }
    }

    public class DListAdapter extends BaseAdapter implements Filterable, SectionIndexer {
        private Activity activity;
        private List<AgentListInfo> data;
        private List<AgentListInfo> originalData;
        private LayoutInflater inflater = null;

        private ItemFilter mFilter = new ItemFilter();
        List<AgentListInfo> filteredList = new ArrayList<>();

        public DListAdapter(Activity a, List<AgentListInfo> d) {
            activity = a;
            this.data = d;
            this.originalData = d;
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (data != null) {
                data = new ArrayList<>();
            }
        }

        public void setData(List<AgentListInfo> d) {
            this.data = d;
            this.originalData = d;
        }

        public List<AgentListInfo> getData() {
            return this.data;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View v = convertView;
            if (convertView == null) v = inflater.inflate(R.layout.proxy_list_row, null);
            TextView name = (TextView) v.findViewById(R.id.proxy_name);
            TextView amount = (TextView) v.findViewById(R.id.proxy_amount);
            TextView change = (TextView) v.findViewById(R.id.proxy_change);
            TextView profit = (TextView) v.findViewById(R.id.proxy_profit);
            AgentListInfo entry;
            entry = data.get(position);

            name.setText(entry.getUserName());

            String str = String.format("%.2f", entry.getAvailableScores());
            if (entry.getAvailableScores() <= 0)
                amount.setTextColor(Color.parseColor("#FF0000"));
            else
                amount.setTextColor(Color.parseColor("#202020"));
            amount.setText(str);

            str = String.format("%.2f", entry.getTurnover());
            change.setText(str);
            if (entry.getTurnover() <= 0)
                change.setTextColor(Color.parseColor("#08A09D"));
            else
                change.setTextColor(Color.parseColor("#202020"));

            str = String.format("%.2f", entry.getWinLoss());
            if (entry.getWinLoss() <= 0)
                profit.setTextColor(Color.parseColor("#08A09D"));
            else
                profit.setTextColor(Color.parseColor("#202020"));
            profit.setText(str);
            return v;
        }

        public Filter getFilter() {
            return mFilter;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String filterString = constraint.toString().toLowerCase();

                FilterResults results = new FilterResults();

                final List<AgentListInfo> list = originalData;

                int count = list.size();
                filteredList.clear();
                for (AgentListInfo entry : list) {
                    if (entry.getUserName().contains(constraint)) {
                        filteredList.add(entry);
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data = (ArrayList<AgentListInfo>) results.values;
                notifyDataSetChanged();
            }
        }

        private String mSections = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        @Override
        public int getPositionForSection(int section) {
            // If there is no item for current section, previous section will be selected
            for (int i = section; i >= 0; i--) {
                for (int j = 0; j < getCount(); j++) {
                    if (i == 0) {
                        // For numeric section
                        for (int k = 0; k <= 9; k++) {
                            if (data.get(j).getUserName().charAt(0) == (k + 48))
                                return j;
                        }
                    } else {
                        if (data.get(j).getUserName().charAt(0) == mSections.charAt(i) || data.get(j).getUserName().charAt(0) == (mSections.charAt(i) + 32))
                            return j;
                    }
                }
            }
            return 0;
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Object[] getSections() {
            String[] sections = new String[mSections.length()];
            for (int i = 0; i < mSections.length(); i++)
                sections[i] = String.valueOf(mSections.charAt(i));
            return sections;
        }

    }
}

