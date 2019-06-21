package pl.gorczyca.compassnavigation;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class EditTextDialog {

    static final int INPUT_LATITUDE = 0;
    static final int INPUT_LONGITUDE = 1;

    AlertDialog getInputDialog(Context context, int whichInput) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
        final View viewEdit = inflater.inflate(R.layout.dialog_input, null);
        final EditText editText = viewEdit.findViewById(R.id.editTextDialog);

        switch (whichInput){
            case INPUT_LATITUDE:
                editText.setHint(R.string.latitude_hint);
                break;
            case INPUT_LONGITUDE:
                editText.setHint(R.string.longitude_hint);
                break;
        }

        builder.setView(viewEdit)
                .setPositiveButton(inflater.getContext().getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setNegativeButton(inflater.getContext().getResources().getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        final AlertDialog dialog = builder.create();
        dialog.show();

        return dialog;


    }
}
