package com.commonsware.empublite;


import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class NoteFragment extends Fragment {
    private static final String KEY_POSITION = "position";
    private EditText editor = null;
    private final String TAG = getClass().getSimpleName();


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

        if (TextUtils.isEmpty(editor.getText())){
            DatabaseHelper db = DatabaseHelper.getInstance(getActivity());
            Log.d(TAG, "onStart done loading data");
            db.loadNote(getPosition());
        }

    }

    public NoteFragment() {
        // Required empty public constructor
    }

    static NoteFragment newInstance(int position){
        NoteFragment frag = new NoteFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_POSITION, position);
        frag.setArguments(args);
        Log.d("NoteFragment", "calling newInstance()");
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View result = inflater.inflate(R.layout.editor, container, false);
        editor = (EditText) result.findViewById(R.id.editor);
        Log.d(TAG, "onCreateView()");
        return result;
    }

    private int getPosition(){
        return getArguments().getInt(KEY_POSITION, -1);
    }


    @Override
    public void onStop(){
        DatabaseHelper.getInstance(getActivity())
        .updateNote( getPosition(), editor.getText().toString());

        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNoteLoaded(NoteLoadedEvent event){
        if (event.getPosition() == getPosition()){
            editor.setText(event.getProse());
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.notes, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.delete){
            editor.setText(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
