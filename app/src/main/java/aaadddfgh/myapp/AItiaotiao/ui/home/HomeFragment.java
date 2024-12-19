package aaadddfgh.myapp.AItiaotiao.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import aaadddfgh.myapp.AItiaotiao.R;
import aaadddfgh.myapp.AItiaotiao.service.AccessibilityServiceManager;
import aaadddfgh.myapp.AItiaotiao.lib.AccessibilityNodeInfoTextConverter;
import aaadddfgh.myapp.AItiaotiao.storage.LogFile;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private Button button;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });




        button=root.findViewById(R.id.button);
        button.setOnClickListener(view -> new Thread(() -> {
            try {
                Thread.sleep(2000);
                // 使线程休眠1秒钟（1000毫秒）
                AccessibilityServiceManager service = AccessibilityServiceManager.getInstance();

                String s = AccessibilityNodeInfoTextConverter.convert(service.getRootInActiveWindow());
                Long l =1L;
            } catch (InterruptedException e) {
                // 处理线程被中断的异常
                e.printStackTrace();
            }
        }).start());

        root.findViewById(R.id.del_log).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        new LogFile(getContext()).deleteLog();

                    }
                }
        );


        return root;
    }



}