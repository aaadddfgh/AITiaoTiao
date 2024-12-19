package aaadddfgh.myapp.AItiaotiao.storage;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

public class LogFile {
    private static final String TAG = "LogFile";
    private Context context;
    private File logFile;
    private Timer timer = new Timer();
    private static boolean isWriting = false;


    public LogFile(Context context) {
        this.context = context;
        logFile = new File(context.getFilesDir(), "log.txt");
    }

    public void deleteLog() {
        if (logFile.exists()) {
            boolean isDeleted = logFile.delete();
            if (isDeleted) {
                System.out.println("Log file deleted successfully.");
            } else {
                System.out.println("Failed to delete log file.");
            }
        } else {
            System.out.println("Log file does not exist.");
        }
    }

    public synchronized void writeLog(final String message) {
        if (isWriting) {
            // If already writing, reschedule the task

        } else {
            Log.d(TAG, "Writing " + LocalDateTime.now().toString());
            // Start a new write process
            isWriting = true;
            scheduleWrite(message);
        }
    }

    private void scheduleWrite(final String message) {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try (FileWriter writer = new FileWriter(logFile, true)) {
                    writer.append(message).append("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isWriting = false;
                }
            }
        }, 3000); // 500 milliseconds delay
    }

    private void cancelTimer() {
        timer.cancel();
        timer = new Timer(); // Create a new timer to avoid race conditions
    }

    public String readLog() {
        StringBuilder logBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                logBuilder.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logBuilder.toString();
    }
}