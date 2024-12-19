package aaadddfgh.myapp.AItiaotiao.ui.log;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import aaadddfgh.myapp.AItiaotiao.R;
import aaadddfgh.myapp.AItiaotiao.storage.LogFile;


public class LogViewerFragment extends Fragment {

    private LogViewerViewModel mViewModel;

    public static LogViewerFragment newInstance() {
        return new LogViewerFragment();
    }
    private TextView logTextView;
    private LogViewerViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.log_viewer_fragment, container, false);


        return view;
    }

    public void updateLog(String newText) {
        viewModel.setLogText(newText);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logTextView = getView().findViewById(R.id.log_text_view);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(LogViewerViewModel.class);
        viewModel.getLogText().observe(getViewLifecycleOwner(), logText -> {
            if (logText != null) {
                logTextView.setText(logText);
            }
        });
        try {
            viewModel.setLogText(
                    new LogFile(getContext()).readLog()
            );
        }
        catch (Exception e){

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LogViewerViewModel.class);

    }

}