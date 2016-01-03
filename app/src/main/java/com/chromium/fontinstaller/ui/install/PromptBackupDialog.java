package com.chromium.fontinstaller.ui.install;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.chromium.fontinstaller.Injector;
import com.chromium.fontinstaller.R;
import com.chromium.fontinstaller.core.FontsterPreferences;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.functions.Action1;

import static com.chromium.fontinstaller.core.FontsterPreferences.Key;

public class PromptBackupDialog extends AlertDialog {

  @Bind(R.id.dont_show_again)
  CheckBox mDontShowAgain;

  @Inject
  FontsterPreferences mPreferences;

  private final Action1<Boolean> mCallback;

  public PromptBackupDialog(Context context, Action1<Boolean> callback) {
    super(context);
    Injector.get().inject(this);
    mCallback = callback;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    final View view = View.inflate(getContext(), R.layout.prompt_backup_dialog, null);
    ButterKnife.bind(this, view);
    setView(view);

    mDontShowAgain.setOnCheckedChangeListener((buttonView, isChecked) ->
        mPreferences.putBoolean(Key.DISABLE_PROMPT_TO_BACKUP, isChecked));

    setButton(BUTTON_POSITIVE, getContext().getString(R.string.yes), (dialog, which) -> {
      dialog.dismiss();
      mCallback.call(true);
    });

    setButton(BUTTON_NEGATIVE, getContext().getString(R.string.no), (dialog, which) -> {
      dialog.dismiss();
      mCallback.call(false);
    });

    super.onCreate(savedInstanceState);
  }
}
