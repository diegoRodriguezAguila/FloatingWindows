package com.diroag.floatingwindows.sample.floating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.diroag.floatingwindows.sample.R;
import com.diroag.floatingwindows.service.FloatingWindowView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by drodriguez on 09/09/2016.
 * this is a sample of a floating view
 */
public class SampleView extends FloatingWindowView {

    @BindView(R.id.txt_requirement_title)
    protected TextView txtRequirementTitle;

    @BindView(R.id.radio_group_action)
    protected RadioGroup radioGroupAction;
    @BindView(R.id.rbtn_approve)
    protected AppCompatRadioButton rbtnApprove;
    @BindView(R.id.rbtn_reject)
    protected AppCompatRadioButton rbtnReject;

    @BindView(R.id.txt_reject_reason)
    protected TextInputLayout txtRejectReason;
    @BindView(R.id.btn_proceed)
    protected AppCompatButton btnProceed;
    @BindView(R.id.btn_lock)
    protected AppCompatImageButton btnLock;


    private String mRequirementId;
    public PopupWindow.OnDismissListener dismissListener;


    public SampleView(Context context, String requirementId) {
        super(context);
        if (requirementId == null)
            throw new NullPointerException("requirementId can't be null");
        mRequirementId = requirementId;
    }

    /**
     * Put click listeners for radio buttons
     */
    private void setButtonsClickListeners() {
        radioGroupAction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case (R.id.rbtn_approve):
                        btnProceed.setText(R.string.btn_approve);
                        txtRejectReason.setVisibility(View.GONE);
                        break;
                    case (R.id.rbtn_reject):
                        btnProceed.setText(R.string.btn_reject);
                        txtRejectReason.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    @SuppressLint("InflateParams")
    @Override
    public
    @NonNull
    View onCreateView(LayoutInflater layoutInflater) {
        View view = layoutInflater.inflate(
                R.layout.sample_floating_view, null, false);
        ButterKnife.bind(this, view);
        setButtonsClickListeners();
        txtRequirementTitle.setText(mRequirementId);
        return view;
    }

    @OnClick(R.id.btn_cancel)
    public void btnCancelClick(View v) {
        dismiss();
    }

    @OnClick(R.id.btn_proceed)
    public void btnProceedClick(View v) {
        dismiss();
    }

    @OnClick(R.id.btn_close)
    public void btnCloseClick(View v) {
        dismiss();
    }

    @OnClick(R.id.btn_lock)
    public void btnLockClick(View v) {
        if (isLocked()) {
            unlockPosition();
            btnLock.setImageResource(R.drawable.unlock);
            Toast.makeText(getContext(), "Unlocked!", Toast.LENGTH_SHORT).show();
            return;
        }
        lockPosition();
        btnLock.setImageResource(R.drawable.lock);
        Toast.makeText(getContext(), "Locked!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }
}