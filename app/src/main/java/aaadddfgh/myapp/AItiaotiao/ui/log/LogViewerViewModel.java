package aaadddfgh.myapp.AItiaotiao.ui.log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LogViewerViewModel extends ViewModel {
    private MutableLiveData<String> logText = new MutableLiveData<>();

    public LogViewerViewModel(){

    }

    public LiveData<String> getLogText() {
        return logText;
    }

    public void setLogText(String text) {
        logText.setValue(text);
    }

}