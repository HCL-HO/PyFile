package com.hec.app.customer_service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.JsonParseException;
import com.hec.app.BuildConfig;
import com.hec.app.R;
import com.hec.app.entity.BizException;
import com.hec.app.entity.CustomServiceChatInfo;
import com.hec.app.entity.CustomerInfo;
import com.hec.app.entity.CustomerServiceChatDetailInfo;
import com.hec.app.entity.MessageReceiveInfo;
import com.hec.app.entity.Response;
import com.hec.app.util.BitmapUtil;
import com.hec.app.util.ChatMessageGetTask;
import com.hec.app.util.CustomerAccountManager;
import com.hec.app.util.MyAsyncTask;
import com.hec.app.util.PictureUtil;
import com.hec.app.webservice.CustomerService;
import com.hec.app.webservice.ServiceException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.app.Activity.RESULT_OK;
import static com.hec.app.config.UrlConfig.CUSTOMER_SERVICE_IMAGE_URL_PREFIX;
import static com.hec.app.config.UrlConfig.TEST_CUSTOMER_SERVICE_IMAGE_URL_PREFIX;

public class CustomerServiceFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String URL_REGEX = "(https?|ftp|http):[^\\s^\n]*";
    private static final int PICK_FROM_GALLERY = 1;
    private static final int MAX_INPUT_LENGTH = 1000;
    private static final int MAX_PICTURE_SIZE = 3;//mb
    private EditText inputEdit;
    private ImageView uploadImgBtn;
    private TextView sendBtn;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SimpleDateFormat chatTime = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat chatDate = new SimpleDateFormat("yyyy-MM-dd");
    private boolean isError = false;
    private List<CustomServiceChatInfo> chatList;
    private int countEditText = 0;
    private Pattern pattern = Pattern.compile("/Date\\((.*?)\\)/");
    private String todayDate;
    private ChatMessageGetTask chatMessageGetTask;
    private OnMessageReceiveListener listener;
    private View contentView;
    private int keyboardHeight;
    private int softButtonsBarHeight;
    private String userId;
    private boolean isShowKeyboard;
    private View topView;
    private List<CustomerServiceChatDetailInfo> dataList;

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private LinearLayoutManager layoutManager;
    private boolean isLockUploadPicture = false;

    private int currentMessageID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.customer_service_fragment, container, false);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        initKeyboard();

        getChatInfo(todayDate);

        if (userId != null) {
            chatMessageGetTask = new ChatMessageGetTask(getContext(), userId, listener);
            chatMessageGetTask.execute();
        }
    }

    private void initKeyboard() {
        FrameLayout content = (FrameLayout) ((Activity) getContext()).findViewById(android.R.id.content);
        final View mChildOfContent = content.getChildAt(0);
        final int statusBarHeight = getStatusBarHeight(getContext());
        final int titleBarHeight = ((Activity) getContext()).findViewById(R.id.appBar) == null ? 0 : ((Activity) getContext()).findViewById(R.id.appBar).getHeight();
        softButtonsBarHeight = getSoftButtonsBarHeight((Activity) getContext());
        mChildOfContent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                //r will be populated with the coordinates of your view that area still visible.
                mChildOfContent.getWindowVisibleDisplayFrame(r);
                int screenHeight = mChildOfContent.getRootView().getHeight();
                int heightDiff = screenHeight - r.bottom;

                if (keyboardHeight == 0 && heightDiff > softButtonsBarHeight) {
                    keyboardHeight = heightDiff - softButtonsBarHeight;
                }

                if (isShowKeyboard) {
                    // 如果软键盘是弹出的状态，并且heightDiff小于等于 状态栏 + 虚拟按键 高度，
                    // 说明这时软键盘已经收起
                    if (heightDiff <= statusBarHeight + softButtonsBarHeight) {
                        isShowKeyboard = false;
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                        lp.height = RelativeLayout.LayoutParams.MATCH_PARENT;
                        contentView.setLayoutParams(lp);
                    }
                } else {
                    // 如果软键盘是收起的状态，并且heightDiff大于 状态栏 + 虚拟按键 高度，
                    // 说明这时软键盘已经弹出
                    if (heightDiff > statusBarHeight + softButtonsBarHeight) {
                        isShowKeyboard = true;
                        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
                        lp.height = screenHeight - softButtonsBarHeight - statusBarHeight - titleBarHeight - topView.getHeight() - keyboardHeight;
                        contentView.setLayoutParams(lp);
                    }
                }
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static int getSoftButtonsBarHeight(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight)
            return realHeight - usableHeight;
        else
            return 0;
    }

    private void initView(View view) {
        inputEdit = (EditText) view.findViewById(R.id.input_edit);
        uploadImgBtn = (ImageView) view.findViewById(R.id.upload_btn);
        sendBtn = (TextView) view.findViewById(R.id.send_btn);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        contentView = view.findViewById(R.id.content_rl);
        topView = view.findViewById(R.id.top);

        sendBtn.setOnClickListener(this);
        uploadImgBtn.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        inputEdit.requestFocus();
        inputEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString();
                char[] chars = input.toCharArray();
                for (int i = 0; i < chars.length; i++) {
                    if (countEditText < MAX_INPUT_LENGTH) {
                        countEditText++;
                    } else {
                        inputEdit.removeTextChangedListener(this);
                        inputEdit.setText(input.substring(0, i));
                        inputEdit.setSelection(i);
                        inputEdit.addTextChangedListener(this);
                        countEditText = 0;
                        break;
                    }
                }
                countEditText = 0;
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MessageAdapter(getContext());
        recyclerView.setAdapter(adapter);
    }

    private void initData() {
        dataList = new ArrayList<>();
        todayDate = chatDate.format(new Date());
        listener = new OnMessageReceiveListener() {
            @Override
            public void onMessageReceive(MessageReceiveInfo.ContentInfoRawData contentInfo) {
                if (contentInfo == null) {
                    return;
                }
                if (contentInfo.isNew()) {
                    getChatInfo(todayDate);
                } else {
                    if (contentInfo.getMessageID() <= 0) {
                        contentInfo.setNew(true);
                        listener.onMessageReceive(contentInfo);
                    } else {
                        int position = adapter.modifyDataByMessageId(contentInfo.getMessageID(), contentInfo.getMessageText());
                        if (position >= 0) {
                            adapter.notifyItemChanged(position);
                        }
                    }
                }

            }
        };
        CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
        if (customer != null && customer.getUserID() != null) {
            userId = customer.getUserID();
        }
    }

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    private boolean isImage(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getPath(), options);
        return options.outWidth != -1 && options.outHeight != -1;
    }

    private String getImageUploadUrl(){
        if(BuildConfig.SIT || BuildConfig.UAT){
            return TEST_CUSTOMER_SERVICE_IMAGE_URL_PREFIX;
        }
            return CUSTOMER_SERVICE_IMAGE_URL_PREFIX;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatMessageGetTask != null) {
            chatMessageGetTask.closeRabbitMQ();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_btn:
                if (!inputEdit.getText().toString().trim().isEmpty()) { //只輸入空格符號要無法送出
                    String msg = inputEdit.getText().toString();

                    CustomerServiceChatDetailInfo info = new CustomerServiceChatDetailInfo();
                    info.setDialogue(msg);
                    info.setDialogueTime(chatTime.format(new Date()));
                    if (userId != null) {
                        info.setUserID(Integer.parseInt(userId));
                    }
                    info.setType(CustomerServiceChatDetailInfo.TYPE_SERVICE_CUSTOMER);
                    adapter.addData(info);
                    adapter.notifyDataSetChanged();
                    sendChatInfo(info, inputEdit.getText().toString());
                    inputEdit.setText("");
                }
                break;
            case R.id.upload_btn:
                if (isLockUploadPicture) {
                    Toast.makeText(getContext(), getString(R.string.activity_customer_service_upload_error), Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                i.setType("image/*");
                startActivityForResult(i, PICK_FROM_GALLERY);
                break;
            default:
                break;
        }
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY) {
            if (resultCode == RESULT_OK && data.getData() != null) {
                try {
                    String filePath = PictureUtil.getPath(getContext(), data.getData());
                    File imgFile = new File(filePath);
                    // 確認是否為圖片
                    if (isImage(imgFile)) {
                        // 取得圖片副檔名
                        String extension = getFileExtensionFromUrl(filePath);

                        final String imgName = "upload_" + new Date().getTime() + "." + extension;
                        if (checkIsOverMaxSize(imgFile)) {
                            Toast.makeText(getContext(), "图片大小不能大于3MB", Toast.LENGTH_SHORT).show();
                        } else if (checkIsImageMimeType(imgName)) {
                            final CustomerServiceChatDetailInfo info = new CustomerServiceChatDetailInfo();
                            CustomerInfo customer = CustomerAccountManager.getInstance().getCustomer();
                            if (customer != null) {
                                info.setUserID(Integer.parseInt(customer.getUserID()));
                            }
                            info.setDialogueTime(chatTime.format(new Date()));
                            info.setType(CustomerServiceChatDetailInfo.TYPE_SERVICE_CUSTOMER);
                            info.setImageLoading(true);
                            info.setPictureURL(filePath);
                            info.setImageName(imgName);
                            adapter.addData(info);
                            adapter.notifyItemInserted(0);
                            layoutManager.scrollToPosition(0);
                            int[] widthHeight = PictureUtil.scalePictureSize(PictureUtil.getImageWidthHeight(filePath), getContext());
                            Glide.with(getContext())
                                    .load(filePath).asBitmap()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(new SimpleTarget<Bitmap>(widthHeight[0], widthHeight[1]) {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    String imgStr = BitmapUtil.bmpToByteArrayString(resource);
                                    sendPicture(info, imgName, imgStr);
                                }

                                @Override
                                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                                    super.onLoadFailed(e, errorDrawable);
                                }
                            });

                        } else {
                            Toast.makeText(getContext(), "只能选择JPG/PNG", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "只能选择图片", Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String timeStampConvertToString(String timeString) {
        String t;
        Matcher matcher = pattern.matcher(timeString);

        if (matcher.find()) {
            t = chatTime.format(new Date(Long.parseLong(matcher.group(1))));
        } else {
            t = timeString;
        }
        return t;
    }

    public static String getFileExtensionFromUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            int fragment = url.lastIndexOf('#');
            if (fragment > 0) {
                url = url.substring(0, fragment);
            }

            int query = url.lastIndexOf('?');
            if (query > 0) {
                url = url.substring(0, query);
            }

            int filenamePos = url.lastIndexOf('/');
            String filename =
                    0 <= filenamePos ? url.substring(filenamePos + 1) : url;

            // if the filename contains special characters, we don't
            // consider it valid for our matching purposes:
            if (!TextUtils.isEmpty(filename)) {
                int dotPos = filename.lastIndexOf('.');
                if (0 <= dotPos) {
                    return filename.substring(dotPos + 1);
                }
            }
        }

        return "";
    }

    private void resendImage(final CustomerServiceChatDetailInfo info, String imgStr) {
        info.setImageError(false);
        info.setImageLoading(true);
        info.setDialogueTime("/Date(" + System.currentTimeMillis() + ")/");
        adapter.removeData(info);
        adapter.addData(info);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(0);
        String fileName = info.getImageName() == null? "" : info.getImageName();
        sendPicture(info, fileName, imgStr);
    }

    private void sendPicture(final CustomerServiceChatDetailInfo info, final String fileName, final String imgStr) {
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    isLockUploadPicture = true;
                    Thread.sleep(2000);
                    isLockUploadPicture = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getContext()) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new CustomerService().sendChatPicture(fileName, imgStr);
            }

            @Override
            public void onLoaded(Response paramT) throws Exception {
                info.setImageLoading(false);
                if (CustomerServiceFragment.this == null) {
                    return;
                }
                if (paramT != null) {
                    info.setImageError(false);
                    if (fileName != null) {
                        info.setPictureURL(fileName);
                        info.setImageName(null);
                    }
                } else {
                    isError = true;
                    info.setImageError(true);
                }
                adapter.notifyItemChanged(adapter.getDataIndex(info));
                layoutManager.scrollToPosition(0);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                isError = true;
                info.setImageLoading(false);
                info.setImageError(true);
                adapter.notifyItemChanged(adapter.getDataIndex(info));
                layoutManager.scrollToPosition(0);
            }
        });
        task.executeTask();
    }

    private void sendChatInfo(final CustomerServiceChatDetailInfo info, final String text) {
        MyAsyncTask<Response> task = new MyAsyncTask<Response>(getContext()) {
            @Override
            public Response callService() throws IOException, JsonParseException, BizException, ServiceException {
                return new CustomerService().sendChatMessage(text);
            }

            @Override
            public void onLoaded(Response paramT) throws Exception {
                if (CustomerServiceFragment.this == null) {
                    return;
                }

                if (paramT == null || !paramT.getSuccess()) {
                    isError = true;
                    info.setError(true);
                } else {
                    info.setError(false);
                }
                adapter.notifyItemChanged(adapter.getDataIndex(info));
                layoutManager.scrollToPosition(0);
            }
        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                isError = true;
                info.setError(true);
                adapter.notifyItemChanged(adapter.getDataIndex(info));
                layoutManager.scrollToPosition(0);
            }
        });
        task.executeTask();
    }

    private void getChatInfo(final String date) {
        MyAsyncTask<List<CustomServiceChatInfo>> task = new MyAsyncTask<List<CustomServiceChatInfo>>(getActivity()) {
            @Override
            public List<CustomServiceChatInfo> callService() throws IOException, JsonParseException, BizException, ServiceException {
                if(currentMessageID == 0) {
                    return new CustomerService().getChatInfo(date, true, currentMessageID);
                }else {
                    return new CustomerService().getChatInfo(date, false, currentMessageID);
                }
            }

            @Override
            public void onLoaded(List<CustomServiceChatInfo> paramT) throws Exception {
                if (paramT != null) {
                    chatList = paramT;
                    if (currentMessageID == 0) {
                        dataList.clear();
                        for (CustomServiceChatInfo dateInfo : chatList) {
                            String dateString = null;
                            Long stamp;
                            Matcher matcher = pattern.matcher(dateInfo.getMessageDate());
                            if (matcher.find()) {
                                stamp = Long.parseLong(matcher.group(1)) / 1000;
                                dateString = chatDate.format(new Date(stamp * 1000));
                            }
                            if (dateString != null) {
                                CustomerServiceChatDetailInfo dateDetailInfo = new CustomerServiceChatDetailInfo();
                                dateDetailInfo.setType(CustomerServiceChatDetailInfo.TYPE_DATE);
                                if (dateString.equals(todayDate)) {
                                    dateDetailInfo.setDate("今日");
                                } else {
                                    dateDetailInfo.setDate(dateString);
                                }
                                dataList.add(dateDetailInfo);

                                List<CustomerServiceChatDetailInfo> detailList = dateInfo.getMessage();
                                if (detailList != null) {
                                    for (CustomerServiceChatDetailInfo detailInfo : detailList) {
                                        if (detailInfo.getUserID() == 1) {
                                            detailInfo.setType(CustomerServiceChatDetailInfo.TYPE_SERVICE_AGENT);
                                        } else {
                                            detailInfo.setType(CustomerServiceChatDetailInfo.TYPE_SERVICE_CUSTOMER);
                                        }
                                        dataList.add(detailInfo);
                                        if (detailInfo.getMessageID() > currentMessageID) {
                                            currentMessageID = detailInfo.getMessageID();
                                        }
                                    }
                                }
                            }
                        }
                        adapter.setData(dataList);
                        adapter.notifyDataSetChanged();
                    } else {
                        for (CustomServiceChatInfo dateInfo : chatList) {
                            List<CustomerServiceChatDetailInfo> detailList = dateInfo.getMessage();
                            if (detailList != null) {
                                for (CustomerServiceChatDetailInfo detailInfo : detailList) {
                                    if (detailInfo.getUserID() == 1 && detailInfo.getMessageID() > currentMessageID) {
                                        detailInfo.setType(CustomerServiceChatDetailInfo.TYPE_SERVICE_AGENT);
                                        adapter.addData(detailInfo);
                                        currentMessageID = detailInfo.getMessageID();
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }

        };
        task.setOnError(new MyAsyncTask.OnError() {
            @Override
            public void handleError(Exception paramException) {
                isError = true;
            }
        });
        task.executeTask();
    }

    private boolean checkIsImageMimeType(String imgName) {
        imgName = imgName.toUpperCase();
        return imgName.endsWith(".JPG") || imgName.endsWith(".JPEG") || imgName.endsWith(".PNG")  ||
                imgName.endsWith(".jpg") || imgName.endsWith(".jpeg") || imgName.endsWith(".png");
    }

    private boolean checkIsOverMaxSize (File file) {
        if (file == null) {
            return false;
        }
        long size = file.length();
        return size / 1048576 > MAX_PICTURE_SIZE;
    }

    private void setErrorImageClickListener(final CustomerServiceChatDetailInfo info) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.message_resend_dialog);
        LinearLayout delete = (LinearLayout) dialog.findViewById(R.id.btn_delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeData(info);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        LinearLayout reSend = (LinearLayout) dialog.findViewById(R.id.btn_resend);
        reSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                info.setDialogueTime("/Date(" + System.currentTimeMillis() + ")/");
                adapter.removeData(info);
                adapter.addData(info);
                adapter.notifyDataSetChanged();
                layoutManager.scrollToPosition(0);
                sendChatInfo(info, info.getDialogue());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<CustomerServiceChatDetailInfo> dataList;
        private Context context;

        public MessageAdapter(Context context) {
            this.context = context;
        }

        public class DateViewHolder extends RecyclerView.ViewHolder {
            public TextView date;
            public DateViewHolder(View view) {
                super(view);
                date = (TextView) view.findViewById(R.id.msg_date_text);
            }
        }

        public class AgentViewHolder extends RecyclerView.ViewHolder {
            public TextView time;
            public TextView content;
            public ImageView photo;
            public AgentViewHolder(View view) {
                super(view);
                time = (TextView) view.findViewById(R.id.agent_timestamp_text);
                content = (TextView) view.findViewById(R.id.agent_msg);
                photo = (ImageView) view.findViewById(R.id.agent_img);
            }
        }

        public class CustomerViewHolder extends RecyclerView.ViewHolder {
            public TextView time;
            public TextView content;
            public ImageView error;
            public View imageContainer;
            public ImageView photo;
            public ProgressBar loading;
            public ImageView reload;
            public CustomerViewHolder(View view) {
                super(view);
                time = (TextView) view.findViewById(R.id.customer_timestamp_text);
                content = (TextView) view.findViewById(R.id.customer_msg);
                error = (ImageView) view.findViewById(R.id.error_image);
                imageContainer = view.findViewById(R.id.customer_img_container);
                photo = (ImageView) view.findViewById(R.id.customer_img);
                loading = (ProgressBar) view.findViewById(R.id.loading_progress);
                reload = (ImageView) view.findViewById(R.id.img_upload_status_btn);
            }
        }


        public void setData(List<CustomerServiceChatDetailInfo> data) {
            if (data == null) {
                return;
            }
            if (dataList == null) {
                dataList = new ArrayList<>();
            } else {
                dataList.clear();
            }
            dataList.addAll(data);
            Collections.reverse(dataList);
        }

        public int addData(CustomerServiceChatDetailInfo data) {
            if (dataList == null) {
                dataList = new ArrayList<>();
            }
            dataList.add(0, data);
            return dataList.indexOf(data);
        }

        public void removeData(CustomerServiceChatDetailInfo data) {
            if (dataList == null || data == null) {
                return;
            }
            dataList.remove(data);
        }

        public int modifyDataByMessageId(int message, String content) {
            int position = -1;
            for (CustomerServiceChatDetailInfo info : dataList) {
                if (info.getMessageID() == message) {
                    info.setDialogue(content);
                    position = getDataIndex(info);
                }
            }
            return position;
        }

        public int getDataIndex(CustomerServiceChatDetailInfo info) {
            if (dataList == null) {
                return 0;
            }
            return dataList.indexOf(info);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if (viewType == CustomerServiceChatDetailInfo.TYPE_SERVICE_AGENT) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_service_view_agent, parent, false);
                vh = new AgentViewHolder(v);
            } else if (viewType == CustomerServiceChatDetailInfo.TYPE_SERVICE_CUSTOMER) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_service_view_customer, parent, false);
                vh = new CustomerViewHolder(v);
            } else {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_service_view_date, parent, false);
                vh = new DateViewHolder(v);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            int type = getItemViewType(position);
            final CustomerServiceChatDetailInfo data = dataList.get(position);
            if (type == CustomerServiceChatDetailInfo.TYPE_DATE) {
                DateViewHolder viewHolder = (DateViewHolder) holder;
                viewHolder.date.setText(data.getDate());
            } else if (type == CustomerServiceChatDetailInfo.TYPE_SERVICE_AGENT) {
                final AgentViewHolder viewHolder = (AgentViewHolder) holder;
                if (data.getDialogueTime() != null) {
                    viewHolder.time.setText(timeStampConvertToString(data.getDialogueTime()));
                }
                viewHolder.content.setVisibility(View.GONE);
                viewHolder.photo.setVisibility(View.GONE);
                if (data.getDialogue() != null && !data.getDialogue().equals("")) {
                    viewHolder.content.setVisibility(View.VISIBLE);
                    String content = data.getDialogue().replace("\r", "\n");
                    viewHolder.content.setText(content);
                }
                if (data.getPictureURL() != null && !data.getPictureURL().equals("")) {
                    viewHolder.photo.setVisibility(View.VISIBLE);
                    viewHolder.photo.layout(0,0,0,0);
                    Glide.with(context)
                            .load(getImageUploadUrl() + data.getPictureURL())
                            .asBitmap()
                            .placeholder(R.mipmap.placeholder_pic)
                            .error(R.mipmap.error_pic)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(viewHolder.photo);
                    viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageViewer viewer = new ImageViewer(getContext(), true);
                            Bitmap bitmap = getImageViewBitmap(viewHolder.photo);
                            if (bitmap != null) {
                                viewer.setImage(bitmap);
                                viewer.show();
                            }
                        }
                    });
                }
            }else if (type == CustomerServiceChatDetailInfo.TYPE_SERVICE_CUSTOMER) {
                final CustomerViewHolder viewHolder = (CustomerViewHolder) holder;
                if (data.getDialogueTime() != null) {
                    viewHolder.time.setText(timeStampConvertToString(data.getDialogueTime()));
                }
                if (data.isError()) {
                    viewHolder.error.setVisibility(View.VISIBLE);
                    viewHolder.error.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setErrorImageClickListener(data);
                        }
                    });
                } else {
                    viewHolder.error.setVisibility(View.GONE);
                }
                if (data.getDialogue() != null && !data.getDialogue().equals("")) {
                    viewHolder.content.setVisibility(View.VISIBLE);
                    viewHolder.imageContainer.setVisibility(View.GONE);
                    String content = data.getDialogue().replace("\r", "\n");
                    viewHolder.content.setText(content);
                } else if (data.getPictureURL() != null) {
                    viewHolder.content.setVisibility(View.GONE);
                    viewHolder.imageContainer.setVisibility(View.VISIBLE);
                    viewHolder.photo.layout(0,0,0,0);
                    if (data.getImageName() == null) {
                        Glide.with(context)
                                .load(getImageUploadUrl() + data.getPictureURL())
                                .asBitmap()
                                .placeholder(R.mipmap.placeholder_pic)
                                .error(R.mipmap.error_pic)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(viewHolder.photo);
                    } else {
                        int[] widthHeight = PictureUtil.scalePictureSize(PictureUtil.getImageWidthHeight(data.getPictureURL()), getContext());
                        Glide.with(context)
                                .load(data.getPictureURL())
                                .asBitmap()
                                .override(widthHeight[0], widthHeight[1])
                                .placeholder(R.mipmap.placeholder_pic)
                                .error(R.mipmap.error_pic)
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(viewHolder.photo);
                    }
                    viewHolder.photo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ImageViewer viewer;
                            if (data.getImageName() == null && !data.isImageLoading() && !data.isImageError()) {
                                viewer = new ImageViewer(getContext(), true);
                            } else {
                                viewer = new ImageViewer(getContext(), false);
                            }
                            Bitmap bitmap = getImageViewBitmap(viewHolder.photo);
                            if (bitmap != null) {
                                viewer.setImage(bitmap);
                                viewer.show();
                            }
                        }
                    });
                    if (data.isImageError()) {
                        viewHolder.reload.setVisibility(View.VISIBLE);
                        viewHolder.reload.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Bitmap bitmap = getImageViewBitmap(viewHolder.photo);
                                if (bitmap != null) {
                                    String imgStr = BitmapUtil.bmpToByteArrayString(bitmap);
                                    resendImage(data, imgStr);
                                }
                            }
                        });
                    } else {
                        viewHolder.reload.setVisibility(View.GONE);
                    }
                    if (data.isImageLoading()) {
                        viewHolder.loading.setVisibility(View.VISIBLE);
                    } else {
                        viewHolder.loading.setVisibility(View.GONE);
                    }
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (dataList == null || position >= dataList.size()) {
                return 0;
            }
            return dataList.get(position).getType();
        }

        @Override
        public int getItemCount() {
            if (dataList == null) {
                return 0;
            } else {
                return dataList.size();
            }
        }
    }

    private Bitmap getImageViewBitmap(ImageView view) {
        Bitmap bitmap = null;
        try {
            BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
            if (drawable != null) {
                bitmap = drawable.getBitmap();
            }
        }catch (ClassCastException e) {

        }
        return bitmap;
    }

    public interface OnMessageReceiveListener {
        void onMessageReceive(MessageReceiveInfo.ContentInfoRawData contentInfo);
    }

    private String encodeUrl(String dialogue) {
        Pattern p = Pattern.compile(URL_REGEX);
        Matcher matcher = p.matcher(dialogue);
        if (matcher.find() && matcher.group(0) != null) {
            String matchString = matcher.group(0);
            String result = "";
            try {
                for (int i = 0; i < matchString.length(); i++) {
                    char c  = matchString.charAt(i);
                    if(isChinese(c)) {
                        result += URLEncoder.encode(String.valueOf(c), "UTF-8");
                    } else {
                        result += String.valueOf(c);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (!result.equals("")) {
                dialogue = dialogue.replace(matchString, result);
            }

        }
        return dialogue;
    }
    private boolean isChinese(char c) {
        boolean result = false;
        if (c >= 19968 && c <= 171941) {
            result = true;
        }
        return result;
    }
}

