package com.testapp.fitspirational.FeatureActivities.Breathe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.florent37.viewanimator.AnimationListener;
import com.github.florent37.viewanimator.ViewAnimator;
import com.testapp.fitspirational.R;

public class Breathe extends AppCompatActivity {

    private ImageView lotusImage;
    private TextView breatheText;
    private Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breathe);

        lotusImage = findViewById(R.id.breathe_image);
        breatheText = findViewById(R.id.breathe_breatheText);
        startBtn = findViewById(R.id.breathe_startBtn);

        startIntroAnimation();

        startBtn.setOnClickListener((v) -> {
            startAnimation();
        });
    }

    private void startIntroAnimation() {
        ViewAnimator
                .animate(breatheText)
                .scale(0,1)
                .duration(1500)
                .onStart(() -> {
                    breatheText.setText("Breathe");
                }).start();
    }

    private void startAnimation() {
        ViewAnimator
                .animate(lotusImage)
                //.alpha(0,1)
                .onStart(() -> {
                    breatheText.setText("Inhale... Exhale");
                })
                .decelerate()
                .duration(8000)
                .thenAnimate(lotusImage)
                .scale(0.2f, 1.0f, 0.2f)
                .rotation(360)
                .repeatCount(6)
                .duration(8000)
                .onStop(() -> {
                    breatheText.setText("Good Job!");
                    lotusImage.setScaleX(1.0f);
                    lotusImage.setScaleY(1.0f);
                })
                .start();

        ViewAnimator
                .animate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 1 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 2 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 3 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 4 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 5 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 6 cycle
                .thenAnimate(breatheText)
                .scale(0.8f,1.2f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Inhale...");
                })
                .thenAnimate(breatheText)
                .scale(1.2f,0.8f)
                .duration(4000)
                .onStart(() -> {
                    breatheText.setText("Exhale...");
                }) // 7 cycle
                .onStop(() -> {
                    breatheText.setScaleX(1.0f);
                    breatheText.setScaleY(1.0f);
                })
                .start();

    }
}