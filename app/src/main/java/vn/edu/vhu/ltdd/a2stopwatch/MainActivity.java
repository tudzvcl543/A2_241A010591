package vn.edu.vhu.ltdd.a2stopwatch;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String TAG = "A2_241A010591";
    private static final String KEY_RUNNING = "running";
    private static final String KEY_ACCUMULATED = "accumulated";
    private static final String KEY_START = "startTime";
    private static final String KEY_RECREATE = "recreateCount";
    private static final String KEY_LAPS = "laps";
    private static final String KEY_PAUSE_ON_BACKGROUND = "pauseOnBackground";

    private TextView tvTime, tvStatus, tvRecreate, tvLaps;
    private Button btnStartPause, btnLap;
    private CheckBox checkPauseOnBackground;
    private boolean running;
    private boolean resumed;
    private long accumulated;
    private long startTime;
    private int recreateCount;
    private ArrayList<String> laps = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override public void run() {
            updateTimeText();
            handler.postDelayed(this, 100L);
        }
    };

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvRecreate = findViewById(R.id.tvRecreate);
        tvLaps = findViewById(R.id.tvLaps);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnLap = findViewById(R.id.btnLap);
        checkPauseOnBackground = findViewById(R.id.checkPauseOnBackground);

        if (state != null) {
            running = state.getBoolean(KEY_RUNNING);
            accumulated = state.getLong(KEY_ACCUMULATED);
            startTime = state.getLong(KEY_START);
            recreateCount = state.getInt(KEY_RECREATE) + 1;
            ArrayList<String> savedLaps = state.getStringArrayList(KEY_LAPS);
            if (savedLaps != null) laps = savedLaps;
            checkPauseOnBackground.setChecked(state.getBoolean(KEY_PAUSE_ON_BACKGROUND));
            Log.d(TAG, "onCreate: KHÔI PHỤC trạng thái, running=" + running
                    + ", elapsed=" + elapsed() + "ms");
        } else {
            Log.d(TAG, "onCreate: khởi tạo mới (savedInstanceState = null)");
        }

        btnStartPause.setOnClickListener(v -> {
            if (running) pauseStopwatch(); else startStopwatch();
        });
        findViewById(R.id.btnReset).setOnClickListener(v -> resetStopwatch());
        btnLap.setOnClickListener(v -> recordLap());
        updateUi();
    }

    private long elapsed() {
        return running ? accumulated + SystemClock.elapsedRealtime() - startTime : accumulated;
    }

    private void startStopwatch() {
        if (running) return;
        startTime = SystemClock.elapsedRealtime();
        running = true;
        if (resumed) startTicking();
        updateUi();
        Log.i(TAG, "BẮT ĐẦU đếm giờ");
    }

    private void pauseStopwatch() {
        if (!running) return;
        accumulated = elapsed();
        running = false;
        stopTicking();
        updateUi();
        Log.i(TAG, "TẠM DỪNG tại " + accumulated + "ms");
    }

    private void resetStopwatch() {
        stopTicking();
        running = false;
        accumulated = 0L;
        startTime = 0L;
        laps.clear();
        updateUi();
        Log.i(TAG, "ĐẶT LẠI về 00:00.0");
    }

    private void recordLap() {
        if (!running) return;
        laps.add(formatTime(elapsed()));
        updateLaps();
        Log.i(TAG, "GHI VÒNG số " + laps.size());
    }

    private void startTicking() {
        handler.removeCallbacks(ticker);
        handler.post(ticker);
    }

    private void stopTicking() {
        handler.removeCallbacks(ticker);
    }

    private String formatTime(long ms) {
        return String.format(Locale.getDefault(), "%02d:%02d.%d",
                ms / 60000L, (ms / 1000L) % 60L, (ms / 100L) % 10L);
    }

    private void updateTimeText() {
        tvTime.setText(formatTime(elapsed()));
    }

    private void updateLaps() {
        if (laps.isEmpty()) {
            tvLaps.setText(R.string.no_laps);
            return;
        }
        StringBuilder text = new StringBuilder();
        for (int i = laps.size() - 1; i >= 0; i--) {
            if (text.length() > 0) text.append('\n');
            text.append(getString(R.string.lap_item, i + 1, laps.get(i)));
        }
        tvLaps.setText(text);
    }

    private void updateUi() {
        updateTimeText();
        tvStatus.setText(running ? R.string.status_running : R.string.status_paused);
        btnStartPause.setText(running ? R.string.pause : R.string.start);
        btnLap.setEnabled(running);
        btnLap.setAlpha(running ? 1f : 0.5f);
        tvRecreate.setText(getString(R.string.recreate_count, recreateCount));
        updateLaps();
    }

    @Override protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override protected void onResume() {
        super.onResume();
        resumed = true;
        if (running) startTicking();
        updateUi();
        Log.d(TAG, "onResume: cập nhật lại giao diện");
    }

    @Override protected void onPause() {
        resumed = false;
        stopTicking();
        Log.d(TAG, "onPause: dừng ticker");
        super.onPause();
    }

    @Override protected void onStop() {
        // Chỉ tạm dừng đồng hồ nếu người dùng đã bật chế độ này.
        if (!isChangingConfigurations() && checkPauseOnBackground.isChecked() && running) {
            pauseStopwatch();
        }
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RUNNING, running);
        outState.putLong(KEY_ACCUMULATED, accumulated);
        outState.putLong(KEY_START, startTime);
        outState.putInt(KEY_RECREATE, recreateCount);
        outState.putStringArrayList(KEY_LAPS, new ArrayList<>(laps));
        outState.putBoolean(KEY_PAUSE_ON_BACKGROUND, checkPauseOnBackground.isChecked());
        Log.d(TAG, "onSaveInstanceState: đã lưu " + elapsed() + "ms vào Bundle");
    }

    @Override protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);
        Log.d(TAG, "onRestoreInstanceState: gọi sau onStart");
    }

    @Override protected void onDestroy() {
        stopTicking();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
