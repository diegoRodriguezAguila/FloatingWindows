package com.diroag.floatingwindows.sample.floating;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.TextInputLayout;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.diroag.floatingwindows.R;
import com.diroag.floatingwindows.service.AbstractFloatingWindowView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by drodriguez on 09/09/2016.
 * this is a sample of a floating view
 */
public class SampleView extends AbstractFloatingWindowView {
    private View mRootView;
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

    @BindView(R.id.btn_cancel)
    protected AppCompatButton btnCancel;
    @BindView(R.id.btn_proceed)
    protected AppCompatButton btnProceed;


    public SampleView(Context context, String requirementId) {
        super(context);
        if (requirementId == null)
            throw new NullPointerException("requirementId can't be null");
        inflateViews();
        txtRequirementTitle.setText(requirementId);
    }

    @SuppressLint("InflateParams")
    private void inflateViews() {
        mRootView = LayoutInflater.from(new ContextThemeWrapper(getContext(),
                R.style.AppTheme)).inflate(
                R.layout.sample_floating_view, null, false);
        ButterKnife.bind(this, mRootView);
        setButtonsClickListeners();
        setFontToAppcompatViews();
    }

    /**
     * Pone la custom font a las vistas appcompat a las cuales no se les aplica por alguna razon
     * la fuente de forma automática
     */
    private void setFontToAppcompatViews() {
        /*Typeface font = getContext().getAssets().get TypefaceUtils.load(getContext().getAssets(),
                "fonts/helvetica_neue_roman.otf");
        if (txtRejectReason.getEditText() != null)
            txtRejectReason.getEditText().setTypeface(font);
        txtRejectReason.setTypeface(font);
        rbtnApprove.setTypeface(font);
        rbtnReject.setTypeface(font);
        btnCancel.setTypeface(font);
        btnProceed.setTypeface(font);*/
    }

    /**
     * Pone los click listeners se los botones
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
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    getService().exit();
            }
        });
        btnProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public View getRootView() {
        return mRootView;
    }
}